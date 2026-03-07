package com.csdn.meeting.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 会议列表查询参数DTO
 * 支持多维度筛选、关键词搜索、分页、视图切换
 */
@Schema(description = "会议列表查询参数，支持多维度筛选、关键词搜索、分页、视图切换")
public class MeetingListQueryDTO {

    /**
     * 视图模式：list（列表视图）| card（阅读视图/卡片视图）
     * 默认：card
     */
    @Schema(description = "视图模式：card（阅读视图，默认）| list（列表视图）", example = "card")
    private String viewMode = "card";

    /**
     * 关键词搜索（匹配标题、标签、主办方）
     */
    @Schema(description = "关键词搜索，匹配会议标题、标签、主办方名称", example = "Java")
    private String keyword;

    /**
     * 会议形式筛选：ONLINE/OFFLINE/HYBRID
     */
    @Schema(description = "会议形式筛选：ONLINE（线上）/OFFLINE（线下）/HYBRID（混合）", example = "ONLINE")
    private String format;

    /**
     * 会议类型筛选：SUMMIT/SALON/WORKSHOP
     */
    @Schema(description = "会议类型筛选：SUMMIT（技术峰会）/SALON（技术沙龙）/WORKSHOP（技术研讨会）", example = "SUMMIT")
    private String type;

    /**
     * 会议场景筛选：DEVELOPER/INDUSTRY/PRODUCT/REGIONAL/UNIVERSITY
     */
    @Schema(description = "会议场景筛选：DEVELOPER（开发者会议）/INDUSTRY（产业会议）/PRODUCT（产品发布会议）/REGIONAL（区域营销会议）/UNIVERSITY（高校会议）", example = "DEVELOPER")
    private String scene;

    /**
     * 召开时间范围筛选：THIS_WEEK/THIS_MONTH/NEXT_3_MONTHS
     */
    @Schema(description = "召开时间范围筛选：THIS_WEEK（本周）/THIS_MONTH（本月）/NEXT_3_MONTHS（未来三个月）", example = "THIS_WEEK")
    private String timeRange;

    /**
     * 分页页码，从0开始
     */
    @Schema(description = "分页页码，从0开始", example = "0")
    private int page = 0;

    /**
     * 分页大小，默认20
     */
    @Schema(description = "分页大小", example = "20")
    private int size = 20;

    /**
     * 用户ID（用于个性化推荐，可选）
     */
    @Schema(description = "用户ID，用于个性化推荐和埋点统计", example = "12345")
    private Long userId;

    public String getViewMode() {
        return viewMode;
    }

    public void setViewMode(String viewMode) {
        this.viewMode = viewMode;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public String getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(String timeRange) {
        this.timeRange = timeRange;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 是否为列表视图
     */
    public boolean isListView() {
        return "list".equalsIgnoreCase(viewMode);
    }

    /**
     * 是否为卡片视图（阅读视图）
     */
    public boolean isCardView() {
        return !isListView();
    }
}
