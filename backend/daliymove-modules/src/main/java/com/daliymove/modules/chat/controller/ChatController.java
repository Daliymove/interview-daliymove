package com.daliymove.modules.chat.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.daliymove.common.response.Result;
import com.daliymove.modules.chat.dto.ConversationVO;
import com.daliymove.modules.chat.entity.ChatConversation;
import com.daliymove.modules.chat.entity.ChatMessage;
import com.daliymove.modules.chat.service.AiService;
import com.daliymove.modules.chat.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Tag(name = "聊天助手", description = "AI聊天助手接口")
public class ChatController {

    private final ChatService chatService;
    private final AiService aiService;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    @GetMapping("/conversations")
    @Operation(summary = "获取会话列表")
    public Result<List<ConversationVO>> getConversations() {
        return Result.success(chatService.getConversationList());
    }

    @GetMapping("/conversation/{id}")
    @Operation(summary = "获取会话详情")
    public Result<ConversationVO> getConversation(@PathVariable Long id) {
        return Result.success(chatService.getConversation(id));
    }

    @PostMapping("/conversation")
    @Operation(summary = "创建新会话")
    public Result<ConversationVO> createConversation() {
        ChatConversation conversation = chatService.createConversation();
        return Result.success(chatService.getConversation(conversation.getId()));
    }

    @PutMapping("/conversation/{id}/title")
    @Operation(summary = "更新会话标题")
    public Result<Void> updateTitle(@PathVariable Long id, @RequestParam String title) {
        chatService.updateTitle(id, title);
        return Result.success();
    }

    @DeleteMapping("/conversation/{id}")
    @Operation(summary = "删除会话")
    public Result<Void> deleteConversation(@PathVariable Long id) {
        chatService.deleteConversation(id);
        return Result.success();
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "流式对话")
    public SseEmitter streamChat(
            @RequestParam(required = false) Long conversationId,
            @RequestParam String message) {
        
        Long userId = StpUtil.getLoginIdAsLong();
        
        SseEmitter emitter = new SseEmitter(120000L);
        AtomicBoolean completed = new AtomicBoolean(false);
        
        emitter.onCompletion(() -> completed.set(true));
        emitter.onTimeout(() -> completed.set(true));
        emitter.onError(e -> completed.set(true));
        
        executorService.execute(() -> {
            try {
                ChatConversation conversation = chatService.getOrCreateConversation(conversationId, userId);
                Long actualConversationId = conversation.getId();
                boolean isNewConversation = "新对话".equals(conversation.getTitle());
                
                chatService.saveMessage(actualConversationId, "user", message);
                
                if (isNewConversation) {
                    String generatedTitle = aiService.generateTitle(message);
                    chatService.updateTitle(actualConversationId, generatedTitle, userId);
                    emitter.send(SseEmitter.event()
                        .name("title")
                        .data("{\"title\":\"" + escapeJson(generatedTitle) + "\"}"));
                }
                
                List<ChatMessage> history = chatService.getConversationMessages(actualConversationId);
                List<AiService.ChatMessageHistory> chatHistory = history.stream()
                        .map(m -> new AiService.ChatMessageHistory(m.getRole(), m.getContent()))
                        .collect(Collectors.toList());
                
                Flux<String> responseFlux = aiService.streamChatWithContext(chatHistory, message);
                
                StringBuilder fullContent = new StringBuilder();
                
                responseFlux.subscribe(
                    chunk -> {
                        if (completed.get()) return;
                        try {
                            fullContent.append(chunk);
                            emitter.send(SseEmitter.event()
                                .name("message")
                                .data("{\"content\":\"" + escapeJson(chunk) + "\"}"));
                        } catch (IOException e) {
                            log.error("Error sending chunk", e);
                        }
                    },
                    error -> {
                        log.error("AI stream error", error);
                        try {
                            emitter.send(SseEmitter.event()
                                .name("error")
                                .data("{\"message\":\"" + escapeJson(error.getMessage()) + "\"}"));
                        } catch (IOException ex) {
                            log.error("Error sending error event", ex);
                        }
                        emitter.completeWithError(error);
                    },
                    () -> {
                        if (!completed.get()) {
                            try {
                                chatService.saveMessage(actualConversationId, "assistant", fullContent.toString());
                                
                                emitter.send(SseEmitter.event()
                                    .name("done")
                                    .data("{\"conversationId\":" + actualConversationId + ",\"tokens\":150}"));
                                emitter.complete();
                            } catch (IOException e) {
                                log.error("Error completing stream", e);
                                emitter.completeWithError(e);
                            }
                        }
                    }
                );
                
            } catch (Exception e) {
                log.error("SSE error", e);
                try {
                    emitter.send(SseEmitter.event()
                        .name("error")
                        .data("{\"message\":\"" + escapeJson(e.getMessage()) + "\"}"));
                } catch (IOException ex) {
                    log.error("Error sending error event", ex);
                }
                emitter.completeWithError(e);
            }
        });
        
        return emitter;
    }

    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}