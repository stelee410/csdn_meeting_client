package com.csdn.meeting.application.service;

import com.csdn.meeting.application.dto.TagCategoryDTO;
import com.csdn.meeting.application.dto.TagDTO;
import com.csdn.meeting.application.dto.TagListResponseDTO;
import com.csdn.meeting.domain.entity.Tag;
import com.csdn.meeting.domain.repository.TagRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 标签应用服务
 * 处理标签查询和分类展示
 */
@Slf4j
@Service
public class TagAppService {

    private final TagRepository tagRepository;

    public TagAppService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    /**
     * 获取所有标签（按分类分组）
     */
    public TagListResponseDTO getAllTags() {
        List<Tag> tags = tagRepository.findAll();

        // 按分类分组
        Map<Tag.TagCategory, List<Tag>> groupedTags = tags.stream()
                .filter(tag -> tag.getTagCategory() != null)
                .collect(Collectors.groupingBy(Tag::getTagCategory));

        // 转换为分类DTO列表
        List<TagCategoryDTO> categories = groupedTags.entrySet().stream()
                .map(entry -> {
                    TagCategoryDTO categoryDTO = new TagCategoryDTO();
                    categoryDTO.setCategory(entry.getKey().name().toLowerCase());
                    categoryDTO.setCategoryName(entry.getKey().getDisplayName());
                    categoryDTO.setTags(entry.getValue().stream()
                            .map(this::convertToTagDTO)
                            .collect(Collectors.toList()));
                    return categoryDTO;
                })
                .collect(Collectors.toList());

        // 构建响应
        TagListResponseDTO response = new TagListResponseDTO();
        response.setCategories(categories);
        response.setAllTags(tags.stream().map(this::convertToTagDTO).collect(Collectors.toList()));

        return response;
    }

    /**
     * 获取会议关联的标签列表
     */
    public List<TagDTO> getTagsByMeetingId(String meetingId) {
        List<Tag> tags = tagRepository.findByMeetingId(meetingId);
        return tags.stream()
                .map(this::convertToTagDTO)
                .collect(Collectors.toList());
    }

    /**
     * 将Tag转换为TagDTO
     */
    private TagDTO convertToTagDTO(Tag tag) {
        TagDTO dto = new TagDTO();
        dto.setTagId(tag.getId());
        dto.setTagName(tag.getTagName());
        dto.setTagCategory(tag.getTagCategory() != null ? tag.getTagCategory().name().toLowerCase() : null);
        dto.setTagCategoryName(tag.getTagCategory() != null ? tag.getTagCategory().getDisplayName() : null);
        return dto;
    }
}
