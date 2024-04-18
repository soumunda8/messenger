package com.server.chat.service;

import com.server.chat.entity.*;
import com.server.chat.persistence.ChatMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatMapper chatMapper;

    private final SimpMessagingTemplate msgOperation;

    @Override
    public List<UserChatVO> getUserChatRoom(String userId) throws Exception {
        return chatMapper.getUserChatRoom(userId);
    }

    @Override
    public EnterChatDAO getEnterChatInfo(int roomId, String userId) throws Exception {
        EnterChatDAO enterChatDAO = new EnterChatDAO();
        enterChatDAO.setUserid(userId);
        enterChatDAO.setRoomid(roomId);
        return chatMapper.getEnterChatInfo(enterChatDAO);
    }

    @Override
    public List<ChatMessageVO> getChatList(int roomId, String senderId) throws Exception {
        return chatMapper.getChatList(roomId, senderId);
    }

    @Override
    public Integer getChatLastNum(int roomId) throws Exception {
        return chatMapper.getChatLastNum(roomId);
    }

    @Override
    public void enterRoom(EnterChatDAO enterChatDAO) throws Exception {
        chatMapper.enterRoom(enterChatDAO);
    }

    @Override
    public EnterChatDAO checkUserEnterRoom(String userId, int roomId) throws Exception {
        return chatMapper.checkUserEnterRoom(userId, roomId);
    }

    @Override
    public void outRoom(int enterId) throws Exception {
        chatMapper.outRoom(enterId);
    }

    @Override
    public void updateRoomStatus(int enterId, int offsetNum) throws Exception {
        chatMapper.updateRoomStatus(enterId, offsetNum);
    }

    @Override
    public EnterChatDAO getEnterInfo(int enterId) throws Exception {
        return chatMapper.getEnterInfo(enterId);
    }

    @Override
    public void createRoom(ChatRoomDAO chatRoomDAO) throws Exception {
        chatMapper.createRoom(chatRoomDAO);
    }

    @Override
    public void saveFileInfo(FileDTO fileDTO) throws Exception {
        chatMapper.saveFileInfo(fileDTO);
    }

    @Override
    public FileDTO getFileInfo(String id) throws Exception {
        return chatMapper.getFileInfo(id);
    }

    public void webMessage(String userId, String userNm, int roomId, String message, String messageType) throws Exception {

        ChatMessageVO chatVO = new ChatMessageVO();
        chatVO.setSendernm(userNm);
        chatVO.setSenderid(userId);
        chatVO.setMessage(message);
        chatVO.setRoomid(roomId);
        chatVO.setMessagetype(messageType);

        msgOperation.convertAndSend("/api/chat/" + roomId, chatVO);

    }
}