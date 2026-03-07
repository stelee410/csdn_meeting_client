package com.csdn.meeting.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 标签列表响应DTO
 */
@Data
@Schema(description = "标签列表响应")
public class TagListResponseDTO {

    @Schema(description = "按分类分组的标签列表")
    private List<TagCategoryDTO> categories;

    @Schema(description = "所有标签平铺列表")
    private List<TagDTO> allTags;
}
