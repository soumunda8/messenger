package com.server.chat.controller;

import com.server.chat.entity.*;
import com.server.chat.service.ChatServiceImpl;
import com.server.chat.service.Producer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.UUID;

@RestController
//@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/api")
public class ChatCtrl {

    private final Producer producer;

    @Autowired
    HttpSession session;

    @Value("${spring.kafka.template.default-topic}")
    private String topicNm;

    @Autowired
    private ChatServiceImpl chatService;

    @GetMapping("/chat/{roomId}")
    public String chatHome(@PathVariable(required = false) int roomId, Model model) throws Exception {

        String userId = (String) session.getAttribute("userId");
        if(userId == null || userId.isEmpty()) {
            return "redirect:/";
        } else {
            EnterChatDAO userInfo = chatService.getEnterChatInfo(roomId, userId);
            if(userInfo == null) {
                return "redirect:/enter";
            } else {
                model.addAttribute("enterId", userInfo.getId());
            }

            List<ChatMessageVO> chatList = chatService.getChatList(roomId, userId);
            model.addAttribute("chatList", chatList);
            model.addAttribute("roomId", roomId);
            return "chat";
        }

    }

    @GetMapping("/outRoom/{enterId}")
    public String outRoom(@PathVariable(required = false) int enterId) throws Exception {

        chatService.outRoom(enterId);

        String message = session.getAttribute("userNm") + " 님이 퇴장했습니다.";
        String messageType = "out";

        producer.send(topicNm, enterId, message, messageType);

        return "redirect:/enter";

    }

    @PostMapping("/chat/send")
    @ResponseBody
    public void send(@RequestParam("message") String message, @RequestParam("roomId") int roomId, @RequestParam("enterId") int enterId) throws Exception {

        String messageType = "text";
        String userId = (String) session.getAttribute("userId");
        String userNm = (String) session.getAttribute("userNm");

        producer.send(topicNm, enterId, message, messageType);
        chatService.webMessage(userId, userNm, roomId, message, messageType);

    }

    @PostMapping("/chat/sendFile")
    @ResponseBody
    public boolean sendFile(@RequestParam("roomId") int roomId, @RequestParam("enterId") int enterId, @RequestParam("uploadFile") MultipartFile uploadFiles, HttpServletRequest request) throws Exception {

        String userId = (String) session.getAttribute("userId");
        String userNm = (String) session.getAttribute("userNm");

        if(uploadFiles != null) {

            String realPath = "C:\\Uploads" + File.separator;

            String dateFolder = userId + File.separator + roomId;

            File uploadPath = new File(realPath, dateFolder);
            if(!uploadPath.exists()) {
                uploadPath.mkdirs();
            }

            String originalFilename = uploadFiles.getOriginalFilename();
            UUID uuid = UUID.randomUUID();
            //String uploadFilename = URLEncoder.encode(uuid.toString() + "_" + originalFilename, "UTF-8");
            String uploadFilename = uuid.toString() + "_" + originalFilename;
            String fid = uuid.toString();

            FileDTO fileDTO = new FileDTO();
            fileDTO.setId(fid);
            fileDTO.setSaveFolder(dateFolder);

            String fileType = uploadFiles.getContentType();
            String[] fileTypeArr = fileType.split("/");
            fileDTO.setFileType(fileTypeArr[0]);

            fileDTO.setOriginName(originalFilename);
            fileDTO.setSaveName(uploadFilename);

            uploadFiles.transferTo(new File(uploadPath, uploadFilename));     // 서버에 파일 업로드 수행
            chatService.saveFileInfo(fileDTO);

            String message = "";
            String innerMessage = "";
            if(fileTypeArr[0].equals("image")) {
                innerMessage = "<img src='/upload/" + userId + File.separator + roomId + File.separator + uploadFilename + "' />";
            } else {
                innerMessage = originalFilename;
            }

            message = "<a href='/util/download?id=" + fid + "'>" + innerMessage + "</a>";
            String messageType = "upload";

            producer.send(topicNm, enterId, message, messageType);
            chatService.webMessage(userId, userNm, roomId, message, messageType);

            return true;

        }

        return false;

    }

