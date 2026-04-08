package com.daliymove.modules.resume.listener;

import com.daliymove.common.async.AbstractStreamProducer;
import com.daliymove.common.constant.AsyncTaskStreamConstants;
import com.daliymove.common.enums.AsyncTaskStatus;
import com.daliymove.common.redis.RedisService;
import com.daliymove.modules.resume.mapper.ResumeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 简历分析任务生产者
 * 负责发送分析任务到 Redis Stream
 */
@Slf4j
@Component
public class AnalyzeStreamProducer extends AbstractStreamProducer<AnalyzeStreamProducer.AnalyzeTaskPayload> {

    private final ResumeMapper resumeMapper;

    record AnalyzeTaskPayload(Long resumeId, String content) {}

    public AnalyzeStreamProducer(RedisService redisService, ResumeMapper resumeMapper) {
        super(redisService);
        this.resumeMapper = resumeMapper;
    }

    /**
     * 发送分析任务到 Redis Stream
     *
     * @param resumeId 简历ID
     * @param content  简历内容
     */
    public void sendAnalyzeTask(Long resumeId, String content) {
        sendTask(new AnalyzeTaskPayload(resumeId, content));
    }

    @Override
    protected String taskDisplayName() {
        return "分析";
    }

    @Override
    protected String streamKey() {
        return AsyncTaskStreamConstants.RESUME_ANALYZE_STREAM_KEY;
    }

    @Override
    protected Map<String, String> buildMessage(AnalyzeTaskPayload payload) {
        return Map.of(
                AsyncTaskStreamConstants.FIELD_RESUME_ID, payload.resumeId().toString(),
                AsyncTaskStreamConstants.FIELD_CONTENT, payload.content(),
                AsyncTaskStreamConstants.FIELD_RETRY_COUNT, "0"
        );
    }

    @Override
    protected String payloadIdentifier(AnalyzeTaskPayload payload) {
        return "resumeId=" + payload.resumeId();
    }

    @Override
    protected void onSendFailed(AnalyzeTaskPayload payload, String error) {
        updateAnalyzeStatus(payload.resumeId(), AsyncTaskStatus.FAILED, truncateError(error));
    }

    /**
     * 更新分析状态
     * 当任务发送失败时，更新简历的分析状态为失败
     */
    private void updateAnalyzeStatus(Long resumeId, AsyncTaskStatus status, String error) {
        try {
            var resume = resumeMapper.selectById(resumeId);
            if (resume != null) {
                resume.setAnalyzeStatus(status.name());
                resume.setAnalyzeError(error);
                resumeMapper.updateById(resume);
                log.info("更新简历分析状态: resumeId={}, status={}", resumeId, status);
            }
        } catch (Exception e) {
            log.error("更新简历分析状态失败: resumeId={}, error={}", resumeId, e.getMessage(), e);
        }
    }
}
