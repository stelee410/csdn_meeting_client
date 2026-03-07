package com.csdn.meeting.application.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 标签DTO
 */
@Data
@ApiModel("标签")
public class TagDTO {

    @ApiModelProperty(value = "标签ID", example = "1")
    private Long tagId;

    @ApiModelProperty(value = "标签名称", example = "鸿蒙")
    private String tagName;

    @ApiModelProperty(value = "标签分类", example = "tech")
    private String tagCategory;

    @ApiModelProperty(value = "标签分类名称", example = "技术")
    private String tagCategoryName;

    @ApiModelProperty(value = "关联会议数量", example = "15")
    private Integer meetingCount;
}
