package com.csdn.meeting.application.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 标签列表响应DTO
 */
@Data
@ApiModel("标签列表响应")
public class TagListResponseDTO {

    @ApiModelProperty(value = "按分类分组的标签列表")
    private List<TagCategoryDTO> categories;

    @ApiModelProperty(value = "所有标签平铺列表")
    private List<TagDTO> allTags;
}
