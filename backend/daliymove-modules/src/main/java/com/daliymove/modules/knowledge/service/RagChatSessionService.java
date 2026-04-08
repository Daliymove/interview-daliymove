package com.daliymove.modules.knowledge.service;

import com.daliymove.common.exception.BusinessException;
import com.daliymove.common.exception.ErrorCode;
import com.daliymove.modules.knowledge.converter.KnowledgeBaseConverter;
import com.daliymove.modules.knowledge.converter.RagChatConverter;
import com.daliymove.modules.knowledge.dto.KnowledgeBaseListItemDTO;
import com.daliymove.modules.knowledge.dto.RagChatDTO.*;
import com.daliymove.modules.knowledge.entity.KnowledgeBase;
import com.daliymove.modules.knowledge.entity.RagChatMessage;
import com.daliymove.modules.knowledge.entity.RagChatSession;
import com.daliymove.modules.knowledge.entity.SessionKnowledgeBase;
import com.daliymove.modules.knowledge.mapper.KnowledgeBaseMapper;
import com.daliymove.modules.knowledge.mapper.RagChatMessageMapper;
import com.daliymove.modules.knowledge.mapper.RagChatSessionMapper;
import com.daliymove.modules.knowledge.mapper.SessionKnowledgeBaseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * RAG 聊天会话服务
 * - 提供RAG聊天会话的创建、获取、更新、删除等操作
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RagChatSessionService {

    private final RagChatSessionMapper sessionMapper;
    private final RagChatMessageMapper messageMapper;
    private final KnowledgeBaseMapper knowledgeBaseMapper;
    private final SessionKnowledgeBaseMapper sessionKnowledgeBaseMapper;
    private final KnowledgeBaseQueryService queryService;
    private final RagChatConverter ragChatConverter;
    private final KnowledgeBaseConverter knowledgeBaseConverter;

    /**
     * 创建新会话
     */
    @Transactional
    public SessionDTO createSession(CreateSessionRequest request) {
        List<KnowledgeBase> knowledgeBases = knowledgeBaseMapper.selectBatchIds(request.knowledgeBaseIds());

        if (knowledgeBases.size() != request.knowledgeBaseIds().size()) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "部分知识库不存在");
        }

        RagChatSession session = new RagChatSession();
        session.setTitle(request.title() != null && !request.title().isBlank()
            ? request.title()
            : generateTitle(knowledgeBases));
        session.setStatus("ACTIVE");
        session.setMessageCount(0);
        session.setIsPinned(false);

        sessionMapper.insert(session);

        for (KnowledgeBase kb : knowledgeBases) {
            SessionKnowledgeBase relation = new SessionKnowledgeBase();
            relation.setSessionId(session.getId());
            relation.setKnowledgeBaseId(kb.getId());
            sessionKnowledgeBaseMapper.insert(relation);
        }

        log.info("创建 RAG 聊天会话: id={}, title={}", session.getId(), session.getTitle());

        return new SessionDTO(
            session.getId(),
            session.getTitle(),
            request.knowledgeBaseIds(),
            session.getCreatedAt()
        );
    }

    /**
     * 获取会话列表
     */
    public List<SessionListItemDTO> listSessions() {
        return sessionMapper.findAllOrderByIsPinnedDescUpdatedAtDesc()
            .stream()
            .map(session -> {
                List<Long> kbIds = sessionKnowledgeBaseMapper.findKnowledgeBaseIdsBySessionId(session.getId());
                List<String> kbNames = kbIds.stream()
                    .map(id -> {
                        KnowledgeBase kb = knowledgeBaseMapper.selectById(id);
                        return kb != null ? kb.getName() : "未知知识库";
                    })
                    .toList();
                return new SessionListItemDTO(
                    session.getId(),
                    session.getTitle(),
                    session.getMessageCount(),
                    kbNames,
                    session.getUpdatedAt(),
                    session.getIsPinned() != null ? session.getIsPinned() : false
                );
            })
            .toList();
    }

    /**
     * 获取会话详情（包含消息）
     */
    public SessionDetailDTO getSessionDetail(Long sessionId) {
        RagChatSession session = sessionMapper.selectById(sessionId);
        if (session == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "会话不存在");
        }

        List<RagChatMessage> messages = messageMapper.findBySessionIdOrderByMessageOrder(sessionId);

        List<Long> kbIds = sessionKnowledgeBaseMapper.findKnowledgeBaseIdsBySessionId(sessionId);
        List<KnowledgeBase> knowledgeBases = knowledgeBaseMapper.selectBatchIds(kbIds);
        List<KnowledgeBaseListItemDTO> kbDTOs = knowledgeBaseConverter.toListItemDTOList(knowledgeBases);

        return ragChatConverter.toSessionDetailDTO(session, messages, kbDTOs);
    }

    /**
     * 准备流式消息（保存用户消息，创建 AI 消息占位）
     *
     * @return AI 消息的 ID
     */
    @Transactional
    public Long prepareStreamMessage(Long sessionId, String question) {
        RagChatSession session = sessionMapper.selectById(sessionId);
        if (session == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "会话不存在");
        }

        int nextOrder = session.getMessageCount() != null ? session.getMessageCount() : 0;

        RagChatMessage userMessage = new RagChatMessage();
        userMessage.setSessionId(sessionId);
        userMessage.setType("USER");
        userMessage.setContent(question);
        userMessage.setMessageOrder(nextOrder);
        userMessage.setCompleted(true);
        messageMapper.insert(userMessage);

        RagChatMessage assistantMessage = new RagChatMessage();
        assistantMessage.setSessionId(sessionId);
        assistantMessage.setType("ASSISTANT");
        assistantMessage.setContent("");
        assistantMessage.setMessageOrder(nextOrder + 1);
        assistantMessage.setCompleted(false);
        messageMapper.insert(assistantMessage);

        session.setMessageCount(nextOrder + 2);
        sessionMapper.updateById(session);

        log.info("准备流式消息: sessionId={}, messageId={}", sessionId, assistantMessage.getId());

        return assistantMessage.getId();
    }

    /**
     * 流式响应完成后更新消息
     */
    @Transactional
    public void completeStreamMessage(Long messageId, String content) {
        RagChatMessage message = messageMapper.selectById(messageId);
        if (message == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "消息不存在");
        }

        message.setContent(content);
        message.setCompleted(true);
        messageMapper.updateById(message);

        log.info("完成流式消息: messageId={}, contentLength={}", messageId, content.length());
    }

    /**
     * 获取流式回答
     */
    public Flux<String> getStreamAnswer(Long sessionId, String question) {
        RagChatSession session = sessionMapper.selectById(sessionId);
        if (session == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "会话不存在");
        }

        List<Long> kbIds = sessionKnowledgeBaseMapper.findKnowledgeBaseIdsBySessionId(sessionId);

        return queryService.answerQuestionStream(kbIds, question);
    }

    /**
     * 更新会话标题
     */
    @Transactional
    public void updateSessionTitle(Long sessionId, String title) {
        RagChatSession session = sessionMapper.selectById(sessionId);
        if (session == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "会话不存在");
        }

        session.setTitle(title);
        sessionMapper.updateById(session);

        log.info("更新会话标题: sessionId={}, title={}", sessionId, title);
    }

    /**
     * 切换会话置顶状态
     */
    @Transactional
    public void togglePin(Long sessionId) {
        RagChatSession session = sessionMapper.selectById(sessionId);
        if (session == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "会话不存在");
        }

        Boolean currentPinned = session.getIsPinned() != null ? session.getIsPinned() : false;
        session.setIsPinned(!currentPinned);
        sessionMapper.updateById(session);

        log.info("切换会话置顶状态: sessionId={}, isPinned={}", sessionId, session.getIsPinned());
    }

    /**
     * 更新会话的知识库关联
     */
    @Transactional
    public void updateSessionKnowledgeBases(Long sessionId, List<Long> knowledgeBaseIds) {
        RagChatSession session = sessionMapper.selectById(sessionId);
        if (session == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "会话不存在");
        }

        sessionKnowledgeBaseMapper.deleteBySessionId(sessionId);

        for (Long kbId : knowledgeBaseIds) {
            SessionKnowledgeBase relation = new SessionKnowledgeBase();
            relation.setSessionId(sessionId);
            relation.setKnowledgeBaseId(kbId);
            sessionKnowledgeBaseMapper.insert(relation);
        }

        log.info("更新会话知识库: sessionId={}, kbIds={}", sessionId, knowledgeBaseIds);
    }

    /**
     * 删除会话
     */
    @Transactional
    public void deleteSession(Long sessionId) {
        RagChatSession session = sessionMapper.selectById(sessionId);
        if (session == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "会话不存在");
        }

        sessionKnowledgeBaseMapper.deleteBySessionId(sessionId);
        sessionMapper.deleteById(sessionId);

        log.info("删除会话: sessionId={}", sessionId);
    }

    private String generateTitle(List<KnowledgeBase> knowledgeBases) {
        if (knowledgeBases.isEmpty()) {
            return "新对话";
        }
        if (knowledgeBases.size() == 1) {
            return knowledgeBases.get(0).getName();
        }
        return knowledgeBases.size() + " 个知识库对话";
    }
}