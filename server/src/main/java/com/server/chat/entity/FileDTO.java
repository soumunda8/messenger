package com.server.chat.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileDTO {

    private String id;
    private String saveFolder;
    private String originName;
    private String saveName;
    private String fileType;
    private String uploadDate;

}