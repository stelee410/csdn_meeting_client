package com.csdn.meeting.infrastructure.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 会议持久化对象 - MyBatisPlus版本
 * 对应数据库表 t_meeting
 */
@Data
@TableName("t_meeting")
public class MeetingPO {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("meeting_id")
    private String meetingId;

    @TableField("title")
    private String title;

    @TableField("description")
    private String description;

    /**
     * 海报地址
     */
    @TableField("poster_url")
    private String posterUrl;

    @TableField("creator_id")
    private Long creatorId;

    @TableField("creator_name")
    private String creatorName;

    @TableField("start_time")
    private LocalDateTime startTime;

    @TableField("end_time")
    private LocalDateTime endTime;

    /**
     * 会议状态：CREATED-已创建, PUBLISHED-已发布, ONGOING-进行中, ENDED-已结束, CANCELLED-已取消
     */
    @TableField("status")
    private String status;

    /**
     * 会议形式：1-线上, 2-线下, 3-混合
     */
    @TableField("format")
    private Integer format;

    /**
     * 会议类型：1-技术峰会, 2-技术沙龙, 3-技术研讨会
     */
    @TableField("meeting_type")
    private Integer meetingType;

    /**
     * 会议场景：1-开发者会议, 2-产业会议, 3-产品发布, 4-区域营销, 5-高校会议
     */
    @TableField("scene")
    private Integer scene;

    /**
     * 城市编码
     */
    @TableField("city_code")
    private String cityCode;

    /**
     * 城市名称（冗余存储）
     */
    @TableField("city_name")
    private String cityName;

    /**
     * 场馆地址
     */
    @TableField("venue")
    private String venue;

    /**
     * 热度分数
     */
    @TableField("hot_score")
    private Integer hotScore;

    /**
     * 当前报名人数
     */
    @TableField("current_participants")
    private Integer currentParticipants;

    /**
     * 最大参与人数
     */
    @TableField("max_participants")
    private Integer maxParticipants;

    /**
     * 主办方ID
     */
    @TableField("organizer_id")
    private Long organizerId;

    /**
     * 主办方名称
     */
    @TableField("organizer_name")
    private String organizerName;

    /**
     * 主办方头像
     */
    @TableField("organizer_avatar")
    private String organizerAvatar;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 创建人ID
     */
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private Long createBy;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 更新人ID
     */
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    /**
     * 软删除标志：0-未删除, 1-已删除
     */
    @TableField(value = "is_deleted", fill = FieldFill.INSERT)
    @TableLogic
    private Integer isDeleted;
}
