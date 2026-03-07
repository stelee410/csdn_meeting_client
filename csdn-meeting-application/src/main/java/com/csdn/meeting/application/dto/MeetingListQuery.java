package com.csdn.meeting.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 会议列表查询参数
 * 支持多维度筛选和分页
 */
@Data
@Schema(description = "会议列表查询参数")
public class MeetingListQuery {

    @Schema(description = "会议形式：1-线上, 2-线下, 3-混合", example = "1")
    private Integer format;

    @Schema(description = "会议类型：1-技术峰会, 2-技术沙龙, 3-技术研讨会", example = "2")
    private Integer meetingType;

    @Schema(description = "会议场景：1-开发者会议, 2-产业会议, 3-产品发布, 4-区域营销, 5-高校会议", example = "1")
    private Integer scene;

    @Schema(description = "时间范围：this_week-本周, this_month-本月, next_3_months-未来三个月", example = "this_week")
    private String timeRange;

    @Schema(description = "搜索关键词（匹配标题、标签、主办方、城市）", example = "Java")
    private String keyword;

    @Schema(description = "视图类型：card-卡片视图(默认), list-列表视图", example = "card")
    private String viewType = "card";

    @Schema(description = "页码，默认1", example = "1")
    private Integer page = 1;

    @Schema(description = "每页大小，默认10，最大50", example = "10")
    private Integer size = 10;

    /**
     * 获取安全的页码
     */
    public int getSafePage() {
        return page != null && page > 0 ? page : 1;
    }

    /**
     * 获取安全的每页大小
     */
    public int getSafeSize() {
        if (size == null || size <= 0) {
            return 10;
        }
        return Math.min(size, 50); // 最大50条
    }

    /**
     * 计算起始位置（用于数据库查询）
     */
    public int getOffset() {
        return (getSafePage() - 1) * getSafeSize();
    }
}
