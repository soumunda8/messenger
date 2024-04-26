package com.server.chat.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageVO {

    private int id;
    private String senderid;
    private String sendernm;
    private String senddate;
    private String message;
    private int roomid;
    private String fid;
    private String roomnm;
    private boolean roomStatus;
    private String messagetype;
    private int chatoffset;

}