package com.server.chat.persistence;

import com.server.chat.entity.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ChatMapper {

    public List<UserChatVO> getUserChatRoom(String userId) throws Exception;
    public EnterChatDAO getEnterChatInfo(EnterChatDAO enterChatDAO) throws Exception;
    public List<ChatMessageVO> getChatList(int roomId, String senderId) throws Exception;
    public Integer getChatLastNum(int roomId) throws Exception;
    public void enterRoom(EnterChatDAO enterChatDAO) throws Exception;
    public EnterChatDAO checkUserEnterRoom(String userId, int roomId) throws Exception;
    public void outRoom(int enterId) throws Exception;
    public void updateRoomStatus(int enterId, int offsetNum) throws Exception;
    public EnterChatDAO getEnterInfo(int enterId) throws Exception;
    public void createRoom(ChatRoomDAO chatRoomDAO) throws Exception;
    public void saveFileInfo(FileDTO fileDTO) throws Exception;
    public FileDTO getFileInfo(String id) throws Exception;

}