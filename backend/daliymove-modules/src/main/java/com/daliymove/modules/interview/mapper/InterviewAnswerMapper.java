package com.daliymove.modules.interview.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.daliymove.modules.interview.entity.InterviewAnswer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 面试答案Mapper接口
 * - 提供面试答案的数据访问操作
 * - 支持按会话和问题索引查询答案
 */
@Mapper
public interface InterviewAnswerMapper extends BaseMapper<InterviewAnswer> {

    /**
     * 根据会话ID查找所有答案，按问题索引排序
     * @param sessionId 会话ID（数据库主键）
     * @return 面试答案列表
     */
    @Select("SELECT * FROM interview_answer WHERE session_id = #{sessionId} ORDER BY question_index")
    List<InterviewAnswer> findBySessionIdOrderByQuestionIndex(@Param("sessionId") Long sessionId);

    /**
     * 根据会话的UUID字符串查找所有答案，按问题索引排序
     * @param sessionUuid 会话的UUID字符串
     * @return 面试答案列表
     */
    @Select("SELECT a.* FROM interview_answer a " +
            "INNER JOIN interview_session s ON a.session_id = s.id " +
            "WHERE s.session_id = #{sessionUuid} ORDER BY a.question_index")
    List<InterviewAnswer> findBySessionUuidOrderByQuestionIndex(@Param("sessionUuid") String sessionUuid);

    /**
     * 根据会话UUID和问题索引查找单条答案（用于upsert）
     * @param sessionUuid 会话的UUID字符串
     * @param questionIndex 问题索引
     * @return 面试答案对象
     */
    @Select("SELECT a.* FROM interview_answer a " +
            "INNER JOIN interview_session s ON a.session_id = s.id " +
            "WHERE s.session_id = #{sessionUuid} AND a.question_index = #{questionIndex}")
    InterviewAnswer findBySessionUuidAndQuestionIndex(
            @Param("sessionUuid") String sessionUuid,
            @Param("questionIndex") Integer questionIndex
    );
}