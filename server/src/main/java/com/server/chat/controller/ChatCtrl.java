package com.server.chat.controller;

import com.server.chat.entity.*;
import com.server.chat.service.ChatServiceImpl;
import com.server.chat.service.Producer;
import com.server.member.entity.MemberDAO;
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

    @PostMapping("/getChatList")
    public List<ChatMessageVO> getChatList(@RequestBody EnterChatDAO enterChatDAO) throws Exception {

        String userId = enterChatDAO.getUserid();
        int roomId = enterChatDAO.getRoomid();
        EnterChatDAO userInfo = chatService.getEnterChatInfo(roomId, userId);
        if(userInfo == null) {
            return null;
        }

        return chatService.getChatList(roomId, userId);

    }

    @PostMapping("/getEnterId")
    public int getEnterId(@RequestBody EnterChatDAO enterChatDAO) throws Exception {

        EnterChatDAO checkUserDAO = chatService.checkUserEnterRoom(enterChatDAO.getUserid(), enterChatDAO.getRoomid());
        return checkUserDAO.getId();

    }

    @PostMapping("/outRoom")
    public boolean outRoom(@RequestBody ChatMessageVO chatMessageVO) throws Exception {

        int enterId = chatMessageVO.getId();

        chatService.outRoom(enterId);

        String message = chatMessageVO.getSendernm() + " 님이 퇴장했습니다.";
        String messageType = "out";

        producer.send(topicNm, enterId, message, messageType);

        return true;

    }

    @PostMapping("/chat/send")
    /*public void send(@RequestParam("message") String message, @RequestParam("roomId") int roomId, @RequestParam("enterId") int enterId) throws Exception {*/
    public void send(@RequestBody ChatMessageVO chatMessageVO) throws Exception {
        int enterId = chatMessageVO.getId();
        int roomId = chatMessageVO.getRoomid();
        String message = chatMessageVO.getMessage();

        String messageType = "text";
        String userId = chatMessageVO.getSenderid();
        String userNm = chatMessageVO.getSendernm();

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

    @PostMapping(value = "/enterRoom")
    public boolean enterRoom(@RequestBody ChatMessageVO chatMessageVO) throws Exception {
        String userId = chatMessageVO.getSenderid();
        String userNm = chatMessageVO.getSendernm();
        int roomId = chatMessageVO.getRoomid();

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

        return true;

    }

    @PostMapping(value = "/createRoom")
    public int createRoomPro(@RequestBody UserChatVO userChatVO) throws Exception {
        ChatRoomDAO chatRoomDAO = new ChatRoomDAO();
        chatRoomDAO.setRoomnm(userChatVO.getRoomnm());
        chatService.createRoom(chatRoomDAO);

        int roomId = chatRoomDAO.getId();
        EnterChatDAO enterChatDAO = new EnterChatDAO();
        enterChatDAO.setUserid(userChatVO.getUserid());
        enterChatDAO.setRoomid(roomId);
        enterChatDAO.setChatoffset(0);
        chatService.enterRoom(enterChatDAO);

        return roomId;
    }

    @PostMapping(value="/getMyChatList")
    public List<UserChatVO> getMyChatList(@RequestBody MemberDAO memberDAO) throws Exception {
        return chatService.getUserChatRoom(memberDAO.getId());
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