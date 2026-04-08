package com.daliymove.common.ai;

import com.daliymove.common.exception.BusinessException;
import com.daliymove.common.exception.ErrorCode;
import org.slf4j.Logger;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Component;

/**
 * 结构化输出调用器
 * - 封装 Spring AI 的结构化输出调用
 * - 提供统一的错误处理和日志记录
 */
@Component
public class StructuredOutputInvoker {

    /**
     * 调用 ChatClient 获取结构化输出
     *
     * @param chatClient         ChatClient 实例
     * @param systemPrompt       系统提示词
     * @param userPrompt         用户提示词
     * @param outputConverter    输出转换器
     * @param errorCode          错误码
     * @param errorPrefix        错误消息前缀
     * @param operationName      操作名称（用于日志）
     * @param log                日志记录器
     * @param <T>                输出类型
     * @return 结构化输出对象
     */
    public <T> T invoke(
            ChatClient chatClient,
            String systemPrompt,
            String userPrompt,
            BeanOutputConverter<T> outputConverter,
            ErrorCode errorCode,
            String errorPrefix,
            String operationName,
            Logger log) {

        try {
            String response = chatClient.prompt()
                    .system(systemPrompt)
                    .user(userPrompt)
                    .call()
                    .content();

            if (response == null || response.isEmpty()) {
                throw new BusinessException(errorCode, errorPrefix + "AI返回空响应");
            }

            T result = outputConverter.convert(response);
            log.debug("{}成功: {}", operationName, result);
            return result;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("{}失败: {}", operationName, e.getMessage(), e);
            throw new BusinessException(errorCode, errorPrefix + e.getMessage());
        }
    }
}