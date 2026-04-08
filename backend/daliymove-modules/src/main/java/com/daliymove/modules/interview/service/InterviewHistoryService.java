package com.daliymove.modules.interview.service;

import com.daliymove.common.exception.BusinessException;
import com.daliymove.common.exception.ErrorCode;
import com.daliymove.modules.interview.converter.InterviewConverter;
import com.daliymove.modules.interview.dto.InterviewDetailDTO;
import com.daliymove.modules.interview.dto.InterviewQuestionDTO;
import com.daliymove.modules.interview.entity.InterviewAnswer;
import com.daliymove.modules.interview.entity.InterviewSession;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 面试历史服务
 * - 获取面试会话详情和导出面试报告
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InterviewHistoryService {

    private final InterviewPersistenceService persistenceService;
    private final ObjectMapper objectMapper;
    private final InterviewConverter interviewConverter;

    /**
     * 获取面试会话详情
     */
    public InterviewDetailDTO getInterviewDetail(String sessionId) {
        InterviewSession session = persistenceService.findBySessionId(sessionId);
        if (session == null) {
            throw new BusinessException(ErrorCode.INTERVIEW_SESSION_NOT_FOUND);
        }

        List<Object> questions = parseJson(session.getQuestionsJson(), new TypeReference<>() {});
        List<String> strengths = parseJson(session.getStrengthsJson(), new TypeReference<>() {});
        List<String> improvements = parseJson(session.getImprovementsJson(), new TypeReference<>() {});
        List<Object> referenceAnswers = parseJson(session.getReferenceAnswersJson(), new TypeReference<>() {});

        List<InterviewQuestionDTO> allQuestions = parseJson(
            session.getQuestionsJson(),
            new TypeReference<>() {}
        );

        List<InterviewAnswer> answers = persistenceService.findAnswersBySessionId(sessionId);

        List<InterviewDetailDTO.AnswerDetailDTO> answerList = buildAnswerDetailList(
            allQuestions,
            answers
        );

        return interviewConverter.toDetailDTO(
            session,
            questions,
            strengths,
            improvements,
            referenceAnswers,
            answerList
        );
    }

    /**
     * 构建答案详情列表（包含所有题目）
     */
    private List<InterviewDetailDTO.AnswerDetailDTO> buildAnswerDetailList(
        List<InterviewQuestionDTO> allQuestions,
        List<InterviewAnswer> answers
    ) {
        if (allQuestions == null || allQuestions.isEmpty()) {
            return interviewConverter.toAnswerDetailDTOList(answers, this::extractKeyPoints);
        }

        Map<Integer, InterviewAnswer> answerMap = answers.stream()
            .collect(Collectors.toMap(
                InterviewAnswer::getQuestionIndex,
                a -> a,
                (a1, a2) -> a1
            ));

        return allQuestions.stream()
            .map(question -> {
                InterviewAnswer answer = answerMap.get(question.questionIndex());
                if (answer != null) {
                    return interviewConverter.toAnswerDetailDTO(answer, extractKeyPoints(answer));
                } else {
                    return new InterviewDetailDTO.AnswerDetailDTO(
                        question.questionIndex(),
                        question.question(),
                        question.category(),
                        null,
                        question.score() != null ? question.score() : 0,
                        question.feedback(),
                        null,
                        null,
                        null
                    );
                }
            })
            .toList();
    }

    /**
     * 从 JSON 提取 keyPoints
     */
    private List<String> extractKeyPoints(InterviewAnswer answer) {
        return parseJson(answer.getKeyPointsJson(), new TypeReference<>() {});
    }

    /**
     * 通用 JSON 解析方法
     */
    private <T> T parseJson(String json, TypeReference<T> typeRef) {
        if (json == null) {
            return null;
        }
        try {
            return objectMapper.readValue(json, typeRef);
        } catch (JsonProcessingException e) {
            log.error("解析 JSON 失败", e);
            return null;
        }
    }
}