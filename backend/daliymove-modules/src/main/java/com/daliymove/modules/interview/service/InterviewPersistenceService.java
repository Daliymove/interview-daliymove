package com.daliymove.modules.interview.service;

import com.daliymove.common.enums.AsyncTaskStatus;
import com.daliymove.common.exception.BusinessException;
import com.daliymove.common.exception.ErrorCode;
import com.daliymove.modules.interview.dto.InterviewQuestionDTO;
import com.daliymove.modules.interview.dto.InterviewReportDTO;
import com.daliymove.modules.interview.entity.InterviewAnswer;
import com.daliymove.modules.interview.entity.InterviewSession;
import com.daliymove.modules.interview.mapper.InterviewAnswerMapper;
import com.daliymove.modules.interview.mapper.InterviewSessionMapper;
import com.daliymove.modules.resume.entity.Resume;
import com.daliymove.modules.resume.mapper.ResumeMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 面试持久化服务
 * - 面试会话和答案的持久化
 * - 使用 MyBatis-Plus 进行数据库操作
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InterviewPersistenceService {

    private final InterviewSessionMapper sessionMapper;
    private final InterviewAnswerMapper answerMapper;
    private final ResumeMapper resumeMapper;
    private final ObjectMapper objectMapper;

    /**
     * 保存新的面试会话
     */
    @Transactional(rollbackFor = Exception.class)
    public InterviewSession saveSession(String sessionId, Long resumeId,
                                        int totalQuestions,
                                        List<InterviewQuestionDTO> questions) {
        try {
            Resume resume = resumeMapper.selectById(resumeId);
            if (resume == null) {
                throw new BusinessException(ErrorCode.RESUME_NOT_FOUND);
            }

            InterviewSession session = new InterviewSession();
            session.setSessionId(sessionId);
            session.setResumeId(resumeId);
            session.setTotalQuestions(totalQuestions);
            session.setCurrentQuestionIndex(0);
            session.setStatus(InterviewSession.SessionStatus.CREATED.name());
            session.setQuestionsJson(objectMapper.writeValueAsString(questions));

            sessionMapper.insert(session);
            log.info("面试会话已保存: sessionId={}, resumeId={}", sessionId, resumeId);

            return session;
        } catch (JsonProcessingException e) {
            log.error("序列化问题列表失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "保存会话失败");
        }
    }

    /**
     * 更新会话状态
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateSessionStatus(String sessionId, InterviewSession.SessionStatus status) {
        InterviewSession session = sessionMapper.findBySessionId(sessionId);
        if (session != null) {
            session.setStatus(status.name());
            if (status == InterviewSession.SessionStatus.COMPLETED ||
                status == InterviewSession.SessionStatus.EVALUATED) {
                session.setCompletedAt(LocalDateTime.now());
            }
            sessionMapper.updateById(session);
        }
    }

    /**
     * 更新评估状态
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateEvaluateStatus(String sessionId, AsyncTaskStatus status, String error) {
        InterviewSession session = sessionMapper.findBySessionId(sessionId);
        if (session != null) {
            session.setEvaluateStatus(status.name());
            if (error != null) {
                session.setEvaluateError(error.length() > 500 ? error.substring(0, 500) : error);
            } else {
                session.setEvaluateError(null);
            }
            sessionMapper.updateById(session);
            log.debug("评估状态已更新: sessionId={}, status={}", sessionId, status);
        }
    }

    /**
     * 更新当前问题索引
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateCurrentQuestionIndex(String sessionId, int index) {
        InterviewSession session = sessionMapper.findBySessionId(sessionId);
        if (session != null) {
            session.setCurrentQuestionIndex(index);
            session.setStatus(InterviewSession.SessionStatus.IN_PROGRESS.name());
            sessionMapper.updateById(session);
        }
    }

    /**
     * 保存面试答案
     */
    @Transactional(rollbackFor = Exception.class)
    public InterviewAnswer saveAnswer(String sessionId, int questionIndex,
                                      String question, String category,
                                      String userAnswer, int score, String feedback) {
        InterviewSession session = sessionMapper.findBySessionId(sessionId);
        if (session == null) {
            throw new BusinessException(ErrorCode.INTERVIEW_SESSION_NOT_FOUND);
        }

        InterviewAnswer answer = answerMapper.findBySessionUuidAndQuestionIndex(sessionId, questionIndex);
        if (answer == null) {
            answer = new InterviewAnswer();
            answer.setSessionId(session.getId());
            answer.setQuestionIndex(questionIndex);
        }

        answer.setQuestion(question);
        answer.setCategory(category);
        answer.setUserAnswer(userAnswer);
        answer.setScore(score);
        answer.setFeedback(feedback);

        if (answer.getId() == null) {
            answerMapper.insert(answer);
        } else {
            answerMapper.updateById(answer);
        }
        log.info("面试答案已保存: sessionId={}, questionIndex={}, score={}",
                sessionId, questionIndex, score);

        return answer;
    }

    /**
     * 保存面试报告
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveReport(String sessionId, InterviewReportDTO report) {
        try {
            InterviewSession session = sessionMapper.findBySessionId(sessionId);
            if (session == null) {
                log.warn("会话不存在: {}", sessionId);
                return;
            }

            session.setOverallScore(report.overallScore());
            session.setOverallFeedback(report.overallFeedback());
            session.setStrengthsJson(objectMapper.writeValueAsString(report.strengths()));
            session.setImprovementsJson(objectMapper.writeValueAsString(report.improvements()));
            session.setReferenceAnswersJson(objectMapper.writeValueAsString(report.referenceAnswers()));
            session.setStatus(InterviewSession.SessionStatus.EVALUATED.name());
            session.setCompletedAt(LocalDateTime.now());

            sessionMapper.updateById(session);

            List<InterviewAnswer> existingAnswers = answerMapper.findBySessionUuidOrderByQuestionIndex(sessionId);
            Map<Integer, InterviewAnswer> answerMap = existingAnswers.stream()
                .collect(Collectors.toMap(
                    InterviewAnswer::getQuestionIndex,
                    a -> a,
                    (a1, a2) -> a1
                ));

            Map<Integer, InterviewReportDTO.ReferenceAnswer> refAnswerMap = report.referenceAnswers().stream()
                .collect(Collectors.toMap(
                    InterviewReportDTO.ReferenceAnswer::questionIndex,
                    r -> r,
                    (r1, r2) -> r1
                ));

            List<InterviewAnswer> answersToSave = new ArrayList<>();

            for (InterviewReportDTO.QuestionEvaluation eval : report.questionDetails()) {
                InterviewAnswer answer = answerMap.get(eval.questionIndex());

                if (answer == null) {
                    answer = new InterviewAnswer();
                    answer.setSessionId(session.getId());
                    answer.setQuestionIndex(eval.questionIndex());
                    answer.setQuestion(eval.question());
                    answer.setCategory(eval.category());
                    answer.setUserAnswer(null);
                    log.debug("为未回答的题目 {} 创建答案记录", eval.questionIndex());
                }

                answer.setScore(eval.score());
                answer.setFeedback(eval.feedback());

                InterviewReportDTO.ReferenceAnswer refAns = refAnswerMap.get(eval.questionIndex());
                if (refAns != null) {
                    answer.setReferenceAnswer(refAns.referenceAnswer());
                    if (refAns.keyPoints() != null && !refAns.keyPoints().isEmpty()) {
                        answer.setKeyPointsJson(objectMapper.writeValueAsString(refAns.keyPoints()));
                    }
                }

                answersToSave.add(answer);
            }

            for (InterviewAnswer ans : answersToSave) {
                if (ans.getId() == null) {
                    answerMapper.insert(ans);
                } else {
                    answerMapper.updateById(ans);
                }
            }
            log.info("面试报告已保存: sessionId={}, score={}, 答案数={}",
                sessionId, report.overallScore(), answersToSave.size());

        } catch (JsonProcessingException e) {
            log.error("序列化报告失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 根据会话ID获取会话
     */
    public InterviewSession findBySessionId(String sessionId) {
        return sessionMapper.findBySessionId(sessionId);
    }

    /**
     * 获取简历的所有面试记录
     */
    public List<InterviewSession> findByResumeId(Long resumeId) {
        return sessionMapper.findByResumeIdOrderByCreatedAtDesc(resumeId);
    }

    /**
     * 删除简历的所有面试会话
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteSessionsByResumeId(Long resumeId) {
        List<InterviewSession> sessions = sessionMapper.findByResumeIdOrderByCreatedAtDesc(resumeId);
        if (!sessions.isEmpty()) {
            sessions.forEach(s -> sessionMapper.deleteById(s.getId()));
            log.info("已删除 {} 个面试会话（包含所有答案）", sessions.size());
        }
    }

    /**
     * 删除单个面试会话
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteSessionBySessionId(String sessionId) {
        InterviewSession session = sessionMapper.findBySessionId(sessionId);
        if (session != null) {
            sessionMapper.deleteById(session.getId());
            log.info("已删除面试会话: sessionId={}", sessionId);
        } else {
            throw new BusinessException(ErrorCode.INTERVIEW_SESSION_NOT_FOUND);
        }
    }

    /**
     * 查找未完成的面试会话（CREATED或IN_PROGRESS状态）
     */
    public InterviewSession findUnfinishedSession(Long resumeId) {
        List<String> unfinishedStatuses = List.of(
            InterviewSession.SessionStatus.CREATED.name(),
            InterviewSession.SessionStatus.IN_PROGRESS.name()
        );
        return sessionMapper.findFirstByResumeIdAndStatusInOrderByCreatedAtDesc(resumeId, unfinishedStatuses);
    }

    /**
     * 根据会话ID查找所有答案
     */
    public List<InterviewAnswer> findAnswersBySessionId(String sessionId) {
        return answerMapper.findBySessionUuidOrderByQuestionIndex(sessionId);
    }

    /**
     * 获取简历的历史提问列表（限制最近的 N 条）
     */
    public List<String> getHistoricalQuestionsByResumeId(Long resumeId) {
        List<InterviewSession> sessions = sessionMapper.findTop10ByResumeIdOrderByCreatedAtDesc(resumeId);

        return sessions.stream()
            .map(InterviewSession::getQuestionsJson)
            .filter(json -> json != null && !json.isEmpty())
            .flatMap(json -> {
                try {
                    List<InterviewQuestionDTO> questions = objectMapper.readValue(json,
                        new TypeReference<List<InterviewQuestionDTO>>() {});
                    return questions.stream()
                        .filter(q -> !q.isFollowUp())
                        .map(InterviewQuestionDTO::question);
                } catch (Exception e) {
                    log.error("解析历史问题JSON失败", e);
                    return java.util.stream.Stream.empty();
                }
            })
            .distinct()
            .limit(30)
            .toList();
    }
}