package com.server.chat.controller;

import com.server.chat.domain.*;
import com.server.chat.service.ChatServiceImpl;
import com.server.chat.service.Producer;
import com.server.file.domain.FileDTO;
import com.server.member.domain.MemberDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
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

        producer.send(topicNm, enterId, message, messageType, "-");

        return true;


    }

    @PostMapping("/chat/send")
    public ResponseEntity<?> send(@RequestBody ChatMessageVO chatMessageVO) throws Exception {
        int enterId = chatMessageVO.getId();
        int roomId = chatMessageVO.getRoomid();
        String message = chatMessageVO.getMessage();
        String messageType = "text";
        String userId = chatMessageVO.getSenderid();
        String userNm = chatMessageVO.getSendernm();
        try {
            producer.send(topicNm, enterId, message, messageType, "-");
            chatService.webMessage(userId, userNm, roomId, null, message, messageType);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending message");
        }
    }

    @PostMapping("/chat/sendFile")
    public boolean sendFile(@RequestParam("roomId") int roomId, @RequestParam("enterId") int enterId, @RequestParam("userId") String userId, @RequestParam("userNm") String userNm, @RequestParam("uploadFile") MultipartFile uploadFiles, HttpServletRequest request) throws Exception {

        if(uploadFiles != null) {

            String realPath = "C:\\Uploads" + File.separator;

            String roomFolder = userId + File.separator + roomId;

            File uploadPath = new File(realPath, roomFolder);
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
            fileDTO.setSaveFolder(roomFolder);

            String fileType = uploadFiles.getContentType();
            String[] fileTypeArr = fileType.split("/");
            fileDTO.setFileType(fileTypeArr[0]);

            fileDTO.setOriginName(originalFilename);
            fileDTO.setSaveName(uploadFilename);

            uploadFiles.transferTo(new File(uploadPath, uploadFilename));     // 서버에 파일 업로드 수행
            chatService.saveFileInfo(fileDTO);

            /*
            String message = "";
            String innerMessage = "";
            if(fileTypeArr[0].equals("image")) {
                innerMessage = "<img src='/util/upload/" + userId + File.separator + roomId + File.separator + uploadFilename + "' />";
            } else {
                innerMessage = originalFilename;
            }

            message = "<a href='/util/download?id=" + fid + "' download>" + innerMessage + "</a>";
            String messageType = "upload";
            */

            String message = "";
            String innerMessage = "";
            String messageType = "";
            if(fileTypeArr[0].equals("image")) {
                messageType = "upload_img";
                message = uploadFilename;
            } else {
                messageType = "upload";
                message = originalFilename;
            }

            //message = "<a href='/util/download?id=" + fid + "' download>" + innerMessage + "</a>";


            producer.send(topicNm, enterId, message, messageType, fid);
            chatService.webMessage(userId, userNm, roomId, fid, message, messageType);

            return true;

        }

        return false;

    }

    @PostMapping("/chat/sendDraw")
    public boolean sendDraw(@RequestParam("roomId") int roomId, @RequestParam("enterId") int enterId, @RequestParam("userId") String userId, @RequestParam("userNm") String userNm, @RequestParam("imageUrl") String imageUrl, HttpServletRequest request) throws Exception {

        if(imageUrl != null) {

            try {

                String[] parts = imageUrl.split(",");
                String imageString = parts[1];
                byte[] imageBytes = Base64.getDecoder().decode(imageString);

                SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
                Date date = new Date();
                String dateFormat = sdf.format(date);

                UUID uuid = UUID.randomUUID();
                String fid = uuid.toString();
                String fileName = fid + "_" + dateFormat + ".png";

                String realPath = "C:\\Uploads" + File.separator;
                String roomFolder = "drawing" + File.separator + roomId;

                String uploadPath = realPath + File.separator + roomFolder + File.separator + fileName;

                FileDTO fileDTO = new FileDTO();
                fileDTO.setId(fid);
                fileDTO.setSaveFolder(roomFolder);
                fileDTO.setFileType("canvas");
                fileDTO.setOriginName(fileName);
                fileDTO.setSaveName(fileName);
                chatService.saveFileInfo(fileDTO);

                FileUtils.writeByteArrayToFile(new File(uploadPath), imageBytes);

                String messageType = "canvas";

                producer.send(topicNm, enterId, fileName, messageType, fid);
                chatService.webMessage(userId, userNm, roomId, fid, fileName, messageType);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

        }

        return false;

    }

    @PostMapping(value = "/enterRoom")
    public boolean enterRoom(@RequestBody ChatMessageVO chatMessageVO) throws Exception {
        String userId = chatMessageVO.getSenderid();
        String userNm = chatMessageVO.getSendernm();
        int roomId = chatMessageVO.getRoomid();

        ChatRoomDAO chatRoomDAO = chatService.getChatRoomInfo(roomId);

        if(!chatRoomDAO.isStatus()) {
            return false;
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
            chatService.updateEnterRoomStatus(checkUserDAO.getId(), chatService.getChatLastNum(roomId) + 1);
        }

        EnterChatDAO userInfo = chatService.getEnterChatInfo(roomId, userId);

        String message = userNm + " 님이 입장했습니다.";
        String messageType = "enter";

        producer.send(topicNm, userInfo.getId(), message, messageType, "-");
        chatService.webMessage(userId, userNm, roomId, null, message, messageType);

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

    @GetMapping("/download")
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