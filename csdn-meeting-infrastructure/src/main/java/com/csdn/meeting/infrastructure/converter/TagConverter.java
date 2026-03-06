package com.csdn.meeting.infrastructure.converter;

import com.csdn.meeting.domain.entity.Tag;
import com.csdn.meeting.infrastructure.po.TagPO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 标签转换器
 * 用于 TagPO 和 Tag 实体之间的转换
 */
@Mapper
public interface TagConverter {

    TagConverter INSTANCE = Mappers.getMapper(TagConverter.class);

    /**
     * PO 转 Entity
     */
    Tag poToEntity(TagPO tagPO);

    /**
     * Entity 转 PO
     */
    TagPO entityToPo(Tag tag);

    /**
     * PO列表转Entity列表
     */
    List<Tag> poListToEntityList(List<TagPO> tagPOList);

    /**
     * Entity列表转PO列表
     */
    List<TagPO> entityListToPoList(List<Tag> tagList);

    /**
     * 字符串转枚举
     */
    default Tag.TagCategory stringToCategory(String category) {
        if (category == null) {
            return null;
        }
        try {
            return Tag.TagCategory.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * 枚举转字符串
     */
    default String categoryToString(Tag.TagCategory category) {
        return category == null ? null : category.name().toLowerCase();
    }
}
