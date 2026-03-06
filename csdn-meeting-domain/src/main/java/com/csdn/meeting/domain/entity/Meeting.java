package com.csdn.meeting.domain.entity;

import com.csdn.meeting.domain.valueobject.MeetingFormat;
import com.csdn.meeting.domain.valueobject.MeetingScene;
import com.csdn.meeting.domain.valueobject.MeetingStatus;
import com.csdn.meeting.domain.valueobject.MeetingType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 会议实体 - 聚合根
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Meeting extends BaseEntity {

    /**
     * 会议业务ID
     */
    private String meetingId;

    /**
     * 会议标题
     */
    private String title;

    /**
     * 会议描述
     */
    private String description;

    /**
     * 海报地址
     */
    private String posterUrl;

    /**
     * 创建人ID
     */
    private Long creatorId;

    /**
     * 创建人名称
     */
    private String creatorName;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 会议状态
     */
    private MeetingStatus status;

    /**
     * 会议形式：ONLINE/OFFLINE/HYBRID
     */
    private MeetingFormat format;

    /**
     * 会议类型：SUMMIT/SALON/WORKSHOP
     */
    private MeetingType meetingType;

    /**
     * 会议场景
     */
    private MeetingScene scene;

    /**
     * 城市编码
     */
    private String cityCode;

    /**
     * 城市名称
     */
    private String cityName;

    /**
     * 场馆地址
     */
    private String venue;

    /**
     * 热度分数
     */
    private Integer hotScore;

    /**
     * 当前报名人数
     */
    private Integer currentParticipants;

    /**
     * 最大参与人数
     */
    private Integer maxParticipants;

    /**
     * 主办方ID
     */
    private Long organizerId;

    /**
     * 主办方名称
     */
    private String organizerName;

    /**
     * 主办方头像
     */
    private String organizerAvatar;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    private Integer isDeleted;

    /**
     * 开始会议
     */
    public void start() {
        if (this.status != MeetingStatus.PUBLISHED) {
            throw new IllegalStateException("只有已发布的会议才能开始");
        }
        this.status = MeetingStatus.ONGOING;
    }

    /**
     * 结束会议
     */
    public void end() {
        if (this.status != MeetingStatus.ONGOING) {
            throw new IllegalStateException("只有进行中的会议才能结束");
        }
        this.status = MeetingStatus.ENDED;
    }

    /**
     * 取消会议
     */
    public void cancel() {
        if (this.status == MeetingStatus.ENDED) {
            throw new IllegalStateException("已结束的会议不能取消");
        }
        this.status = MeetingStatus.CANCELLED;
    }

    /**
     * 发布会议
     */
    public void publish() {
        if (this.status != MeetingStatus.CREATED) {
            throw new IllegalStateException("只有已创建的会议才能发布");
        }
        this.status = MeetingStatus.PUBLISHED;
    }

    /**
     * 增加报名人数
     */
    public void incrementParticipants() {
        if (this.currentParticipants == null) {
            this.currentParticipants = 0;
        }
        if (this.maxParticipants != null && this.currentParticipants >= this.maxParticipants) {
            throw new IllegalStateException("报名人数已满");
        }
        this.currentParticipants++;
    }

    /**
     * 减少报名人数
     */
    public void decrementParticipants() {
        if (this.currentParticipants != null && this.currentParticipants > 0) {
            this.currentParticipants--;
        }
    }

    /**
     * 获取报名进度显示
     */
    public String getParticipantsDisplay() {
        if (this.currentParticipants == null || this.maxParticipants == null) {
            return "";
        }
        return String.format("%d / %d 人", this.currentParticipants, this.maxParticipants);
    }

    /**
     * 获取热度显示
     */
    public String getHotScoreDisplay() {
        if (this.hotScore == null || this.hotScore == 0) {
            return "";
        }
        if (this.hotScore >= 1000) {
            return String.format("%.1fk人感兴趣", this.hotScore / 1000.0);
        }
        return this.hotScore + "人感兴趣";
    }

    /**
     * 是否可以在列表中展示
     */
    public boolean isVisibleInList() {
        return this.status != null && this.status.isVisibleInList();
    }
}
