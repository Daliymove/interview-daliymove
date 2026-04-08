package com.daliymove.modules.knowledge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.daliymove.modules.knowledge.entity.RagChatSession;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * RAG聊天会话Mapper接口
 * - 提供RAG聊天会话的数据访问操作
 * - 支持会话状态查询
 * - 支持置顶会话查询
 */
@Mapper
public interface RagChatSessionMapper extends BaseMapper<RagChatSession> {

    /**
     * 根据状态查找会话列表
     * @param status 会话状态
     * @return 会话列表
     */
    @Select("SELECT * FROM rag_chat_session WHERE status = #{status} ORDER BY is_pinned DESC, updated_at DESC")
    List<RagChatSession> findByStatus(@Param("status") String status);

    /**
     * 查找所有置顶的会话
     * @return 会话列表
     */
    @Select("SELECT * FROM rag_chat_session WHERE is_pinned = true ORDER BY updated_at DESC")
    List<RagChatSession> findByIsPinnedTrue();

    /**
     * 查找所有会话，按置顶和更新时间排序
     * @return 会话列表
     */
    @Select("SELECT * FROM rag_chat_session ORDER BY is_pinned DESC, updated_at DESC")
    List<RagChatSession> findAllOrderByIsPinnedDescUpdatedAtDesc();

    /**
     * 更新会话消息数量
     * @param sessionId 会话ID
     * @param count 消息数量
     * @return 影响行数
     */
    @Update("UPDATE rag_chat_session SET message_count = #{count}, updated_at = NOW() WHERE id = #{sessionId}")
    int updateMessageCount(@Param("sessionId") Long sessionId, @Param("count") Integer count);

    /**
     * 更新会话标题
     * @param sessionId 会话ID
     * @param title 标题
     * @return 影响行数
     */
    @Update("UPDATE rag_chat_session SET title = #{title}, updated_at = NOW() WHERE id = #{sessionId}")
    int updateTitle(@Param("sessionId") Long sessionId, @Param("title") String title);

    /**
     * 切换会话置顶状态
     * @param sessionId 会话ID
     * @param isPinned 是否置顶
     * @return 影响行数
     */
    @Update("UPDATE rag_chat_session SET is_pinned = #{isPinned}, updated_at = NOW() WHERE id = #{sessionId}")
    int updatePinnedStatus(@Param("sessionId") Long sessionId, @Param("isPinned") Boolean isPinned);

    /**
     * 归档会话（更新状态为ARCHIVED）
     * @param sessionId 会话ID
     * @return 影响行数
     */
    @Update("UPDATE rag_chat_session SET status = 'ARCHIVED', updated_at = NOW() WHERE id = #{sessionId}")
    int archiveSession(@Param("sessionId") Long sessionId);
}