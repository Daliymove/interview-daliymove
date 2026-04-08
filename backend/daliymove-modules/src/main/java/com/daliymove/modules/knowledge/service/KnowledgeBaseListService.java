package com.daliymove.modules.knowledge.service;

import com.daliymove.common.exception.BusinessException;
import com.daliymove.common.exception.ErrorCode;
import com.daliymove.modules.file.FileStorageService;
import com.daliymove.modules.knowledge.converter.KnowledgeBaseConverter;
import com.daliymove.modules.knowledge.dto.KnowledgeBaseListItemDTO;
import com.daliymove.modules.knowledge.dto.KnowledgeBaseStatsDTO;
import com.daliymove.modules.knowledge.entity.KnowledgeBase;
import com.daliymove.modules.knowledge.enums.VectorStatus;
import com.daliymove.modules.knowledge.mapper.KnowledgeBaseMapper;
import com.daliymove.modules.knowledge.mapper.RagChatMessageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 知识库查询服务
 * - 负责知识库列表和详情的查询
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeBaseListService {

    private final KnowledgeBaseMapper knowledgeBaseMapper;
    private final RagChatMessageMapper ragChatMessageMapper;
    private final KnowledgeBaseConverter knowledgeBaseConverter;
    private final FileStorageService fileStorageService;

    /**
     * 获取知识库列表（支持状态过滤和排序）
     *
     * @param vectorStatus 向量化状态，null 表示不过滤
     * @param sortBy 排序字段，null 或 "time" 表示按时间排序
     * @return 知识库列表
     */
    public List<KnowledgeBaseListItemDTO> listKnowledgeBases(VectorStatus vectorStatus, String sortBy) {
        List<KnowledgeBase> entities;

        if (vectorStatus != null) {
            entities = knowledgeBaseMapper.findByVectorStatusOrderByUploadedAtDesc(vectorStatus.name());
        } else {
            entities = knowledgeBaseMapper.findAllByOrderByUploadedAtDesc();
        }

        if (sortBy != null && !sortBy.isBlank() && !sortBy.equalsIgnoreCase("time")) {
            entities = sortEntities(entities, sortBy);
        }

        return knowledgeBaseConverter.toListItemDTOList(entities);
    }

    /**
     * 获取所有知识库列表（保持向后兼容）
     */
    public List<KnowledgeBaseListItemDTO> listKnowledgeBases() {
        return listKnowledgeBases(null, null);
    }

    /**
     * 按向量化状态获取知识库列表（保持向后兼容）
     */
    public List<KnowledgeBaseListItemDTO> listKnowledgeBasesByStatus(VectorStatus vectorStatus) {
        return listKnowledgeBases(vectorStatus, null);
    }

    /**
     * 根据ID获取知识库详情
     */
    public Optional<KnowledgeBaseListItemDTO> getKnowledgeBase(Long id) {
        KnowledgeBase entity = knowledgeBaseMapper.selectById(id);
        if (entity == null) {
            return Optional.empty();
        }
        return Optional.of(knowledgeBaseConverter.toListItemDTO(entity));
    }

    /**
     * 根据ID获取知识库实体（用于删除等操作）
     */
    public Optional<KnowledgeBase> getKnowledgeBaseEntity(Long id) {
        KnowledgeBase entity = knowledgeBaseMapper.selectById(id);
        return Optional.ofNullable(entity);
    }

    /**
     * 根据ID列表获取知识库名称列表
     */
    public List<String> getKnowledgeBaseNames(List<Long> ids) {
        return ids.stream()
            .map(id -> {
                KnowledgeBase kb = knowledgeBaseMapper.selectById(id);
                return kb != null ? kb.getName() : "未知知识库";
            })
            .toList();
    }

    /**
     * 获取所有分类
     */
    public List<String> getAllCategories() {
        return knowledgeBaseMapper.findAllCategories();
    }

    /**
     * 根据分类获取知识库列表
     */
    public List<KnowledgeBaseListItemDTO> listByCategory(String category) {
        List<KnowledgeBase> entities;
        if (category == null || category.isBlank()) {
            entities = knowledgeBaseMapper.findByCategoryIsNullOrderByUploadedAtDesc();
        } else {
            entities = knowledgeBaseMapper.findByCategoryOrderByUploadedAtDesc(category);
        }
        return knowledgeBaseConverter.toListItemDTOList(entities);
    }

    /**
     * 更新知识库分类
     */
    @Transactional
    public void updateCategory(Long id, String category) {
        KnowledgeBase entity = knowledgeBaseMapper.selectById(id);
        if (entity == null) {
            throw new RuntimeException("知识库不存在");
        }
        entity.setCategory(category != null && !category.isBlank() ? category : null);
        knowledgeBaseMapper.updateById(entity);
        log.info("更新知识库分类: id={}, category={}", id, category);
    }

    /**
     * 按关键词搜索知识库
     */
    public List<KnowledgeBaseListItemDTO> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return listKnowledgeBases();
        }
        return knowledgeBaseConverter.toListItemDTOList(
            knowledgeBaseMapper.searchByKeyword(keyword.trim())
        );
    }

    /**
     * 按指定字段排序获取知识库列表（保持向后兼容）
     */
    public List<KnowledgeBaseListItemDTO> listSorted(String sortBy) {
        return listKnowledgeBases(null, sortBy);
    }

    /**
     * 在内存中对实体列表排序
     */
    private List<KnowledgeBase> sortEntities(List<KnowledgeBase> entities, String sortBy) {
        return switch (sortBy.toLowerCase()) {
            case "size" -> entities.stream()
                .sorted((a, b) -> Long.compare(b.getFileSize() != null ? b.getFileSize() : 0, a.getFileSize() != null ? a.getFileSize() : 0))
                .toList();
            case "access" -> entities.stream()
                .sorted((a, b) -> Integer.compare(b.getAccessCount() != null ? b.getAccessCount() : 0, a.getAccessCount() != null ? a.getAccessCount() : 0))
                .toList();
            case "question" -> entities.stream()
                .sorted((a, b) -> Integer.compare(b.getQuestionCount() != null ? b.getQuestionCount() : 0, a.getQuestionCount() != null ? a.getQuestionCount() : 0))
                .toList();
            default -> entities;
        };
    }

    /**
     * 获取知识库统计信息
     */
    public KnowledgeBaseStatsDTO getStatistics() {
        return new KnowledgeBaseStatsDTO(
            knowledgeBaseMapper.selectCount(null),
            ragChatMessageMapper.countByType("USER"),
            knowledgeBaseMapper.sumAccessCount(),
            knowledgeBaseMapper.countByVectorStatus(VectorStatus.COMPLETED.name()),
            knowledgeBaseMapper.countByVectorStatus(VectorStatus.PROCESSING.name())
        );
    }

    /**
     * 下载知识库文件
     */
    public byte[] downloadFile(Long id) {
        KnowledgeBase entity = knowledgeBaseMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.KNOWLEDGE_BASE_NOT_FOUND, "知识库不存在");
        }

        String storageKey = entity.getStorageKey();
        if (storageKey == null || storageKey.isBlank()) {
            throw new BusinessException(ErrorCode.STORAGE_DOWNLOAD_FAILED, "文件存储信息不存在");
        }

        log.info("下载知识库文件: id={}, filename={}", id, entity.getOriginalFilename());
        return fileStorageService.downloadFile(storageKey);
    }

    /**
     * 获取知识库文件信息（用于下载）
     */
    public KnowledgeBase getEntityForDownload(Long id) {
        KnowledgeBase entity = knowledgeBaseMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.KNOWLEDGE_BASE_NOT_FOUND, "知识库不存在");
        }
        return entity;
    }
}