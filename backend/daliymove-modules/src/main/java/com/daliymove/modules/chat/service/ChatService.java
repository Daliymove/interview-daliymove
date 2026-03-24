package com.daliymove.modules.chat.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.daliymove.modules.chat.dto.ConversationVO;
import com.daliymove.modules.chat.entity.ChatConversation;
import com.daliymove.modules.chat.entity.ChatMessage;
import com.daliymove.modules.chat.mapper.ConversationMapper;
import com.daliymove.modules.chat.mapper.MessageMapper;
import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ConversationMapper conversationMapper;
    private final MessageMapper messageMapper;

    public List<ConversationVO> getConversationList() {
        Long userId = StpUtil.getLoginIdAsLong();
        
        List<ChatConversation> conversations = conversationMapper.selectList(
            new LambdaQueryWrapper<ChatConversation>()
                .eq(ChatConversation::getUserId, userId)
                .eq(ChatConversation::getStatus, 1)
                .orderByDesc(ChatConversation::getUpdateTime)
        );
        
        return conversations.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    public ConversationVO getConversation(Long id) {
        Long userId = StpUtil.getLoginIdAsLong();
        
        ChatConversation conversation = conversationMapper.selectOne(
            new LambdaQueryWrapper<ChatConversation>()
                .eq(ChatConversation::getId, id)
                .eq(ChatConversation::getUserId, userId)
                .eq(ChatConversation::getStatus, 1)
        );
        
        if (conversation == null) {
            return null;
        }
        
        ConversationVO vo = convertToVO(conversation);
        
        List<ChatMessage> messages = messageMapper.selectList(
            new LambdaQueryWrapper<ChatMessage>()
                .eq(ChatMessage::getConversationId, id)
                .orderByAsc(ChatMessage::getCreateTime)
        );
        
        vo.setMessages(messages.stream().map(this::convertMessageToVO).collect(Collectors.toList()));
        
        return vo;
    }

    @Transactional(rollbackFor = Exception.class)
    public ChatConversation createConversation() {
        Long userId = StpUtil.getLoginIdAsLong();
        
        ChatConversation conversation = new ChatConversation();
        conversation.setUserId(userId);
        conversation.setTitle("新对话");
        conversation.setModelType("qwen-plus");
        conversation.setStatus(1);
        
        conversationMapper.insert(conversation);
        
        return conversation;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateTitle(Long id, String title) {
        Long userId = StpUtil.getLoginIdAsLong();
        
        ChatConversation conversation = conversationMapper.selectOne(
            new LambdaQueryWrapper<ChatConversation>()
                .eq(ChatConversation::getId, id)
                .eq(ChatConversation::getUserId, userId)
        );
        
        if (conversation != null) {
            conversation.setTitle(title);
            conversationMapper.updateById(conversation);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteConversation(Long id) {
        Long userId = StpUtil.getLoginIdAsLong();
        
        ChatConversation conversation = conversationMapper.selectOne(
            new LambdaQueryWrapper<ChatConversation>()
                .eq(ChatConversation::getId, id)
                .eq(ChatConversation::getUserId, userId)
        );
        
        if (conversation != null) {
            conversation.setStatus(0);
            conversationMapper.updateById(conversation);
            
            messageMapper.delete(
                new LambdaQueryWrapper<ChatMessage>()
                    .eq(ChatMessage::getConversationId, id)
            );
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveMessage(Long conversationId, String role, String content) {
        ChatMessage message = new ChatMessage();
        message.setConversationId(conversationId);
        message.setRole(role);
        message.setContent(content);
        messageMapper.insert(message);
    }

    public ChatConversation getOrCreateConversation(Long conversationId, Long userId) {
        if (conversationId != null) {
            ChatConversation existing = conversationMapper.selectOne(
                new LambdaQueryWrapper<ChatConversation>()
                    .eq(ChatConversation::getId, conversationId)
                    .eq(ChatConversation::getUserId, userId)
                    .eq(ChatConversation::getStatus, 1)
            );
            if (existing != null) {
                return existing;
            }
        }
        
        return createConversation(userId);
    }
    
    public List<ChatMessage> getConversationMessages(Long conversationId) {
        return messageMapper.selectList(
            new LambdaQueryWrapper<ChatMessage>()
                .eq(ChatMessage::getConversationId, conversationId)
                .orderByAsc(ChatMessage::getCreateTime)
        );
    }
    
    @Transactional(rollbackFor = Exception.class)
    public ChatConversation createConversation(Long userId) {
        ChatConversation conversation = new ChatConversation();
        conversation.setUserId(userId);
        conversation.setTitle("新对话");
        conversation.setModelType("qwen-plus");
        conversation.setStatus(1);
        
        conversationMapper.insert(conversation);
        
        return conversation;
    }

    private ConversationVO convertToVO(ChatConversation conversation) {
        ConversationVO vo = new ConversationVO();
        vo.setId(conversation.getId());
        vo.setTitle(conversation.getTitle());
        vo.setModelType(conversation.getModelType());
        vo.setCreateTime(conversation.getCreateTime() != null ? 
            conversation.getCreateTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() : null);
        vo.setUpdateTime(conversation.getUpdateTime() != null ? 
            conversation.getUpdateTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() : null);
        return vo;
    }

    private ConversationVO.MessageVO convertMessageToVO(ChatMessage message) {
        ConversationVO.MessageVO vo = new ConversationVO.MessageVO();
        vo.setId(message.getId());
        vo.setRole(message.getRole());
        vo.setContent(message.getContent());
        vo.setCreateTime(message.getCreateTime() != null ? 
            message.getCreateTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() : null);
        return vo;
    }
}