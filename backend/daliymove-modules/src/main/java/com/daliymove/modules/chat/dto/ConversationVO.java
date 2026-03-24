package com.daliymove.modules.chat.dto;

import lombok.Data;

import java.util.List;

@Data
public class ConversationVO {
    private Long id;
    private String title;
    private String modelType;
    private Long createTime;
    private Long updateTime;
    private List<MessageVO> messages;

    @Data
    public static class MessageVO {
        private Long id;
        private String role;
        private String content;
        private Long createTime;
    }
}