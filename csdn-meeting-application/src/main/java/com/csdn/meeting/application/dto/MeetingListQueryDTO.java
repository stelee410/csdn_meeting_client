package com.csdn.meeting.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 会议列表查询参数DTO
 * 支持多维度筛选、关键词搜索、分页
 */
@Getter
@Setter
@Schema(description = "会议列表查询参数，支持多维度筛选、关键词搜索、分页")
public class MeetingListQueryDTO {

    /**
     * 关键词搜索（匹配标题、标签、主办方）
     */
    @Schema(description = "关键词搜索，匹配会议标题、标签、主办方名称", example = "Java")
    private String keyword;

    /**
     * 会议形式筛选：ONLINE/OFFLINE/HYBRID
     */
    @Schema(description = "会议形式筛选：0-ALL/1-ONLINE（线上）/2-OFFLINE（线下）/3-HYBRID（混合）", example = "1")
    private Integer format;

    /**
     * 会议类型筛选：SUMMIT/SALON/WORKSHOP
     */
    @Schema(description = "会议类型筛选：0-ALL/1-SUMMIT（技术峰会）/2-SALON（技术沙龙）/3-WORKSHOP（技术研讨会）", example = "1")
    private Integer type;

    /**
     * 会议场景筛选：DEVELOPER/INDUSTRY/PRODUCT/REGIONAL/UNIVERSITY
     */
    @Schema(description = "会议场景筛选：0-ALL/1-DEVELOPER（开发者会议）/2-INDUSTRY（产业会议）/3-PRODUCT（产品发布会议）/4-REGIONAL（区域营销会议）/5-UNIVERSITY（高校会议）", example = "1")
    private Integer scene;

    /**
     * 召开时间范围筛选：THIS_WEEK/THIS_MONTH/NEXT_3_MONTHS
     */
    @Schema(description = "召开时间范围筛选：0-ALL/1-THIS_WEEK（本周）/2-THIS_MONTH（本月）/3-NEXT_3_MONTHS（未来三个月）", example = "1")
    private Integer timeRange;

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

}
