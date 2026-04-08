package com.daliymove.modules.knowledge.converter;

import com.daliymove.modules.knowledge.dto.KnowledgeBaseListItemDTO;
import com.daliymove.modules.knowledge.dto.RagChatDTO;
import com.daliymove.modules.knowledge.entity.KnowledgeBase;
import com.daliymove.modules.knowledge.entity.RagChatMessage;
import com.daliymove.modules.knowledge.entity.RagChatSession;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * RAG聊天相关实体到DTO的映射器
 * - 使用MapStruct自动生成转换代码
 */
@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = KnowledgeBaseConverter.class
)
public interface RagChatConverter {

    /**
     * 将会话实体转换为会话DTO
     */
    @Mapping(target = "knowledgeBaseIds", ignore = true)
    RagChatDTO.SessionDTO toSessionDTO(RagChatSession session);

    /**
     * 将消息实体转换为消息DTO
     */
    RagChatDTO.MessageDTO toMessageDTO(RagChatMessage message);

    /**
     * 将消息实体列表转换为消息DTO列表
     */
    List<RagChatDTO.MessageDTO> toMessageDTOList(List<RagChatMessage> messages);

    /**
     * 将知识库实体集合转换为知识库名称列表
     */
    @Named("extractKnowledgeBaseNames")
    default List<String> extractKnowledgeBaseNames(List<KnowledgeBase> knowledgeBases) {
        if (knowledgeBases == null) {
            return List.of();
        }
        return knowledgeBases.stream()
            .map(KnowledgeBase::getName)
            .toList();
    }

    /**
     * 将会话实体转换为会话列表项DTO
     */
    @Mapping(target = "knowledgeBaseNames", ignore = true)
    @Mapping(target = "isPinned", source = "isPinned", qualifiedByName = "getIsPinnedWithDefault")
    RagChatDTO.SessionListItemDTO toSessionListItemDTO(RagChatSession session);

    /**
     * 处理isPinned的null值，默认为false
     */
    @Named("getIsPinnedWithDefault")
    default Boolean getIsPinnedWithDefault(Boolean isPinned) {
        return isPinned != null ? isPinned : false;
    }

    /**
     * 将会话实体和消息列表转换为会话详情DTO
     */
    default RagChatDTO.SessionDetailDTO toSessionDetailDTO(
            RagChatSession session,
            List<RagChatMessage> messages,
            List<KnowledgeBaseListItemDTO> knowledgeBases) {
        List<RagChatDTO.MessageDTO> messageDTOs = toMessageDTOList(messages);

        return new RagChatDTO.SessionDetailDTO(
            session.getId(),
            session.getTitle(),
            knowledgeBases,
            messageDTOs,
            session.getCreatedAt(),
            session.getUpdatedAt()
        );
    }
}