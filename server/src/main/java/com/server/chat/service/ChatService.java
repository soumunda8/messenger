package com.server.chat.service;

import com.server.chat.domain.*;
import com.server.file.domain.FileDTO;

import java.util.List;

public interface ChatService {

    public List<UserChatVO> getUserChatRoom(String userId) throws Exception;
    public EnterChatDAO getEnterChatInfo(int roomId, String userId) throws Exception;
    public List<ChatMessageVO> getChatList(int roomId, String senderId) throws Exception;
    public Integer getChatLastNum(int roomId) throws Exception;
    public void enterRoom(EnterChatDAO enterChatDAO) throws Exception;
    public EnterChatDAO checkUserEnterRoom(String userId, int roomId) throws Exception;
    public void outRoom(int enterId) throws Exception;
    public void updateEnterRoomStatus(int enterId, int offsetNum) throws Exception;
    public EnterChatDAO getEnterInfo(int enterId) throws Exception;
    public void createRoom(ChatRoomDAO chatRoomDAO) throws Exception;
    public void saveFileInfo(FileDTO fileDTO) throws Exception;
    public FileDTO getFileInfo(String id) throws Exception;
    public ChatRoomDAO getChatRoomInfo(int id) throws Exception;

}