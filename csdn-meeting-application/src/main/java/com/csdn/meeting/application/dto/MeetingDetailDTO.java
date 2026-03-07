package com.csdn.meeting.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 会议详情DTO
 * 包含完整信息和标签订阅状态
 */
@Data
@Schema(description = "会议详情")
public class MeetingDetailDTO {

    @Schema(description = "会议ID", example = "M123456789")
    private String meetingId;

    @Schema(description = "会议标题", example = "HarmonyOS开发者日")
    private String title;

    @Schema(description = "详细描述", example = "详细介绍...")
    private String description;

    @Schema(description = "海报地址", example = "https://.../poster.jpg")
    private String posterUrl;

    @Schema(description = "主办方ID", example = "12345")
    private Long organizerId;

    @Schema(description = "主办方名称", example = "华为开发者联盟")
    private String organizerName;

    @Schema(description = "主办方头像", example = "https://.../avatar.jpg")
    private String organizerAvatar;

    @Schema(description = "会议形式编码：1-线上, 2-线下, 3-混合", example = "2")
    private Integer format;

    @Schema(description = "会议形式名称", example = "线下")
    private String formatName;

    @Schema(description = "会议类型编码：1-技术峰会, 2-技术沙龙, 3-技术研讨会", example = "2")
    private Integer meetingType;

    @Schema(description = "会议类型名称", example = "技术沙龙")
    private String meetingTypeName;

    @Schema(description = "会议场景编码：1-开发者会议, 2-产业会议, 3-产品发布, 4-区域营销, 5-高校会议", example = "1")
    private Integer scene;

    @Schema(description = "会议场景名称", example = "开发者会议")
    private String sceneName;

    @Schema(description = "城市编码", example = "110000")
    private String cityCode;

    @Schema(description = "城市名称", example = "北京")
    private String cityName;

    @Schema(description = "场馆地址", example = "中关村创业大街")
    private String venue;

    @Schema(description = "开始时间", example = "2026-03-15 14:00:00")
    private LocalDateTime startTime;

    @Schema(description = "结束时间", example = "2026-03-15 17:00:00")
    private LocalDateTime endTime;

    @Schema(description = "状态编码", example = "1")
    private Integer status;

    @Schema(description = "状态名称", example = "报名中")
    private String statusName;

    @Schema(description = "热度分数", example = "1200")
    private Integer hotScore;

    @Schema(description = "热度显示", example = "1.2k人感兴趣")
    private String hotScoreDisplay;

    @Schema(description = "当前报名人数", example = "856")
    private Integer currentParticipants;

    @Schema(description = "最大参与人数", example = "1000")
    private Integer maxParticipants;

    @Schema(description = "报名人数显示", example = "856 / 1000 人")
    private String participantsDisplay;

    @Schema(description = "关联标签列表（包含订阅状态）")
    private List<MeetingTagDTO> tags;

    @Schema(description = "创建时间", example = "2026-02-01 10:00:00")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", example = "2026-02-10 15:30:00")
    private LocalDateTime updateTime;
}
