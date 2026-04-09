package com.daliymove.modules.knowledge.controller;

import com.daliymove.common.response.Result;
import com.daliymove.modules.knowledge.dto.RagChatDTO.*;
import com.daliymove.modules.knowledge.service.RagChatSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
 * - RAG聊天会话管理
 * - 基于知识库的对话
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "RAG聊天", description = "基于知识库的RAG聊天会话管理接口")
public class RagChatController {

    private final RagChatSessionService sessionService;

    /**
     * 创建新会话
     */
@PostMapping("/rag-chat/sessions")
    @Operation(summary = "创建RAG聊天会话", description = "创建新的RAG聊天会话，绑定知识库")
    public Result<SessionDTO> createSession(@Valid @RequestBody CreateSessionRequest request) {
        return Result.success(sessionService.createSession(request));
    }

    @GetMapping("/rag-chat/sessions")
    @Operation(summary = "获取会话列表", description = "获取当前用户的所有RAG聊天会话列表")
    public Result<List<SessionListItemDTO>> listSessions() {
        return Result.success(sessionService.listSessions());
    }

    @GetMapping("/rag-chat/sessions/{sessionId}")
    @Operation(summary = "获取会话详情", description = "获取RAG聊天会话详情，包含消息历史")
    public Result<SessionDetailDTO> getSessionDetail(
            @Parameter(description = "会话ID") @PathVariable Long sessionId) {
        return Result.success(sessionService.getSessionDetail(sessionId));
    }

    @PutMapping("/rag-chat/sessions/{sessionId}/title")
    @Operation(summary = "更新会话标题", description = "更新RAG聊天会话的标题")
    public Result<Void> updateSessionTitle(
            @Parameter(description = "会话ID") @PathVariable Long sessionId,
            @Valid @RequestBody UpdateTitleRequest request) {
        sessionService.updateSessionTitle(sessionId, request.title());
        return Result.success(null);
    }

    @PutMapping("/rag-chat/sessions/{sessionId}/pin")
    @Operation(summary = "切换置顶状态", description = "切换会话的置顶状态")
    public Result<Void> togglePin(
            @Parameter(description = "会话ID") @PathVariable Long sessionId) {
        sessionService.togglePin(sessionId);
        return Result.success(null);
    }

    @PutMapping("/rag-chat/sessions/{sessionId}/knowledge-bases")
    @Operation(summary = "更新会话知识库", description = "更新会话绑定的知识库列表")
    public Result<Void> updateSessionKnowledgeBases(
            @Parameter(description = "会话ID") @PathVariable Long sessionId,
            @Valid @RequestBody UpdateKnowledgeBasesRequest request) {
        sessionService.updateSessionKnowledgeBases(sessionId, request.knowledgeBaseIds());
        return Result.success(null);
    }

    @DeleteMapping("/rag-chat/sessions/{sessionId}")
    @Operation(summary = "删除会话", description = "删除指定的RAG聊天会话")
    public Result<Void> deleteSession(
            @Parameter(description = "会话ID") @PathVariable Long sessionId) {
        sessionService.deleteSession(sessionId);
        return Result.success(null);
    }

    @PostMapping(value = "/rag-chat/sessions/{sessionId}/messages/stream",
                 produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "发送消息（流式）", description = "发送消息并获取流式回答")
    public Flux<ServerSentEvent<String>> sendMessageStream(
            @Parameter(description = "会话ID") @PathVariable Long sessionId,
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