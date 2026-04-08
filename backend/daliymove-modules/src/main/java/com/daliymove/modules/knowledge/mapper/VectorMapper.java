package com.daliymove.modules.knowledge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.daliymove.modules.knowledge.entity.KnowledgeVector;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Delete;

/**
 * 向量数据Mapper接口
 * - TODO: MySQL不原生支持向量存储，需要考虑以下方案：
 *   1. 使用外部向量数据库（如Milvus、Pinecone、Weaviate）
 *   2. 使用MySQL + 向量插件（如MariaDB Vector）
 *   3. 将向量存储为BLOB，在内存中进行相似度计算
 * - 当前为占位实现，实际向量操作通过Spring AI VectorStore进行
 */
@Mapper
public interface VectorMapper extends BaseMapper<KnowledgeVector> {

    /**
     * 删除指定知识库的所有向量数据
     * TODO: 需要根据实际向量存储方案实现
     * @param knowledgeBaseId 知识库ID
     * @return 影响行数
     */
    @Delete("DELETE FROM knowledge_vector WHERE knowledge_base_id = #{knowledgeBaseId}")
    int deleteByKnowledgeBaseId(@Param("knowledgeBaseId") Long knowledgeBaseId);
}