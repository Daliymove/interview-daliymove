package com.daliymove.modules.interview.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.daliymove.modules.interview.entity.InterviewSession;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 面试会话Mapper接口
 * - 提供面试会话的数据访问操作
 * - 包含基于会话ID、简历ID和状态的查询方法
 */
@Mapper
public interface InterviewSessionMapper extends BaseMapper<InterviewSession> {

    /**
     * 根据会话ID（UUID）查找面试会话
     * @param sessionId 会话ID（UUID字符串）
     * @return 面试会话对象
     */
    @Select("SELECT * FROM interview_session WHERE session_id = #{sessionId}")
    InterviewSession findBySessionId(@Param("sessionId") String sessionId);

    /**
     * 根据简历ID查找所有面试记录，按创建时间降序
     * @param resumeId 简历ID
     * @return 面试会话列表
     */
    @Select("SELECT * FROM interview_session WHERE resume_id = #{resumeId} ORDER BY created_at DESC")
    List<InterviewSession> findByResumeIdOrderByCreatedAtDesc(@Param("resumeId") Long resumeId);

    /**
     * 根据简历ID查找最近的10条面试记录（用于历史题去重）
     * @param resumeId 简历ID
     * @return 面试会话列表（最多10条）
     */
    @Select("SELECT * FROM interview_session WHERE resume_id = #{resumeId} ORDER BY created_at DESC LIMIT 10")
    List<InterviewSession> findTop10ByResumeIdOrderByCreatedAtDesc(@Param("resumeId") Long resumeId);

    /**
     * 查找简历的未完成面试（指定状态列表），按创建时间降序取第一条
     * @param resumeId 简历ID
     * @param statuses 状态列表
     * @return 面试会话对象
     */
    @Select("<script>" +
            "SELECT * FROM interview_session WHERE resume_id = #{resumeId} " +
            "AND status IN " +
            "<foreach collection='statuses' item='status' open='(' separator=',' close=')'>" +
            "#{status}" +
            "</foreach>" +
            " ORDER BY created_at DESC LIMIT 1" +
            "</script>")
    InterviewSession findFirstByResumeIdAndStatusInOrderByCreatedAtDesc(
            @Param("resumeId") Long resumeId,
            @Param("statuses") List<String> statuses
    );

    /**
     * 根据简历ID和状态查找会话
     * @param resumeId 简历ID
     * @param statuses 状态列表
     * @return 面试会话对象
     */
    @Select("<script>" +
            "SELECT * FROM interview_session WHERE resume_id = #{resumeId} " +
            "AND status IN " +
            "<foreach collection='statuses' item='status' open='(' separator=',' close=')'>" +
            "#{status}" +
            "</foreach>" +
            "</script>")
    InterviewSession findByResumeIdAndStatusIn(
            @Param("resumeId") Long resumeId,
            @Param("statuses") List<String> statuses
    );
}