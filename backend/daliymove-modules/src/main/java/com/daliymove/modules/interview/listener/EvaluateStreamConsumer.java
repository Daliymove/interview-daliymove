package com.daliymove.modules.interview.listener;

import com.daliymove.common.async.AbstractStreamConsumer;
import com.daliymove.common.constant.AsyncTaskStreamConstants;
import com.daliymove.common.enums.AsyncTaskStatus;
import com.daliymove.common.redis.RedisService;
import com.daliymove.modules.interview.dto.InterviewQuestionDTO;
import com.daliymove.modules.interview.dto.InterviewReportDTO;
import com.daliymove.modules.interview.entity.InterviewAnswer;
import com.daliymove.modules.interview.entity.InterviewSession;
import com.daliymove.modules.interview.mapper.InterviewSessionMapper;
import com.daliymove.modules.interview.service.AnswerEvaluationService;
import com.daliymove.modules.interview.service.InterviewPersistenceService;
import com.daliymove.modules.resume.entity.Resume;
import com.daliymove.modules.resume.mapper.ResumeMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.stream.StreamMessageId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 面试评估 Stream 消费者
 * - 从 Redis Stream 消费消息并执行评估
 * - 继承 AbstractStreamConsumer 实现异步任务处理
 */
@Slf4j
@Component
public class EvaluateStreamConsumer extends AbstractStreamConsumer<EvaluateStreamConsumer.EvaluatePayload> {

    private final InterviewSessionMapper sessionMapper;
    private final AnswerEvaluationService evaluationService;
    private final InterviewPersistenceService persistenceService;
    private final ResumeMapper resumeMapper;
    private final ObjectMapper objectMapper;

    public EvaluateStreamConsumer(
        RedisService redisService,
        InterviewSessionMapper sessionMapper,
        AnswerEvaluationService evaluationService,
        InterviewPersistenceService persistenceService,
        ResumeMapper resumeMapper,
        ObjectMapper objectMapper
    ) {
        super(redisService);
        this.sessionMapper = sessionMapper;
        this.evaluationService = evaluationService;
        this.persistenceService = persistenceService;
        this.resumeMapper = resumeMapper;
        this.objectMapper = objectMapper;
    }

    record EvaluatePayload(String sessionId) {}

    @Override
    protected String taskDisplayName() {
        return "评估";
    }

    @Override
    protected String streamKey() {
        return AsyncTaskStreamConstants.INTERVIEW_EVALUATE_STREAM_KEY;
    }

    @Override
    protected String groupName() {
        return AsyncTaskStreamConstants.INTERVIEW_EVALUATE_GROUP_NAME;
    }

    @Override
    protected String consumerPrefix() {
        return AsyncTaskStreamConstants.INTERVIEW_EVALUATE_CONSUMER_PREFIX;
    }

    @Override
    protected String threadName() {
        return "evaluate-consumer";
    }

    @Override
    protected EvaluatePayload parsePayload(StreamMessageId messageId, Map<String, String> data) {
        String sessionId = data.get(AsyncTaskStreamConstants.FIELD_SESSION_ID);
        if (sessionId == null) {
            log.warn("消息格式错误，跳过: messageId={}", messageId);
            return null;
        }
        return new EvaluatePayload(sessionId);
    }

    @Override
    protected String payloadIdentifier(EvaluatePayload payload) {
        return "sessionId=" + payload.sessionId();
    }

    @Override
    protected void markProcessing(EvaluatePayload payload) {
        updateEvaluateStatus(payload.sessionId(), AsyncTaskStatus.PROCESSING, null);
    }

    @Override
    protected void processBusiness(EvaluatePayload payload) {
        String sessionId = payload.sessionId();
        InterviewSession session = sessionMapper.findBySessionId(sessionId);
        if (session == null) {
            log.warn("会话已被删除，跳过评估任务: sessionId={}", sessionId);
            return;
        }

        try {
            List<InterviewQuestionDTO> questions = objectMapper.readValue(
                session.getQuestionsJson(),
                new TypeReference<>() {}
            );

            List<InterviewAnswer> answers = persistenceService.findAnswersBySessionId(sessionId);
            for (InterviewAnswer answer : answers) {
                int index = answer.getQuestionIndex();
                if (index >= 0 && index < questions.size()) {
                    InterviewQuestionDTO question = questions.get(index);
                    questions.set(index, question.withAnswer(answer.getUserAnswer()));
                }
            }

            Resume resume = resumeMapper.selectById(session.getResumeId());
            String resumeText = resume != null ? resume.getResumeText() : "";

            InterviewReportDTO report = evaluationService.evaluateInterview(sessionId, resumeText, questions);
            persistenceService.saveReport(sessionId, report);
        } catch (Exception e) {
            log.error("评估业务处理失败: sessionId={}, error={}", sessionId, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void markCompleted(EvaluatePayload payload) {
        updateEvaluateStatus(payload.sessionId(), AsyncTaskStatus.COMPLETED, null);
    }

    @Override
    protected void markFailed(EvaluatePayload payload, String error) {
        updateEvaluateStatus(payload.sessionId(), AsyncTaskStatus.FAILED, error);
    }

    @Override
    protected void retryMessage(EvaluatePayload payload, int retryCount) {
        String sessionId = payload.sessionId();
        try {
            Map<String, String> message = Map.of(
                AsyncTaskStreamConstants.FIELD_SESSION_ID, sessionId,
                AsyncTaskStreamConstants.FIELD_RETRY_COUNT, String.valueOf(retryCount)
            );

            redisService().streamAdd(
                AsyncTaskStreamConstants.INTERVIEW_EVALUATE_STREAM_KEY,
                message,
                AsyncTaskStreamConstants.STREAM_MAX_LEN
            );
            log.info("评估任务已重新入队: sessionId={}, retryCount={}", sessionId, retryCount);

        } catch (Exception e) {
            log.error("重试入队失败: sessionId={}, error={}", sessionId, e.getMessage(), e);
            updateEvaluateStatus(sessionId, AsyncTaskStatus.FAILED, truncateError("重试入队失败: " + e.getMessage()));
        }
    }

    /**
     * 更新评估状态
     */
    private void updateEvaluateStatus(String sessionId, AsyncTaskStatus status, String error) {
        try {
            InterviewSession session = sessionMapper.findBySessionId(sessionId);
            if (session != null) {
                session.setEvaluateStatus(status.name());
                session.setEvaluateError(error);
                sessionMapper.updateById(session);
                log.debug("评估状态已更新: sessionId={}, status={}", sessionId, status);
            }
        } catch (Exception e) {
            log.error("更新评估状态失败: sessionId={}, status={}, error={}", sessionId, status, e.getMessage(), e);
        }
    }
}