package com.daliymove.modules.interview.converter;

import com.daliymove.modules.interview.dto.InterviewDetailDTO;
import com.daliymove.modules.interview.dto.InterviewReportDTO;
import com.daliymove.modules.interview.entity.InterviewAnswer;
import com.daliymove.modules.interview.entity.InterviewSession;
import org.mapstruct.*;

import java.util.List;

/**
 * 面试相关的对象映射器
 * - 使用MapStruct自动生成转换代码
 * - JSON字段需要在Service层手动处理
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InterviewConverter {

    /**
     * 将面试答案实体转换为问题评估详情
     */
    @Mapping(target = "questionIndex", source = "questionIndex", qualifiedByName = "nullIndexToZero")
    @Mapping(target = "question", source = "question")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "userAnswer", source = "userAnswer")
    @Mapping(target = "score", source = "score", qualifiedByName = "nullScoreToZero")
    @Mapping(target = "feedback", source = "feedback")
    InterviewReportDTO.QuestionEvaluation toQuestionEvaluation(InterviewAnswer entity);

    /**
     * 批量转换面试答案实体
     */
    List<InterviewReportDTO.QuestionEvaluation> toQuestionEvaluations(List<InterviewAnswer> entities);

    /**
     * InterviewAnswer 转换为 AnswerDetailDTO
     * - keyPoints 需要从 JSON 解析后传入
     */
    @Mapping(target = "keyPoints", source = "keyPoints")
    InterviewDetailDTO.AnswerDetailDTO toAnswerDetailDTO(
        InterviewAnswer entity,
        List<String> keyPoints
    );

    /**
     * 批量转换（需要在 Service 层处理 JSON）
     */
    default List<InterviewDetailDTO.AnswerDetailDTO> toAnswerDetailDTOList(
        List<InterviewAnswer> entities,
        java.util.function.Function<InterviewAnswer, List<String>> keyPointsExtractor
    ) {
        return entities.stream()
            .map(e -> toAnswerDetailDTO(e, keyPointsExtractor.apply(e)))
            .toList();
    }

    /**
     * InterviewSession 转换为 InterviewDetailDTO
     * - questions, strengths, improvements, referenceAnswers, answers 需要在 Service 层处理
     */
    @Mapping(target = "status", source = "session.status")
    @Mapping(target = "evaluateStatus", source = "session.evaluateStatus")
    @Mapping(target = "evaluateError", source = "session.evaluateError")
    @Mapping(target = "questions", source = "questions")
    @Mapping(target = "strengths", source = "strengths")
    @Mapping(target = "improvements", source = "improvements")
    @Mapping(target = "referenceAnswers", source = "referenceAnswers")
    @Mapping(target = "answers", source = "answers")
    InterviewDetailDTO toDetailDTO(
        InterviewSession session,
        List<Object> questions,
        List<String> strengths,
        List<String> improvements,
        List<Object> referenceAnswers,
        List<InterviewDetailDTO.AnswerDetailDTO> answers
    );

    /**
     * InterviewSession 转换为简要信息 Map
     * - 用于 ResumeDetailDTO 中的面试历史列表
     */
    default java.util.Map<String, Object> toInterviewHistoryItem(InterviewSession session) {
        java.util.Map<String, Object> map = new java.util.LinkedHashMap<>();
        map.put("id", session.getId());
        map.put("sessionId", session.getSessionId());
        map.put("totalQuestions", session.getTotalQuestions());
        map.put("status", session.getStatus());
        map.put("evaluateStatus", session.getEvaluateStatus());
        map.put("evaluateError", session.getEvaluateError());
        map.put("overallScore", session.getOverallScore());
        map.put("createdAt", session.getCreatedAt());
        map.put("completedAt", session.getCompletedAt());
        return map;
    }

    /**
     * 批量转换面试历史
     */
    default List<Object> toInterviewHistoryList(List<InterviewSession> sessions) {
        return sessions.stream()
            .map(this::toInterviewHistoryItem)
            .map(m -> (Object) m)
            .toList();
    }

    /**
     * 工具方法：null索引转零
     */
    @Named("nullIndexToZero")
    default int nullIndexToZero(Integer value) {
        return value != null ? value : 0;
    }

    /**
     * 工具方法：null分数转零
     */
    @Named("nullScoreToZero")
    default int nullScoreToZero(Integer value) {
        return value != null ? value : 0;
    }
}