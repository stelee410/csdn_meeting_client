package com.csdn.meeting.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 标签DTO
 */
@Data
@Schema(description = "标签")
public class TagDTO {

    @Schema(description = "标签ID", example = "1")
    private Long tagId;

    @Schema(description = "标签名称", example = "鸿蒙")
    private String tagName;

    @Schema(description = "标签分类", example = "tech")
    private String tagCategory;

    @Schema(description = "标签分类名称", example = "技术")
    private String tagCategoryName;

    @Schema(description = "关联会议数量", example = "15")
    private Integer meetingCount;
}
