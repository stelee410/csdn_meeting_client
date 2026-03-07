package com.csdn.meeting.application.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 会议列表项DTO
 * 用于列表展示（支持双视图）
 */
@Data
@ApiModel("会议列表项")
public class MeetingListItemDTO {

    @ApiModelProperty(value = "会议ID", example = "M123456789")
    private String meetingId;

    @ApiModelProperty(value = "会议标题", example = "HarmonyOS开发者日")
    private String title;

    @ApiModelProperty(value = "描述摘要（前100字）", example = "聚焦人工智能前沿技术...")
    private String description;

    @ApiModelProperty(value = "海报地址", example = "https://.../poster.jpg")
    private String posterUrl;

    @ApiModelProperty(value = "主办方ID", example = "12345")
    private Long organizerId;

    @ApiModelProperty(value = "主办方名称", example = "华为开发者联盟")
    private String organizerName;

    @ApiModelProperty(value = "主办方头像", example = "https://.../avatar.jpg")
    private String organizerAvatar;

    @ApiModelProperty(value = "会议形式编码：1-线上, 2-线下, 3-混合", example = "2")
    private Integer format;

    @ApiModelProperty(value = "会议形式名称", example = "线下")
    private String formatName;

    @ApiModelProperty(value = "会议类型编码：1-技术峰会, 2-技术沙龙, 3-技术研讨会", example = "2")
    private Integer meetingType;

    @ApiModelProperty(value = "会议类型名称", example = "技术沙龙")
    private String meetingTypeName;

    @ApiModelProperty(value = "会议场景编码：1-开发者会议, 2-产业会议, 3-产品发布, 4-区域营销, 5-高校会议", example = "1")
    private Integer scene;

    @ApiModelProperty(value = "会议场景名称", example = "开发者会议")
    private String sceneName;

    @ApiModelProperty(value = "城市编码", example = "110000")
    private String cityCode;

    @ApiModelProperty(value = "城市名称", example = "北京")
    private String cityName;

    @ApiModelProperty(value = "场馆地址", example = "中关村创业大街")
    private String venue;

    @ApiModelProperty(value = "开始时间", example = "2026-03-15 14:00:00")
    private LocalDateTime startTime;

    @ApiModelProperty(value = "结束时间", example = "2026-03-15 17:00:00")
    private LocalDateTime endTime;

    @ApiModelProperty(value = "状态编码：1-已发布, 2-进行中, 3-已结束", example = "1")
    private Integer status;

    @ApiModelProperty(value = "状态名称", example = "报名中")
    private String statusName;

    @ApiModelProperty(value = "热度分数", example = "1200")
    private Integer hotScore;

    @ApiModelProperty(value = "热度显示", example = "1.2k人感兴趣")
    private String hotScoreDisplay;

    @ApiModelProperty(value = "当前报名人数", example = "856")
    private Integer currentParticipants;

    @ApiModelProperty(value = "最大参与人数", example = "1000")
    private Integer maxParticipants;

    @ApiModelProperty(value = "报名人数显示", example = "856 / 1000 人")
    private String participantsDisplay;

    @ApiModelProperty(value = "关联标签列表")
    private List<TagDTO> tags;
}
