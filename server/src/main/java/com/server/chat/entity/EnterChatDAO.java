package com.server.chat.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnterChatDAO {

    private int id;
    private int roomid;
    private String userid;
    private boolean status;
    private int chatoffset;

}