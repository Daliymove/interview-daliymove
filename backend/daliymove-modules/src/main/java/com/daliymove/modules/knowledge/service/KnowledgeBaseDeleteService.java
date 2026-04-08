package com.daliymove.modules.knowledge.service;

import com.daliymove.common.exception.BusinessException;
import com.daliymove.common.exception.ErrorCode;
import com.daliymove.modules.file.FileStorageService;
import com.daliymove.modules.knowledge.entity.KnowledgeBase;
import com.daliymove.modules.knowledge.mapper.KnowledgeBaseMapper;
import com.daliymove.modules.knowledge.mapper.SessionKnowledgeBaseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 知识库删除服务
 * - 负责知识库的删除操作
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeBaseDeleteService {

    private final KnowledgeBaseMapper knowledgeBaseMapper;
    private final SessionKnowledgeBaseMapper sessionKnowledgeBaseMapper;
    private final KnowledgeBaseVectorService vectorService;
    private final FileStorageService storageService;

    /**
     * 删除知识库
     * 包括：会话关联、向量数据、存储文件、数据库记录
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteKnowledgeBase(Long id) {
        KnowledgeBase kb = knowledgeBaseMapper.selectById(id);
        if (kb == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "知识库不存在");
        }

        List<Long> sessionIds = sessionKnowledgeBaseMapper.findSessionIdsByKnowledgeBaseId(id);
        for (Long sessionId : sessionIds) {
            sessionKnowledgeBaseMapper.deleteBySessionIdAndKnowledgeBaseId(sessionId, id);
            log.debug("已从会话中移除知识库关联: sessionId={}, kbId={}", sessionId, id);
        }
        if (!sessionIds.isEmpty()) {
            log.info("已从 {} 个会话中移除知识库关联: kbId={}", sessionIds.size(), id);
        }

        try {
            vectorService.deleteByKnowledgeBaseId(id);
        } catch (Exception e) {
            log.warn("删除向量数据失败，继续删除知识库: kbId={}, error={}", id, e.getMessage());
        }

        try {
            storageService.deleteKnowledgeBase(kb.getStorageKey());
        } catch (Exception e) {
            log.warn("删除存储文件失败，继续删除知识库记录: kbId={}, error={}", id, e.getMessage());
        }

        knowledgeBaseMapper.deleteById(id);
        log.info("知识库已删除: id={}", id);
    }
}