package com.daliymove.modules.knowledge.listener;

import com.daliymove.common.async.AbstractStreamConsumer;
import com.daliymove.common.constant.AsyncTaskStreamConstants;
import com.daliymove.common.redis.RedisService;
import com.daliymove.modules.knowledge.entity.KnowledgeBase;
import com.daliymove.modules.knowledge.enums.VectorStatus;
import com.daliymove.modules.knowledge.mapper.KnowledgeBaseMapper;
import com.daliymove.modules.knowledge.service.KnowledgeBaseVectorService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.stream.StreamMessageId;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 知识库向量化 Stream 消费者
 * - 负责从 Redis Stream 消费消息并执行向量化
 */
@Slf4j
@Component
public class VectorizeStreamConsumer extends AbstractStreamConsumer<VectorizeStreamConsumer.VectorizePayload> {

    private final KnowledgeBaseVectorService vectorService;
    private final KnowledgeBaseMapper knowledgeBaseMapper;

    public VectorizeStreamConsumer(
        RedisService redisService,
        KnowledgeBaseVectorService vectorService,
        KnowledgeBaseMapper knowledgeBaseMapper
    ) {
        super(redisService);
        this.vectorService = vectorService;
        this.knowledgeBaseMapper = knowledgeBaseMapper;
    }

    record VectorizePayload(Long kbId, String content) {}

    @Override
    protected String taskDisplayName() {
        return "向量化";
    }

    @Override
    protected String streamKey() {
        return AsyncTaskStreamConstants.KB_VECTORIZE_STREAM_KEY;
    }

    @Override
    protected String groupName() {
        return AsyncTaskStreamConstants.KB_VECTORIZE_GROUP_NAME;
    }

    @Override
    protected String consumerPrefix() {
        return AsyncTaskStreamConstants.KB_VECTORIZE_CONSUMER_PREFIX;
    }

    @Override
    protected String threadName() {
        return "vectorize-consumer";
    }

    @Override
    protected VectorizePayload parsePayload(StreamMessageId messageId, Map<String, String> data) {
        String kbIdStr = data.get(AsyncTaskStreamConstants.FIELD_KB_ID);
        String content = data.get(AsyncTaskStreamConstants.FIELD_CONTENT);
        if (kbIdStr == null || content == null) {
            log.warn("消息格式错误，跳过: messageId={}", messageId);
            return null;
        }
        return new VectorizePayload(Long.parseLong(kbIdStr), content);
    }

    @Override
    protected String payloadIdentifier(VectorizePayload payload) {
        return "kbId=" + payload.kbId();
    }

    @Override
    protected void markProcessing(VectorizePayload payload) {
        updateVectorStatus(payload.kbId(), VectorStatus.PROCESSING, null);
    }

    @Override
    protected void processBusiness(VectorizePayload payload) {
        vectorService.vectorizeAndStore(payload.kbId(), payload.content());
    }

    @Override
    protected void markCompleted(VectorizePayload payload) {
        updateVectorStatus(payload.kbId(), VectorStatus.COMPLETED, null);
    }

    @Override
    protected void markFailed(VectorizePayload payload, String error) {
        updateVectorStatus(payload.kbId(), VectorStatus.FAILED, error);
    }

    @Override
    protected void retryMessage(VectorizePayload payload, int retryCount) {
        Long kbId = payload.kbId();
        String content = payload.content();
        try {
            Map<String, String> message = Map.of(
                AsyncTaskStreamConstants.FIELD_KB_ID, kbId.toString(),
                AsyncTaskStreamConstants.FIELD_CONTENT, content,
                AsyncTaskStreamConstants.FIELD_RETRY_COUNT, String.valueOf(retryCount)
            );

            redisService().streamAdd(
                AsyncTaskStreamConstants.KB_VECTORIZE_STREAM_KEY,
                message,
                AsyncTaskStreamConstants.STREAM_MAX_LEN
            );
            log.info("向量化任务已重新入队: kbId={}, retryCount={}", kbId, retryCount);

        } catch (Exception e) {
            log.error("重试入队失败: kbId={}, error={}", kbId, e.getMessage(), e);
            updateVectorStatus(kbId, VectorStatus.FAILED, truncateError("重试入队失败: " + e.getMessage()));
        }
    }

    private void updateVectorStatus(Long kbId, VectorStatus status, String error) {
        try {
            KnowledgeBase kb = knowledgeBaseMapper.selectById(kbId);
            if (kb != null) {
                kb.setVectorStatus(status.name());
                kb.setVectorError(error);
                knowledgeBaseMapper.updateById(kb);
                log.debug("向量化状态已更新: kbId={}, status={}", kbId, status);
            }
        } catch (Exception e) {
            log.error("更新向量化状态失败: kbId={}, status={}, error={}", kbId, status, e.getMessage(), e);
        }
    }
}