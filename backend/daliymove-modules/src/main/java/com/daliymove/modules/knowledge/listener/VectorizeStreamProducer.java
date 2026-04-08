package com.daliymove.modules.knowledge.listener;

import com.daliymove.common.async.AbstractStreamProducer;
import com.daliymove.common.constant.AsyncTaskStreamConstants;
import com.daliymove.common.redis.RedisService;
import com.daliymove.modules.knowledge.entity.KnowledgeBase;
import com.daliymove.modules.knowledge.enums.VectorStatus;
import com.daliymove.modules.knowledge.mapper.KnowledgeBaseMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 向量化任务生产者
 * - 负责发送向量化任务到 Redis Stream
 */
@Slf4j
@Component
public class VectorizeStreamProducer extends AbstractStreamProducer<VectorizeStreamProducer.VectorizeTaskPayload> {

    private final KnowledgeBaseMapper knowledgeBaseMapper;

    record VectorizeTaskPayload(Long kbId, String content) {}

    public VectorizeStreamProducer(RedisService redisService, KnowledgeBaseMapper knowledgeBaseMapper) {
        super(redisService);
        this.knowledgeBaseMapper = knowledgeBaseMapper;
    }

    /**
     * 发送向量化任务到 Redis Stream
     *
     * @param kbId    知识库ID
     * @param content 文档内容
     */
    public void sendVectorizeTask(Long kbId, String content) {
        sendTask(new VectorizeTaskPayload(kbId, content));
    }

    @Override
    protected String taskDisplayName() {
        return "向量化";
    }

    @Override
    protected String streamKey() {
        return AsyncTaskStreamConstants.KB_VECTORIZE_STREAM_KEY;
    }

    @Override
    protected Map<String, String> buildMessage(VectorizeTaskPayload payload) {
        return Map.of(
            AsyncTaskStreamConstants.FIELD_KB_ID, payload.kbId().toString(),
            AsyncTaskStreamConstants.FIELD_CONTENT, payload.content(),
            AsyncTaskStreamConstants.FIELD_RETRY_COUNT, "0"
        );
    }

    @Override
    protected String payloadIdentifier(VectorizeTaskPayload payload) {
        return "kbId=" + payload.kbId();
    }

    @Override
    protected void onSendFailed(VectorizeTaskPayload payload, String error) {
        updateVectorStatus(payload.kbId(), VectorStatus.FAILED, truncateError(error));
    }

    private void updateVectorStatus(Long kbId, VectorStatus status, String error) {
        KnowledgeBase kb = knowledgeBaseMapper.selectById(kbId);
        if (kb != null) {
            kb.setVectorStatus(status.name());
            if (error != null) {
                kb.setVectorError(error.length() > 500 ? error.substring(0, 500) : error);
            }
            knowledgeBaseMapper.updateById(kb);
        }
    }
}