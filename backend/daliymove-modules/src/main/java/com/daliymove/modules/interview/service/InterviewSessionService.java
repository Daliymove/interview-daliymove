package com.daliymove.modules.interview.service;

import com.daliymove.common.enums.AsyncTaskStatus;
import com.daliymove.common.exception.BusinessException;
import com.daliymove.common.exception.ErrorCode;
import com.daliymove.common.redis.RedisService;
import com.daliymove.modules.interview.dto.CreateInterviewRequest;
import com.daliymove.modules.interview.dto.InterviewQuestionDTO;
import com.daliymove.modules.interview.dto.InterviewReportDTO;
import com.daliymove.modules.interview.dto.InterviewSessionDTO;
import com.daliymove.modules.interview.dto.InterviewSessionDTO.SessionStatus;
import com.daliymove.modules.interview.dto.SubmitAnswerRequest;
import com.daliymove.modules.interview.dto.SubmitAnswerResponse;
import com.daliymove.modules.interview.entity.InterviewAnswer;
import com.daliymove.modules.interview.entity.InterviewSession;
import com.daliymove.modules.interview.listener.EvaluateStreamProducer;
import com.daliymove.modules.resume.entity.Resume;
import com.daliymove.modules.resume.mapper.ResumeMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 面试会话管理服务
 * - 管理面试会话的生命周期
 * - 使用 Redis 缓存会话状态
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InterviewSessionService {

    private static final String SESSION_CACHE_PREFIX = "interview:session:";
    private static final long SESSION_TTL_HOURS = 2;

    private final InterviewQuestionService questionService;
    private final AnswerEvaluationService evaluationService;
    private final InterviewPersistenceService persistenceService;
    private final ObjectMapper objectMapper;
    private final EvaluateStreamProducer evaluateStreamProducer;
    private final RedisService redisService;
    private final ResumeMapper resumeMapper;

    /**
     * 创建新的面试会话
     */
    public InterviewSessionDTO createSession(CreateInterviewRequest request) {
        if (request.resumeId() != null && !Boolean.TRUE.equals(request.forceCreate())) {
            InterviewSessionDTO unfinishedOpt = findUnfinishedSession(request.resumeId());
            if (unfinishedOpt != null) {
                log.info("检测到未完成的面试会话，返回现有会话: resumeId={}, sessionId={}",
                    request.resumeId(), unfinishedOpt.sessionId());
                return unfinishedOpt;
            }
        }

        String sessionId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);

        log.info("创建新面试会话: {}, 题目数量: {}, resumeId: {}",
            sessionId, request.questionCount(), request.resumeId());

        List<String> historicalQuestions = null;
        if (request.resumeId() != null) {
            historicalQuestions = persistenceService.getHistoricalQuestionsByResumeId(request.resumeId());
        }

        List<InterviewQuestionDTO> questions = questionService.generateQuestions(
            request.resumeText(),
            request.questionCount(),
            historicalQuestions
        );

        saveSessionToCache(
            sessionId,
            request.resumeText(),
            request.resumeId(),
            questions,
            0,
            SessionStatus.CREATED
        );

        if (request.resumeId() != null) {
            try {
                persistenceService.saveSession(sessionId, request.resumeId(),
                    questions.size(), questions);
            } catch (Exception e) {
                log.warn("保存面试会话到数据库失败: {}", e.getMessage());
            }
        }

        return new InterviewSessionDTO(
            sessionId,
            request.resumeText(),
            questions.size(),
            0,
            questions,
            SessionStatus.CREATED
        );
    }

    /**
     * 获取会话信息（优先从缓存获取）
     */
    public InterviewSessionDTO getSession(String sessionId) {
        CachedSession cachedOpt = getSessionFromCache(sessionId);
        if (cachedOpt != null) {
            return toDTO(cachedOpt);
        }

        CachedSession restoredSession = restoreSessionFromDatabase(sessionId);
        if (restoredSession == null) {
            throw new BusinessException(ErrorCode.INTERVIEW_SESSION_NOT_FOUND);
        }

        return toDTO(restoredSession);
    }

    /**
     * 查找并恢复未完成的面试会话
     */
    public InterviewSessionDTO findUnfinishedSession(Long resumeId) {
        try {
            String cachedSessionId = findUnfinishedSessionIdFromCache(resumeId);
            if (cachedSessionId != null) {
                CachedSession cachedOpt = getSessionFromCache(cachedSessionId);
                if (cachedOpt != null) {
                    log.debug("从 Redis 缓存找到未完成会话: resumeId={}, sessionId={}", resumeId, cachedSessionId);
                    return toDTO(cachedOpt);
                }
            }

            InterviewSession entity = persistenceService.findUnfinishedSession(resumeId);
            if (entity == null) {
                return null;
            }

            CachedSession restoredSession = restoreSessionFromEntity(entity);
            if (restoredSession != null) {
                return toDTO(restoredSession);
            }
        } catch (Exception e) {
            log.error("恢复未完成会话失败: {}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * 查找并恢复未完成的面试会话，如果不存在则抛出异常
     */
    public InterviewSessionDTO findUnfinishedSessionOrThrow(Long resumeId) {
        InterviewSessionDTO session = findUnfinishedSession(resumeId);
        if (session == null) {
            throw new BusinessException(ErrorCode.INTERVIEW_SESSION_NOT_FOUND, "未找到未完成的面试会话");
        }
        return session;
    }

    /**
     * 从数据库恢复会话并缓存到 Redis
     */
    private CachedSession restoreSessionFromDatabase(String sessionId) {
        try {
            InterviewSession entity = persistenceService.findBySessionId(sessionId);
            if (entity == null) {
                return null;
            }
            return restoreSessionFromEntity(entity);
        } catch (Exception e) {
            log.error("从数据库恢复会话失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 从实体恢复会话并缓存到 Redis
     */
    private CachedSession restoreSessionFromEntity(InterviewSession entity) {
        try {
            List<InterviewQuestionDTO> questions = objectMapper.readValue(
                entity.getQuestionsJson(),
                new TypeReference<>() {}
            );

            Resume resume = resumeMapper.selectById(entity.getResumeId());
            String resumeText = resume != null ? resume.getResumeText() : "";

            List<InterviewAnswer> answers = persistenceService.findAnswersBySessionId(entity.getSessionId());
            for (InterviewAnswer answer : answers) {
                int index = answer.getQuestionIndex();
                if (index >= 0 && index < questions.size()) {
                    InterviewQuestionDTO question = questions.get(index);
                    questions.set(index, question.withAnswer(answer.getUserAnswer()));
                }
            }

            SessionStatus status = convertStatus(entity.getStatus());

            saveSessionToCache(
                entity.getSessionId(),
                resumeText,
                entity.getResumeId(),
                questions,
                entity.getCurrentQuestionIndex(),
                status
            );

            log.info("从数据库恢复会话到 Redis: sessionId={}, currentIndex={}, status={}",
                entity.getSessionId(), entity.getCurrentQuestionIndex(), entity.getStatus());

            return getSessionFromCache(entity.getSessionId());

        } catch (Exception e) {
            log.error("恢复会话失败: {}", e.getMessage(), e);
            return null;
        }
    }

    private SessionStatus convertStatus(String status) {
        return switch (status) {
            case "CREATED" -> SessionStatus.CREATED;
            case "IN_PROGRESS" -> SessionStatus.IN_PROGRESS;
            case "COMPLETED" -> SessionStatus.COMPLETED;
            case "EVALUATED" -> SessionStatus.EVALUATED;
            default -> SessionStatus.CREATED;
        };
    }

    /**
     * 获取当前问题的响应
     */
    public Map<String, Object> getCurrentQuestionResponse(String sessionId) {
        InterviewQuestionDTO question = getCurrentQuestion(sessionId);
        if (question == null) {
            return Map.of(
                "completed", true,
                "message", "所有问题已回答完毕"
            );
        }
        return Map.of(
            "completed", false,
            "question", question
        );
    }

    /**
     * 获取当前问题
     */
    public InterviewQuestionDTO getCurrentQuestion(String sessionId) {
        CachedSession session = getOrRestoreSession(sessionId);
        List<InterviewQuestionDTO> questions = session.questions;

        if (session.currentIndex >= questions.size()) {
            return null;
        }

        if (session.status == SessionStatus.CREATED) {
            updateSessionStatusInCache(sessionId, SessionStatus.IN_PROGRESS);

            try {
                persistenceService.updateSessionStatus(sessionId,
                    InterviewSession.SessionStatus.IN_PROGRESS);
            } catch (Exception e) {
                log.warn("更新会话状态失败: {}", e.getMessage());
            }
        }

        return questions.get(session.currentIndex);
    }

    /**
     * 提交答案
     */
    public SubmitAnswerResponse submitAnswer(SubmitAnswerRequest request) {
        CachedSession session = getOrRestoreSession(request.sessionId());
        List<InterviewQuestionDTO> questions = session.questions;

        int index = request.questionIndex();
        if (index < 0 || index >= questions.size()) {
            throw new BusinessException(ErrorCode.INTERVIEW_QUESTION_NOT_FOUND, "无效的问题索引: " + index);
        }

        InterviewQuestionDTO question = questions.get(index);
        InterviewQuestionDTO answeredQuestion = question.withAnswer(request.answer());
        questions.set(index, answeredQuestion);

        int newIndex = index + 1;

        boolean hasNextQuestion = newIndex < questions.size();
        InterviewQuestionDTO nextQuestion = hasNextQuestion ? questions.get(newIndex) : null;

        SessionStatus newStatus = hasNextQuestion ? SessionStatus.IN_PROGRESS : SessionStatus.COMPLETED;

        updateQuestionsInCache(request.sessionId(), questions);
        updateCurrentIndexInCache(request.sessionId(), newIndex);
        if (newStatus == SessionStatus.COMPLETED) {
            updateSessionStatusInCache(request.sessionId(), SessionStatus.COMPLETED);
        }

        try {
            persistenceService.saveAnswer(
                request.sessionId(), index,
                question.question(), question.category(),
                request.answer(), 0, null
            );
            persistenceService.updateCurrentQuestionIndex(request.sessionId(), newIndex);
            persistenceService.updateSessionStatus(request.sessionId(),
                newStatus == SessionStatus.COMPLETED
                    ? InterviewSession.SessionStatus.COMPLETED
                    : InterviewSession.SessionStatus.IN_PROGRESS);

            if (!hasNextQuestion) {
                persistenceService.updateEvaluateStatus(request.sessionId(), AsyncTaskStatus.PENDING, null);
                evaluateStreamProducer.sendEvaluateTask(request.sessionId());
                log.info("会话 {} 已完成所有问题，评估任务已入队", request.sessionId());
            }
        } catch (Exception e) {
            log.warn("保存答案到数据库失败: {}", e.getMessage());
        }

        log.info("会话 {} 提交答案: 问题{}, 剩余{}题",
            request.sessionId(), index, questions.size() - newIndex);

        return new SubmitAnswerResponse(
            hasNextQuestion,
            nextQuestion,
            newIndex,
            questions.size()
        );
    }

    /**
     * 暂存答案
     */
    public void saveAnswer(SubmitAnswerRequest request) {
        CachedSession session = getOrRestoreSession(request.sessionId());
        List<InterviewQuestionDTO> questions = session.questions;

        int index = request.questionIndex();
        if (index < 0 || index >= questions.size()) {
            throw new BusinessException(ErrorCode.INTERVIEW_QUESTION_NOT_FOUND, "无效的问题索引: " + index);
        }

        InterviewQuestionDTO question = questions.get(index);
        InterviewQuestionDTO answeredQuestion = question.withAnswer(request.answer());
        questions.set(index, answeredQuestion);

        updateQuestionsInCache(request.sessionId(), questions);

        if (session.status == SessionStatus.CREATED) {
            updateSessionStatusInCache(request.sessionId(), SessionStatus.IN_PROGRESS);
        }

        try {
            persistenceService.saveAnswer(
                request.sessionId(), index,
                question.question(), question.category(),
                request.answer(), 0, null
            );
            persistenceService.updateSessionStatus(request.sessionId(),
                InterviewSession.SessionStatus.IN_PROGRESS);
        } catch (Exception e) {
            log.warn("暂存答案到数据库失败: {}", e.getMessage());
        }

        log.info("会话 {} 暂存答案: 问题{}", request.sessionId(), index);
    }

    /**
     * 提前交卷
     */
    public void completeInterview(String sessionId) {
        CachedSession session = getOrRestoreSession(sessionId);

        if (session.status == SessionStatus.COMPLETED || session.status == SessionStatus.EVALUATED) {
            throw new BusinessException(ErrorCode.INTERVIEW_ALREADY_COMPLETED);
        }

        updateSessionStatusInCache(sessionId, SessionStatus.COMPLETED);

        try {
            persistenceService.updateSessionStatus(sessionId,
                InterviewSession.SessionStatus.COMPLETED);
            persistenceService.updateEvaluateStatus(sessionId, AsyncTaskStatus.PENDING, null);
        } catch (Exception e) {
            log.warn("更新会话状态失败: {}", e.getMessage());
        }

        evaluateStreamProducer.sendEvaluateTask(sessionId);

        log.info("会话 {} 提前交卷，评估任务已入队", sessionId);
    }

    /**
     * 获取或恢复会话
     */
    private CachedSession getOrRestoreSession(String sessionId) {
        CachedSession cachedOpt = getSessionFromCache(sessionId);
        if (cachedOpt != null) {
            refreshSessionTTL(sessionId);
            return cachedOpt;
        }

        CachedSession restoredSession = restoreSessionFromDatabase(sessionId);
        if (restoredSession == null) {
            throw new BusinessException(ErrorCode.INTERVIEW_SESSION_NOT_FOUND);
        }

        return restoredSession;
    }

    /**
     * 生成评估报告
     */
    public InterviewReportDTO generateReport(String sessionId) {
        CachedSession session = getOrRestoreSession(sessionId);

        if (session.status != SessionStatus.COMPLETED && session.status != SessionStatus.EVALUATED) {
            throw new BusinessException(ErrorCode.INTERVIEW_NOT_COMPLETED, "面试尚未完成，无法生成报告");
        }

        log.info("生成面试报告: {}", sessionId);

        InterviewReportDTO report = evaluationService.evaluateInterview(
            sessionId,
            session.resumeText,
            session.questions
        );

        updateSessionStatusInCache(sessionId, SessionStatus.EVALUATED);

        try {
            persistenceService.saveReport(sessionId, report);
        } catch (Exception e) {
            log.warn("保存报告到数据库失败: {}", e.getMessage());
        }

        return report;
    }

    /**
     * 重新评估面试
     */
    public void reevaluateInterview(String sessionId) {
        InterviewSession session = persistenceService.findBySessionId(sessionId);
        if (session == null) {
            throw new BusinessException(ErrorCode.INTERVIEW_SESSION_NOT_FOUND);
        }

        if (!session.getStatus().equals(InterviewSession.SessionStatus.COMPLETED.name()) &&
            !session.getStatus().equals(InterviewSession.SessionStatus.EVALUATED.name())) {
            throw new BusinessException(ErrorCode.INTERVIEW_NOT_COMPLETED, "面试尚未完成，无法重新评估");
        }

        persistenceService.updateEvaluateStatus(sessionId, AsyncTaskStatus.PENDING, null);

        evaluateStreamProducer.sendEvaluateTask(sessionId);

        log.info("面试重新评估任务已入队: sessionId={}", sessionId);
    }

    private void saveSessionToCache(String sessionId, String resumeText, Long resumeId,
                                    List<InterviewQuestionDTO> questions, int currentIndex, SessionStatus status) {
        try {
            String key = SESSION_CACHE_PREFIX + sessionId;

            Map<String, Object> sessionData = new HashMap<>();
            sessionData.put("sessionId", sessionId);
            sessionData.put("resumeText", resumeText);
            sessionData.put("resumeId", resumeId);
            sessionData.put("questions", objectMapper.writeValueAsString(questions));
            sessionData.put("currentIndex", currentIndex);
            sessionData.put("status", status.name());

            redisService.hMSet(key, sessionData);
            redisService.expire(key, Duration.ofHours(SESSION_TTL_HOURS));

            if (resumeId != null) {
                String unfinishedKey = "interview:unfinished:" + resumeId;
                redisService.set(unfinishedKey, sessionId, Duration.ofHours(SESSION_TTL_HOURS));
            }
        } catch (Exception e) {
            log.warn("保存会话到 Redis 失败: {}", e.getMessage());
        }
    }

    private CachedSession getSessionFromCache(String sessionId) {
        try {
            String key = SESSION_CACHE_PREFIX + sessionId;
            Map<String, Object> data = redisService.hGetAll(key);
            if (data == null || data.isEmpty()) {
                return null;
            }

            List<InterviewQuestionDTO> questions = objectMapper.readValue(
                (String) data.get("questions"),
                new TypeReference<>() {}
            );

            return new CachedSession(
                (String) data.get("sessionId"),
                (String) data.get("resumeText"),
                (Long) data.get("resumeId"),
                questions,
                (Integer) data.get("currentIndex"),
                SessionStatus.valueOf((String) data.get("status"))
            );
        } catch (Exception e) {
            log.warn("从 Redis 获取会话失败: {}", e.getMessage());
            return null;
        }
    }

    private String findUnfinishedSessionIdFromCache(Long resumeId) {
        try {
            String unfinishedKey = "interview:unfinished:" + resumeId;
            String sessionId = redisService.get(unfinishedKey);
            return sessionId;
        } catch (Exception e) {
            log.warn("查找未完成会话ID失败: {}", e.getMessage());
            return null;
        }
    }

    private void updateQuestionsInCache(String sessionId, List<InterviewQuestionDTO> questions) {
        try {
            String key = SESSION_CACHE_PREFIX + sessionId;
            redisService.hSet(key, "questions", objectMapper.writeValueAsString(questions));
        } catch (Exception e) {
            log.warn("更新 Redis 问题列表失败: {}", e.getMessage());
        }
    }

    private void updateCurrentIndexInCache(String sessionId, int index) {
        try {
            String key = SESSION_CACHE_PREFIX + sessionId;
            redisService.hSet(key, "currentIndex", index);
        } catch (Exception e) {
            log.warn("更新 Redis 当前索引失败: {}", e.getMessage());
        }
    }

    private void updateSessionStatusInCache(String sessionId, SessionStatus status) {
        try {
            String key = SESSION_CACHE_PREFIX + sessionId;
            redisService.hSet(key, "status", status.name());
        } catch (Exception e) {
            log.warn("更新 Redis 会话状态失败: {}", e.getMessage());
        }
    }

    private void refreshSessionTTL(String sessionId) {
        try {
            String key = SESSION_CACHE_PREFIX + sessionId;
            redisService.expire(key, Duration.ofHours(SESSION_TTL_HOURS));
        } catch (Exception e) {
            log.warn("刷新 Redis TTL 失败: {}", e.getMessage());
        }
    }

    private InterviewSessionDTO toDTO(CachedSession session) {
        return new InterviewSessionDTO(
            session.sessionId,
            session.resumeText,
            session.questions.size(),
            session.currentIndex,
            session.questions,
            session.status
        );
    }

    private record CachedSession(
        String sessionId,
        String resumeText,
        Long resumeId,
        List<InterviewQuestionDTO> questions,
        int currentIndex,
        SessionStatus status
    ) {}
}