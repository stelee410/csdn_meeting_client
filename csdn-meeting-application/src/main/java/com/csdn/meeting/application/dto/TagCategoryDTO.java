package com.csdn.meeting.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 标签分类DTO
 */
@Data
@Schema(description = "标签分类")
public class TagCategoryDTO {

    @Schema(description = "分类编码", example = "tech")
    private String category;

    @Schema(description = "分类名称", example = "技术")
    private String categoryName;

    @Schema(description = "该分类下的标签列表")
    private List<TagDTO> tags;
}
