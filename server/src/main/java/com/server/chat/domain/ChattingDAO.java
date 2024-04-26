package com.server.chat.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChattingDAO {

    private int id;
    private int enterid;
    private String senddate;
    private String message;
    private String messagetype;
    private String fid;

}