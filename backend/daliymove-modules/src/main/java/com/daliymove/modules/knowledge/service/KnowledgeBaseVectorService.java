package com.daliymove.modules.knowledge.service;

import com.daliymove.modules.knowledge.mapper.VectorMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 知识库向量存储服务
 * - 负责文档分块、向量化和检索
 * - TODO: 需要根据实际向量存储方案调整（MySQL不原生支持向量）
 * - 当前使用Spring AI VectorStore进行向量操作
 */
@Slf4j
@Service
public class KnowledgeBaseVectorService {

    private static final int MAX_BATCH_SIZE = 10;
    private final VectorStore vectorStore;
    private final TextSplitter textSplitter;
    private final VectorMapper vectorMapper;

    public KnowledgeBaseVectorService(VectorStore vectorStore, VectorMapper vectorMapper) {
        this.vectorStore = vectorStore;
        this.vectorMapper = vectorMapper;
        this.textSplitter = new TokenTextSplitter();
    }

    /**
     * 将知识库内容向量化并存储
     * TODO: 需要确认VectorStore配置（如使用外部向量数据库或内存方案）
     * @param knowledgeBaseId 知识库ID
     * @param content 知识库文本内容
     */
    @Transactional
    public void vectorizeAndStore(Long knowledgeBaseId, String content) {
        log.info("开始向量化知识库: kbId={}, contentLength={}", knowledgeBaseId, content.length());
        try {
            deleteByKnowledgeBaseId(knowledgeBaseId);

            List<Document> chunks = textSplitter.apply(
                List.of(new Document(content))
            );

            log.info("文本分块完成: {} 个chunks", chunks.size());

            chunks.forEach(chunk -> chunk.getMetadata().put("kb_id", knowledgeBaseId.toString()));

            int totalChunks = chunks.size();
            int batchCount = (totalChunks + MAX_BATCH_SIZE - 1) / MAX_BATCH_SIZE;
            log.info("开始分批向量化: 总共 {} 个chunks，分 {} 批处理，每批最多 {} 个",
                    totalChunks, batchCount, MAX_BATCH_SIZE);

            for (int i = 0; i < batchCount; i++) {
                int start = i * MAX_BATCH_SIZE;
                int end = Math.min(start + MAX_BATCH_SIZE, totalChunks);
                List<Document> batch = chunks.subList(start, end);
                log.debug("处理第 {}/{} 批: chunks {}-{}", i + 1, batchCount, start + 1, end);
                vectorStore.add(batch);
            }

            log.info("知识库向量化完成: kbId={}, chunks={}, batches={}",
                    knowledgeBaseId, totalChunks, batchCount);
        } catch (Exception e) {
            log.error("向量化知识库失败: kbId={}, error={}", knowledgeBaseId, e.getMessage(), e);
            throw new RuntimeException("向量化知识库失败: " + e.getMessage(), e);
        }
    }

    /**
     * 基于多个知识库进行相似度搜索
     * TODO: 需要确认VectorStore的过滤表达式语法
     * @param query 查询文本
     * @param knowledgeBaseIds 知识库ID列表
     * @param topK 返回top K个结果
     * @return 相关文档列表
     */
    public List<Document> similaritySearch(String query, List<Long> knowledgeBaseIds, int topK, double minScore) {
        log.info("向量相似度搜索: query={}, kbIds={}, topK={}, minScore={}",
            query, knowledgeBaseIds, topK, minScore);

        try {
            SearchRequest.Builder builder = SearchRequest.builder()
                .query(query)
                .topK(Math.max(topK, 1));

            if (minScore > 0) {
                builder.similarityThreshold(minScore);
            }

            if (knowledgeBaseIds != null && !knowledgeBaseIds.isEmpty()) {
                builder.filterExpression(buildKbFilterExpression(knowledgeBaseIds));
            }

            List<Document> results = vectorStore.similaritySearch(builder.build());
            if (results == null) {
                return List.of();
            }

            log.info("搜索完成: 找到 {} 个相关文档", results.size());
            return results;

        } catch (Exception e) {
            log.warn("向量搜索前置过滤失败，回退到本地过滤: {}", e.getMessage());
            return similaritySearchFallback(query, knowledgeBaseIds, topK, minScore);
        }
    }

    private List<Document> similaritySearchFallback(String query, List<Long> knowledgeBaseIds, int topK, double minScore) {
        try {
            SearchRequest.Builder builder = SearchRequest.builder()
                .query(query)
                .topK(Math.max(topK * 3, topK));
            if (minScore > 0) {
                builder.similarityThreshold(minScore);
            }

            List<Document> allResults = vectorStore.similaritySearch(builder.build());
            if (allResults == null || allResults.isEmpty()) {
                return List.of();
            }

            if (knowledgeBaseIds != null && !knowledgeBaseIds.isEmpty()) {
                allResults = allResults.stream()
                    .filter(doc -> isDocInKnowledgeBases(doc, knowledgeBaseIds))
                    .collect(Collectors.toList());
            }

            List<Document> results = allResults.stream()
                .limit(topK)
                .collect(Collectors.toList());

            log.info("回退检索完成: 找到 {} 个相关文档", results.size());
            return results;
        } catch (Exception e) {
            log.error("向量搜索失败: {}", e.getMessage(), e);
            throw new RuntimeException("向量搜索失败: " + e.getMessage(), e);
        }
    }

    private boolean isDocInKnowledgeBases(Document doc, List<Long> knowledgeBaseIds) {
        Object kbId = doc.getMetadata().get("kb_id");
        if (kbId == null) {
            return false;
        }
        try {
            Long kbIdLong = kbId instanceof Long
                ? (Long) kbId
                : Long.parseLong(kbId.toString());
            return knowledgeBaseIds.contains(kbIdLong);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private String buildKbFilterExpression(List<Long> knowledgeBaseIds) {
        String values = knowledgeBaseIds.stream()
            .filter(Objects::nonNull)
            .map(String::valueOf)
            .map(id -> "'" + id + "'")
            .collect(Collectors.joining(", "));
        return "kb_id in [" + values + "]";
    }

    /**
     * 删除指定知识库的所有向量数据
     * TODO: 需要根据实际向量存储方案实现删除逻辑
     * @param knowledgeBaseId 知识库ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteByKnowledgeBaseId(Long knowledgeBaseId) {
        try {
            vectorMapper.deleteByKnowledgeBaseId(knowledgeBaseId);
            log.info("向量数据已删除: kbId={}", knowledgeBaseId);
        } catch (Exception e) {
            log.error("删除向量数据失败: kbId={}, error={}", knowledgeBaseId, e.getMessage(), e);
        }
    }
}