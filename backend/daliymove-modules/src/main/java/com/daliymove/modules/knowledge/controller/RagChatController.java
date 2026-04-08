package com.daliymove.modules.knowledge.controller;

import com.daliymove.common.response.Result;
import com.daliymove.modules.knowledge.dto.RagChatDTO.*;
import com.daliymove.modules.knowledge.service.RagChatSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * RAG 聊天控制器
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class RagChatController {

    private final RagChatSessionService sessionService;

    /**
     * 创建新会话
     */
    @PostMapping("/api/rag-chat/sessions")
    public Result<SessionDTO> createSession(@Valid @RequestBody CreateSessionRequest request) {
        return Result.success(sessionService.createSession(request));
    }

    /**
     * 获取会话列表
     */
    @GetMapping("/api/rag-chat/sessions")
    public Result<List<SessionListItemDTO>> listSessions() {
        return Result.success(sessionService.listSessions());
    }

    /**
     * 获取会话详情（包含消息历史）
     */
    @GetMapping("/api/rag-chat/sessions/{sessionId}")
    public Result<SessionDetailDTO> getSessionDetail(@PathVariable Long sessionId) {
        return Result.success(sessionService.getSessionDetail(sessionId));
    }

    /**
     * 更新会话标题
     */
    @PutMapping("/api/rag-chat/sessions/{sessionId}/title")
    public Result<Void> updateSessionTitle(
            @PathVariable Long sessionId,
            @Valid @RequestBody UpdateTitleRequest request) {
        sessionService.updateSessionTitle(sessionId, request.title());
        return Result.success(null);
    }

    /**
     * 切换会话置顶状态
     */
    @PutMapping("/api/rag-chat/sessions/{sessionId}/pin")
    public Result<Void> togglePin(@PathVariable Long sessionId) {
        sessionService.togglePin(sessionId);
        return Result.success(null);
    }

    /**
     * 更新会话知识库
     */
    @PutMapping("/api/rag-chat/sessions/{sessionId}/knowledge-bases")
    public Result<Void> updateSessionKnowledgeBases(
            @PathVariable Long sessionId,
            @Valid @RequestBody UpdateKnowledgeBasesRequest request) {
        sessionService.updateSessionKnowledgeBases(sessionId, request.knowledgeBaseIds());
        return Result.success(null);
    }

    /**
     * 删除会话
     */
    @DeleteMapping("/api/rag-chat/sessions/{sessionId}")
    public Result<Void> deleteSession(@PathVariable Long sessionId) {
        sessionService.deleteSession(sessionId);
        return Result.success(null);
    }

    /**
     * 发送消息（流式SSE）
     */
    @PostMapping(value = "/api/rag-chat/sessions/{sessionId}/messages/stream",
                 produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> sendMessageStream(
            @PathVariable Long sessionId,
            @Valid @RequestBody SendMessageRequest request) {

        log.info("收到 RAG 聊天流式请求: sessionId={}, question={}", sessionId, request.question());

        Long messageId = sessionService.prepareStreamMessage(sessionId, request.question());

        StringBuilder fullContent = new StringBuilder();

        return sessionService.getStreamAnswer(sessionId, request.question())
            .doOnNext(fullContent::append)
            .map(chunk -> ServerSentEvent.<String>builder()
                .data(chunk.replace("\n", "\\n").replace("\r", "\\r"))
                .build())
            .doOnComplete(() -> {
                sessionService.completeStreamMessage(messageId, fullContent.toString());
                log.info("RAG 聊天流式完成: sessionId={}, messageId={}", sessionId, messageId);
            })
            .doOnError(e -> {
                String content = !fullContent.isEmpty()
                    ? fullContent.toString()
                    : "【错误】回答生成失败：" + e.getMessage();
                sessionService.completeStreamMessage(messageId, content);
                log.error("RAG 聊天流式错误: sessionId={}", sessionId, e);
            });
    }
}