package com.daliymove.modules.knowledge.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.daliymove.modules.knowledge.entity.RagChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * RAG聊天消息Mapper接口
 * - 提供RAG聊天消息的数据访问操作
 * - 支持按会话查询消息
 * - 支持按消息类型过滤
 */
@Mapper
public interface RagChatMessageMapper extends BaseMapper<RagChatMessage> {

    /**
     * 根据会话ID查找所有消息，按消息顺序排序
     * @param sessionId 会话ID
     * @return 消息列表
     */
    @Select("SELECT * FROM rag_chat_message WHERE session_id = #{sessionId} ORDER BY message_order ASC")
    List<RagChatMessage> findBySessionIdOrderByMessageOrder(@Param("sessionId") Long sessionId);

    /**
     * 根据会话ID和消息类型查找消息
     * @param sessionId 会话ID
     * @param type 消息类型（USER/ASSISTANT）
     * @return 消息列表
     */
    @Select("SELECT * FROM rag_chat_message WHERE session_id = #{sessionId} AND type = #{type} ORDER BY message_order ASC")
    List<RagChatMessage> findBySessionIdAndType(@Param("sessionId") Long sessionId, @Param("type") String type);

    /**
     * 根据会话ID查找最后一条消息
     * @param sessionId 会话ID
     * @return 消息对象
     */
    @Select("SELECT * FROM rag_chat_message WHERE session_id = #{sessionId} ORDER BY message_order DESC LIMIT 1")
    RagChatMessage findTopBySessionIdOrderByMessageOrderDesc(@Param("sessionId") Long sessionId);

    /**
     * 根据会话ID统计消息数量
     * @param sessionId 会话ID
     * @return 消息数量
     */
    @Select("SELECT COUNT(*) FROM rag_chat_message WHERE session_id = #{sessionId}")
    int countBySessionId(@Param("sessionId") Long sessionId);

    /**
     * 根据消息类型统计数量
     * @param type 消息类型（USER/ASSISTANT）
     * @return 数量
     */
    @Select("SELECT COUNT(*) FROM rag_chat_message WHERE type = #{type}")
    int countByType(@Param("type") String type);

    /**
     * 根据会话ID查找指定顺序的消息
     * @param sessionId 会话ID
     * @param messageOrder 消息顺序
     * @return 消息对象
     */
    @Select("SELECT * FROM rag_chat_message WHERE session_id = #{sessionId} AND message_order = #{messageOrder}")
    RagChatMessage findBySessionIdAndMessageOrder(@Param("sessionId") Long sessionId, @Param("messageOrder") Integer messageOrder);

    /**
     * 根据会话ID查找未完成的消息（用于流式响应）
     * @param sessionId 会话ID
     * @return 消息列表
     */
    @Select("SELECT * FROM rag_chat_message WHERE session_id = #{sessionId} AND completed = false ORDER BY message_order ASC")
    List<RagChatMessage> findIncompleteMessagesBySessionId(@Param("sessionId") Long sessionId);

    /**
     * 更新消息完成状态
     * @param messageId 消息ID
     * @param content 消息内容
     * @param completed 是否完成
     * @return 影响行数
     */
    @Update("UPDATE rag_chat_message SET content = #{content}, completed = #{completed}, updated_at = NOW() WHERE id = #{messageId}")
    int updateContentAndCompleted(@Param("messageId") Long messageId, @Param("content") String content, @Param("completed") Boolean completed);
}