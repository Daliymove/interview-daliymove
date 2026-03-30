package com.daliymove.modules.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {

    private final ChatModel chatModel;
    
    private static final String SYSTEM_PROMPT = """
        你是一个专业的AI面试助手，你的任务是帮助用户准备面试、回答技术问题、提供面试建议。
        请用中文回答问题，回答要专业、简洁、有条理。
        如果用户问的是技术问题，请提供清晰的解释和代码示例（如果适用）。
        """;
    
    private static final String TITLE_PROMPT = """
        请为以下对话生成一个简短的标题（不超过15个字），只返回标题内容，不要包含任何其他文字。
        对话内容：%s
        """;

    public String chat(String userMessage) {
        ChatClient chatClient = ChatClient.create(chatModel);
        return chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(userMessage)
                .call()
                .content();
    }

    public Flux<String> streamChat(String userMessage) {
        ChatClient chatClient = ChatClient.create(chatModel);
        return chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(userMessage)
                .stream()
                .content();
    }

    public Flux<String> streamChatWithContext(List<ChatMessageHistory> history, String userMessage) {
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(SYSTEM_PROMPT));
        
        for (ChatMessageHistory h : history) {
            if ("user".equals(h.getRole())) {
                messages.add(new UserMessage(h.getContent()));
            } else if ("assistant".equals(h.getRole())) {
                messages.add(new org.springframework.ai.chat.messages.AssistantMessage(h.getContent()));
            }
        }
        messages.add(new UserMessage(userMessage));
        
        Prompt prompt = new Prompt(messages);
        ChatClient chatClient = ChatClient.create(chatModel);
        
        return chatClient.prompt(prompt)
                .stream()
                .content();
    }

    /**
     * 生成对话标题
     * 根据用户问题生成简短的对话标题
     * 
     * @param userMessage 用户消息
     * @return 生成的标题
     */
    public String generateTitle(String userMessage) {
        ChatClient chatClient = ChatClient.create(chatModel);
        String prompt = String.format(TITLE_PROMPT, userMessage);
        String title = chatClient.prompt()
                .user(prompt)
                .call()
                .content();
        return title != null ? title.trim() : "新对话";
    }

    public static class ChatMessageHistory {
        private String role;
        private String content;

        public ChatMessageHistory(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public String getContent() {
            return content;
        }
    }
}