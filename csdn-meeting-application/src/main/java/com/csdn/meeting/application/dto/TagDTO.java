package com.csdn.meeting.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 标签DTO
 */
@Schema(description = "标签信息")
public class TagDTO {

    @Schema(description = "标签ID", example = "1")
    private Long id;

    @Schema(description = "标签名称", example = "Java")
    private String name;

    @Schema(description = "标签分类：TECH/INDUSTRY/TOPIC/FORM", example = "TECH")
    private String category;

    // 是否已订阅（用于在会议详情页展示铃铛状态） todo 是否需要
//    @Schema(description = "当前用户是否已订阅该标签", example = "true")
//    private Boolean isSubscribed;

    public TagDTO() {
    }

    public TagDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public TagDTO(Long id, String name, String category) {
        this.id = id;
        this.name = name;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

//    public Boolean getIsSubscribed() {
//        return isSubscribed;
//    }
//
//    public void setIsSubscribed(Boolean isSubscribed) {
//        this.isSubscribed = isSubscribed;
//    }
}
