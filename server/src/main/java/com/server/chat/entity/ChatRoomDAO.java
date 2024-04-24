package com.server.chat.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDAO {

    private int id;
    private String roomnm;
    private String resdate;
    private boolean status;

}