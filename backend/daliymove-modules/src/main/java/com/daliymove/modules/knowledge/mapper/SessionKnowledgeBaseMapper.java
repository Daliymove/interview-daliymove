package com.daliymove.modules.knowledge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.daliymove.modules.knowledge.entity.SessionKnowledgeBase;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;

import java.util.List;

/**
 * 会话知识库关联Mapper接口
 * - 实现RagChatSession和KnowledgeBase的多对多关系
 */
@Mapper
public interface SessionKnowledgeBaseMapper extends BaseMapper<SessionKnowledgeBase> {

    /**
     * 根据会话ID查找知识库ID列表
     * @param sessionId 会话ID
     * @return 知识库ID列表
     */
    @Select("SELECT knowledge_base_id FROM session_knowledge_base WHERE session_id = #{sessionId}")
    List<Long> findKnowledgeBaseIdsBySessionId(@Param("sessionId") Long sessionId);

    /**
     * 根据知识库ID查找会话ID列表
     * @param knowledgeBaseId 知识库ID
     * @return 会话ID列表
     */
    @Select("SELECT session_id FROM session_knowledge_base WHERE knowledge_base_id = #{knowledgeBaseId}")
    List<Long> findSessionIdsByKnowledgeBaseId(@Param("knowledgeBaseId") Long knowledgeBaseId);

    /**
     * 删除会话的所有知识库关联
     * @param sessionId 会话ID
     * @return 影响行数
     */
    @Delete("DELETE FROM session_knowledge_base WHERE session_id = #{sessionId}")
    int deleteBySessionId(@Param("sessionId") Long sessionId);

    /**
     * 删除知识库的所有会话关联
     * @param knowledgeBaseId 知识库ID
     * @return 影响行数
     */
    @Delete("DELETE FROM session_knowledge_base WHERE knowledge_base_id = #{knowledgeBaseId}")
    int deleteByKnowledgeBaseId(@Param("knowledgeBaseId") Long knowledgeBaseId);

    /**
     * 删除指定会话和知识库的关联
     * @param sessionId 会话ID
     * @param knowledgeBaseId 知识库ID
     * @return 影响行数
     */
    @Delete("DELETE FROM session_knowledge_base WHERE session_id = #{sessionId} AND knowledge_base_id = #{knowledgeBaseId}")
    int deleteBySessionIdAndKnowledgeBaseId(@Param("sessionId") Long sessionId, @Param("knowledgeBaseId") Long knowledgeBaseId);

    /**
     * 检查会话和知识库是否已关联
     * @param sessionId 会话ID
     * @param knowledgeBaseId 知识库ID
     * @return 是否存在关联
     */
    @Select("SELECT COUNT(*) > 0 FROM session_knowledge_base WHERE session_id = #{sessionId} AND knowledge_base_id = #{knowledgeBaseId}")
    boolean existsBySessionIdAndKnowledgeBaseId(@Param("sessionId") Long sessionId, @Param("knowledgeBaseId") Long knowledgeBaseId);
}