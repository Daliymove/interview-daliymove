package com.daliymove.modules.knowledge.converter;

import com.daliymove.modules.knowledge.dto.KnowledgeBaseListItemDTO;
import com.daliymove.modules.knowledge.entity.KnowledgeBase;
import com.daliymove.modules.knowledge.enums.VectorStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * 知识库实体到DTO的映射器
 * - 使用MapStruct自动生成转换代码
 */
@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface KnowledgeBaseConverter {

    /**
     * 将知识库实体转换为列表项DTO
     */
    @Mapping(target = "vectorStatus", source = "vectorStatus", qualifiedByName = "parseVectorStatus")
    KnowledgeBaseListItemDTO toListItemDTO(KnowledgeBase entity);

    /**
     * 将知识库实体列表转换为列表项DTO列表
     */
    List<KnowledgeBaseListItemDTO> toListItemDTOList(List<KnowledgeBase> entities);

    /**
     * 解析向量状态字符串为枚举
     */
    @Named("parseVectorStatus")
    default VectorStatus parseVectorStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        try {
            return VectorStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}