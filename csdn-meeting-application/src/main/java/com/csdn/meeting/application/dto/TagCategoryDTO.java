package com.csdn.meeting.application.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 标签分类DTO
 */
@Data
@ApiModel("标签分类")
public class TagCategoryDTO {

    @ApiModelProperty(value = "分类编码", example = "tech")
    private String category;

    @ApiModelProperty(value = "分类名称", example = "技术")
    private String categoryName;

    @ApiModelProperty(value = "该分类下的标签列表")
    private List<TagDTO> tags;
}
