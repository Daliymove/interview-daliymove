package com.daliymove.modules.knowledge.service;

import com.daliymove.common.exception.BusinessException;
import com.daliymove.common.exception.ErrorCode;
import com.daliymove.modules.knowledge.entity.KnowledgeBase;
import com.daliymove.modules.knowledge.mapper.KnowledgeBaseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 知识库计数服务
 * - 负责更新知识库的访问计数和问题计数
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeBaseCountService {

    private final KnowledgeBaseMapper knowledgeBaseMapper;

    /**
     * 批量更新知识库提问计数（使用单条 SQL 批量更新）
     * 每个知识库的 questionCount +1，表示该知识库参与回答的次数
     *
     * @param knowledgeBaseIds 知识库ID列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateQuestionCounts(List<Long> knowledgeBaseIds) {
        if (knowledgeBaseIds == null || knowledgeBaseIds.isEmpty()) {
            return;
        }

        List<Long> uniqueIds = knowledgeBaseIds.stream().distinct().toList();

        Set<Long> existingIds = new HashSet<>(knowledgeBaseMapper.selectBatchIds(uniqueIds)
                .stream().map(KnowledgeBase::getId).toList());

        for (Long id : uniqueIds) {
            if (!existingIds.contains(id)) {
                throw new BusinessException(ErrorCode.NOT_FOUND, "知识库不存在: " + id);
            }
        }

        int updated = knowledgeBaseMapper.incrementQuestionCountBatch(uniqueIds);
        log.debug("批量更新知识库提问计数: ids={}, updated={}", uniqueIds, updated);
    }
}