    @RequestMapping(value="/enterRoom", method=RequestMethod.POST)
    public ResponseEntity<String> enterRoom(@RequestParam("roomId") int roomId) throws Exception {
        String userId = (String) session.getAttribute("userId");
        String userNm = (String) session.getAttribute("userNm");

        String result = "false";

        if(session.getAttribute("userId") == null) {
            result = "null";
        }

        EnterChatDAO checkUserDAO = chatService.checkUserEnterRoom(userId, roomId);

        if(checkUserDAO == null) {

            int offsetNum = 1;
            if(chatService.getChatLastNum(roomId) != null) {
                offsetNum = chatService.getChatLastNum(roomId) + 1;
            }

            EnterChatDAO insertEnterDAO = new EnterChatDAO();
            insertEnterDAO.setUserid(userId);
            insertEnterDAO.setRoomid(roomId);
            insertEnterDAO.setChatoffset(offsetNum);
            chatService.enterRoom(insertEnterDAO);

        } else if(!checkUserDAO.isStatus()) {
            chatService.updateRoomStatus(checkUserDAO.getId(), chatService.getChatLastNum(roomId) + 1);
        }

        EnterChatDAO userInfo = chatService.getEnterChatInfo(roomId, userId);

        String message = session.getAttribute("userNm") + " 님이 입장했습니다.";
        String messageType = "enter";

        producer.send(topicNm, userInfo.getId(), message, messageType);
        chatService.webMessage(userId, userNm, roomId, message, messageType);

        result = "true";

        return ResponseEntity.ok(result);

    }

    @GetMapping("/create")
    public String createRoom() throws Exception {
        return "create";
    }

    @RequestMapping(value="/createRoom", method=RequestMethod.POST)
    public ResponseEntity createRoomPro(@RequestParam("roomNm") String roomNm) throws Exception {
        String userId = (String) session.getAttribute("userId");

        ChatRoomDAO chatRoomDAO = new ChatRoomDAO();
        chatRoomDAO.setRoomnm(roomNm);
        chatService.createRoom(chatRoomDAO);

        int roomId = chatRoomDAO.getId();
        EnterChatDAO enterChatDAO = new EnterChatDAO();
        enterChatDAO.setUserid(userId);
        enterChatDAO.setRoomid(roomId);
        enterChatDAO.setChatoffset(0);
        chatService.enterRoom(enterChatDAO);

        return new ResponseEntity<>(roomId, HttpStatus.OK);
    }

    @RequestMapping(value="/util/getMyChatList", method=RequestMethod.POST)
    public ResponseEntity getMyChatList() throws Exception {
        String userId = (String) session.getAttribute("userId");
        List<UserChatVO> chatListForLeft = chatService.getUserChatRoom(userId);
        return new ResponseEntity<>(chatListForLeft, HttpStatus.OK);
    }

    @GetMapping("/util/download")
    public void fileDownload(@RequestParam String id, HttpServletRequest request, HttpServletResponse response) throws Exception {
        FileDTO files = chatService.getFileInfo(id);

        String realPath = "C:\\Uploads" + File.separator;
        
        String saveFolder = realPath + files.getSaveFolder();
        String originalFile = files.getOriginName();
        String saveFile = files.getSaveName();
        File file = new File(saveFolder, saveFile);

        response.setContentType("apllication/download; charset=UTF-8");
        response.setContentLength((int) file.length());

        String header = request.getHeader("User-Agent");
        boolean isIE = header.indexOf("MSIE") > -1 || header.indexOf("Trident") > -1;
        String fileName = null;
        // IE는 다르게 처리
        if (isIE) {
            fileName = URLEncoder.encode(originalFile, "UTF-8").replaceAll("\\+", "%20");
        } else {
            fileName = new String(originalFile.getBytes("UTF-8"), "ISO-8859-1");
        }
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\";");
        response.setHeader("Content-Transfer-Encoding", "binary");
        OutputStream out = response.getOutputStream();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            FileCopyUtils.copy(fis, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            out.close();
        }

    }

}