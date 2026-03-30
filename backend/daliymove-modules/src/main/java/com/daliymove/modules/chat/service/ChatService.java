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

/**
 * 聊天服务类
 * - 对话的创建、查询、更新、删除
 * - 消息的保存和查询
 * - 对话和消息的转换处理
 */
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ConversationMapper conversationMapper;
    private final MessageMapper messageMapper;

    /**
     * 获取当前用户的对话列表
     * 按更新时间倒序返回当前用户的所有有效对话。
     * 
     * @return 对话列表
     */
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

    /**
     * 获取对话详情（包含消息列表）
     * 
     * @param id 对话ID
     * @return 对话详情（包含消息列表）
     */
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

    /**
     * 创建新对话
     * 使用当前登录用户创建新对话，默认标题为"新对话"，模型类型为"qwen-plus"。
     * 
     * @return 创建的对话对象
     */
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

    /**
     * 更新对话标题
     * 
     * @param id 对话ID
     * @param title 新标题
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateTitle(Long id, String title) {
        Long userId = StpUtil.getLoginIdAsLong();
        updateTitle(id, title, userId);
    }

    /**
     * 更新对话标题（异步线程使用）
     * 
     * @param id 对话ID
     * @param title 新标题
     * @param userId 用户ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateTitle(Long id, String title, Long userId) {
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

    /**
     * 删除对话（逻辑删除）
     * 同时删除对话关联的所有消息。
     * 
     * @param id 对话ID
     */
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

    /**
     * 保存消息
     * 
     * @param conversationId 对话ID
     * @param role 角色（user/assistant）
     * @param content 消息内容
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveMessage(Long conversationId, String role, String content) {
        ChatMessage message = new ChatMessage();
        message.setConversationId(conversationId);
        message.setRole(role);
        message.setContent(content);
        messageMapper.insert(message);
    }

    /**
     * 获取或创建对话
     * 如果对话ID不为空且存在有效对话，则返回该对话；否则创建新对话。
     * 
     * @param conversationId 对话ID（可为空）
     * @param userId 用户ID
     * @return 对话对象
     */
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
    
    /**
     * 获取对话的所有消息
     * 
     * @param conversationId 对话ID
     * @return 消息列表（按创建时间升序）
     */
    public List<ChatMessage> getConversationMessages(Long conversationId) {
        return messageMapper.selectList(
            new LambdaQueryWrapper<ChatMessage>()
                .eq(ChatMessage::getConversationId, conversationId)
                .orderByAsc(ChatMessage::getCreateTime)
        );
    }
    
    /**
     * 为指定用户创建对话
     * 
     * @param userId 用户ID
     * @return 创建的对话对象
     */
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

    /**
     * 将对话实体转换为视图对象
     * 
     * @param conversation 对话实体
     * @return 对话视图对象
     */
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

    /**
     * 将消息实体转换为视图对象
     * 
     * @param message 消息实体
     * @return 消息视图对象
     */
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