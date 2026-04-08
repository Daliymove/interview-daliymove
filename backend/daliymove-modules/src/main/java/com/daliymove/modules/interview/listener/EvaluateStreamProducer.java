package com.daliymove.modules.interview.listener;

import com.daliymove.common.constant.AsyncTaskStreamConstants;
import com.daliymove.common.enums.AsyncTaskStatus;
import com.daliymove.common.redis.RedisService;
import com.daliymove.modules.interview.entity.InterviewSession;
import com.daliymove.modules.interview.mapper.InterviewSessionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 面试评估任务生产者
 * - 发送评估任务到 Redis Stream
 * - 异步处理面试评估
 * - 使用 RedisService 统一操作 Stream
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EvaluateStreamProducer {

    private final RedisService redisService;
    private final InterviewSessionMapper sessionMapper;

    /**
     * 发送评估任务到 Redis Stream
     *
     * @param sessionId 面试会话ID（UUID）
     */
    public void sendEvaluateTask(String sessionId) {
        log.info("发送评估任务到Stream: sessionId={}", sessionId);

        Map<String, String> message = Map.of(
            AsyncTaskStreamConstants.FIELD_SESSION_ID, sessionId,
            AsyncTaskStreamConstants.FIELD_TIMESTAMP, String.valueOf(System.currentTimeMillis())
        );

        try {
            String messageId = redisService.streamAdd(
                AsyncTaskStreamConstants.INTERVIEW_EVALUATE_STREAM_KEY,
                message,
                AsyncTaskStreamConstants.STREAM_MAX_LEN
            );
            log.info("评估任务已发送: sessionId={}, messageId={}", sessionId, messageId);
        } catch (Exception e) {
            log.error("发送评估任务失败: sessionId={}, error={}", sessionId, e.getMessage(), e);
            updateEvaluateStatus(sessionId, AsyncTaskStatus.FAILED, truncateError("任务入队失败: " + e.getMessage()));
        }
    }

    /**
     * 更新评估状态
     * 当任务发送失败时，更新面试会话的评估状态为失败
     *
     * @param sessionId 面试会话ID（UUID）
     * @param status    评估状态
     * @param error     错误信息
     */
    private void updateEvaluateStatus(String sessionId, AsyncTaskStatus status, String error) {
        try {
            InterviewSession session = sessionMapper.findBySessionId(sessionId);
            if (session != null) {
                session.setEvaluateStatus(status.name());
                session.setEvaluateError(error);
                sessionMapper.updateById(session);
                log.info("更新面试评估状态: sessionId={}, status={}", sessionId, status);
            }
        } catch (Exception e) {
            log.error("更新面试评估状态失败: sessionId={}, error={}", sessionId, e.getMessage(), e);
        }
    }

    /**
     * 截断错误信息，限制最大长度为500字符
     */
    private String truncateError(String error) {
        if (error == null) {
            return null;
        }
        return error.length() > 500 ? error.substring(0, 500) : error;
    }
}