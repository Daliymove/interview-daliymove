package com.daliymove.modules.chat.dto;

import lombok.Data;

@Data
public class ChatRequest {
    private Long conversationId;
    private String message;
}