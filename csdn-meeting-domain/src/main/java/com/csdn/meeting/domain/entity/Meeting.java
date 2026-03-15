package com.csdn.meeting.domain.entity;

import com.csdn.meeting.domain.valueobject.MeetingFormat;
import com.csdn.meeting.domain.valueobject.MeetingType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    private String creatorId;

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
     * 城市编码
     */
    private String cityCode;

    /**
     * 城市名称
     */
    private String cityName;

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

    // 新增字段（agent.prd §1.1）
    private String organizer;
    private Long organizerId;         // 主办方用户ID
    private String organizerName;     // 主办方名称
    private String organizerAvatar;   // 主办方头像URL
    private String scene;
    private String venue;
    private String regions;           // JSON
    private String coverImage;
    private String tags;              // 逗号分隔的 tagId，如 1,2,3
    private String targetAudience;    // JSON
    private Boolean isPremium;
    private String takedownReason;
    private String rejectReason;

    // 四级日程结构
    private List<ScheduleDay> scheduleDays;

    /**
     * 发布时间（会议创建时间）
     */
    private LocalDateTime publishTime;

    // V1.2新增字段 - 报名签到功能
    /**
     * 报名截止时间
     */
    private LocalDateTime regEndTime;

    /**
     * 签到码（用于生成二维码）
     */
    private String checkinCode;

    /**
     * 是否启用签到
     */
    private Boolean requireCheckin;

    public enum MeetingStatus {
        DRAFT(0),
        PENDING_REVIEW(1),
        PUBLISHED(2),
        IN_PROGRESS(3),
        ENDED(4),
        REJECTED(5),
        OFFLINE(6),
        DELETED(7);

        private final int code;

        MeetingStatus(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public boolean isVisibleInList() {
            return this == PUBLISHED || this == IN_PROGRESS || this == ENDED;
        }

        public String getDisplayName() {
            switch (this) {
                case DRAFT: return "草稿";
                case PENDING_REVIEW: return "待审核";
                case PUBLISHED: return "已发布";
                case IN_PROGRESS: return "进行中";
                case ENDED: return "已结束";
                case REJECTED: return "已拒绝";
                case OFFLINE: return "已下架";
                case DELETED: return "已删除";
                default: return name();
            }
        }
    }

    // ---- submit / withdraw ----

    /**
     * 提交审核：DRAFT 或 REJECTED -> PENDING_REVIEW
     * 四级日程完整性由 MeetingDomainService 在调用前校验
     */
    public void submit() {
        if (this.status != MeetingStatus.DRAFT
                && this.status != MeetingStatus.REJECTED
                && this.status != MeetingStatus.OFFLINE) {
            throw new IllegalStateException(
                "只有草稿、已拒绝或已下架状态才能提交审核，当前状态: " + this.status);
        }
        this.status = MeetingStatus.PENDING_REVIEW;
    }

    /**
     * 撤回审核：PENDING_REVIEW -> DRAFT
     */
    public void withdraw() {
        if (this.status != MeetingStatus.PENDING_REVIEW) {
            throw new IllegalStateException(
                "只有待审核状态才能撤回，当前状态: " + this.status);
        }
        this.status = MeetingStatus.DRAFT;
    }

    /**
     * 审核通过：DRAFT 或 PENDING_REVIEW -> PUBLISHED
     * 由管理员触发，审计日志由调用方记录。
     * 业务上 DRAFT（草稿）与 PENDING_REVIEW（已提交待审核）均视为「待审核」，管理员均可直接通过。
     */
    public void approve() {
        if (this.status != MeetingStatus.DRAFT && this.status != MeetingStatus.PENDING_REVIEW) {
            throw new IllegalStateException(
                "只有待审核状态才能审核通过，当前状态: " + this.status);
        }
        this.status = MeetingStatus.PUBLISHED;
    }

    /**
     * 审核拒绝：DRAFT 或 PENDING_REVIEW -> REJECTED
     * 由管理员触发，rejectReason 必填。DRAFT 与 PENDING_REVIEW 均视为「待审核」，管理员均可驳回。
     *
     * @param reason 拒绝原因，不能为空
     */
    public void reject(String reason) {
        if (this.status != MeetingStatus.DRAFT && this.status != MeetingStatus.PENDING_REVIEW) {
            throw new IllegalStateException(
                "只有待审核状态才能审核拒绝，当前状态: " + this.status);
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("拒绝原因不能为空");
        }
        this.rejectReason = reason;
        this.status = MeetingStatus.REJECTED;
    }

    /**
     * 重新提交审核：REJECTED -> PENDING_REVIEW
     * 办会方修改后重新进入审核队列
     */
    public void resubmit() {
        if (this.status != MeetingStatus.REJECTED) {
            throw new IllegalStateException(
                "只有已拒绝状态才能重新提交，当前状态: " + this.status);
        }
        this.rejectReason = null;
        this.status = MeetingStatus.PENDING_REVIEW;
    }

    /**
     * 主动下架：PUBLISHED / IN_PROGRESS -> OFFLINE
     * takedownReason 必填
     *
     * @param reason 下架原因，不能为空
     */
    public void takedown(String reason) {
        if (this.status != MeetingStatus.PUBLISHED && this.status != MeetingStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                "只有已发布或进行中状态才能下架，当前状态: " + this.status);
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("下架原因不能为空");
        }
        this.takedownReason = reason;
        this.status = MeetingStatus.OFFLINE;
    }

    /**
     * 逻辑删除：DRAFT / ENDED / OFFLINE / REJECTED -> DELETED
     * 仅允许终态或草稿/拒绝状态删除
     */
    public void delete() {
        if (this.status != MeetingStatus.DRAFT
                && this.status != MeetingStatus.ENDED
                && this.status != MeetingStatus.OFFLINE
                && this.status != MeetingStatus.REJECTED) {
            throw new IllegalStateException(
                "只有草稿、已结束、已下架或已拒绝状态才能删除，当前状态: " + this.status);
        }
        this.status = MeetingStatus.DELETED;
    }

    /**
     * 自动开始：PUBLISHED -> IN_PROGRESS
     * 由定时任务按 startTime 触发
     */
    public void autoStart() {
        if (this.status != MeetingStatus.PUBLISHED) {
            throw new IllegalStateException(
                "只有已发布状态才能自动开始，当前状态: " + this.status);
        }
        this.status = MeetingStatus.IN_PROGRESS;
    }

    /**
     * 自动结束：IN_PROGRESS -> ENDED
     * 由定时任务按 endTime 触发，触发后应发布 MeetingEndedEvent
     */
    public void autoEnd() {
        if (this.status != MeetingStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                "只有进行中状态才能自动结束，当前状态: " + this.status);
        }
        this.status = MeetingStatus.ENDED;
    }

    // ---- 保留的 start / end / cancel（适配新状态机） ----

    /**
     * 开始会议：PUBLISHED -> IN_PROGRESS
     * 等效于定时任务触发的 autoStart()，用于手动或测试场景
     *
     * @deprecated 推荐使用 autoStart() 由定时任务按 startTime 触发
     */
    @Deprecated
    public void start() {
        if (this.status != MeetingStatus.PUBLISHED) {
            throw new IllegalStateException(
                "只有已发布状态才能开始会议，当前状态: " + this.status);
        }
        this.status = MeetingStatus.IN_PROGRESS;
    }

    /**
     * 结束会议：IN_PROGRESS -> ENDED
     * 等效于定时任务触发的 autoEnd()
     */
    public void end() {
        if (this.status != MeetingStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                "只有进行中状态才能结束会议，当前状态: " + this.status);
        }
        this.status = MeetingStatus.ENDED;
    }

    /**
     * 取消会议（下线）：PUBLISHED 或 IN_PROGRESS -> OFFLINE
     * 需要调用 takedown(reason) 时传入原因，本方法使用默认原因
     *
     * @deprecated 推荐使用 takedown(reason) 明确下架原因
     */
    @Deprecated
    public void cancel() {
        if (this.status == MeetingStatus.PUBLISHED || this.status == MeetingStatus.IN_PROGRESS) {
            this.takedownReason = "主动取消";
            this.status = MeetingStatus.OFFLINE;
        } else if (this.status == MeetingStatus.ENDED) {
            throw new IllegalStateException("已结束的会议不能取消");
        } else {
            throw new IllegalStateException("当前状态不允许取消: " + this.status);
        }
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

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public Long getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(Long organizerId) {
        this.organizerId = organizerId;
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }

    public String getOrganizerAvatar() {
        return organizerAvatar;
    }

    public void setOrganizerAvatar(String organizerAvatar) {
        this.organizerAvatar = organizerAvatar;
    }

    public MeetingFormat getFormat() {
        return format;
    }

    public void setFormat(MeetingFormat format) {
        this.format = format;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getRegions() {
        return regions;
    }

    public void setRegions(String regions) {
        this.regions = regions;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getTargetAudience() {
        return targetAudience;
    }

    public void setTargetAudience(String targetAudience) {
        this.targetAudience = targetAudience;
    }

    public Boolean getIsPremium() {
        return isPremium;
    }

    public void setIsPremium(Boolean isPremium) {
        this.isPremium = isPremium;
    }

    public String getTakedownReason() {
        return takedownReason;
    }

    public void setTakedownReason(String takedownReason) {
        this.takedownReason = takedownReason;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    /**
     * 是否已解锁高阶数据（用户画像 + 简报高阶内容）。
     * 与 Meeting.isPremium 一致。
     */
    public boolean isAdvancedDataAvailable() {
        return Boolean.TRUE.equals(isPremium);
    }

    public List<ScheduleDay> getScheduleDays() {
        if (scheduleDays == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(scheduleDays);
    }

    public void setScheduleDays(List<ScheduleDay> scheduleDays) {
        this.scheduleDays = scheduleDays == null ? new ArrayList<>() : new ArrayList<>(scheduleDays);
    }

    // V1.2新增字段的getter/setter
    public LocalDateTime getRegEndTime() {
        return regEndTime;
    }

    public void setRegEndTime(LocalDateTime regEndTime) {
        this.regEndTime = regEndTime;
    }

    public String getCheckinCode() {
        return checkinCode;
    }

    public void setCheckinCode(String checkinCode) {
        this.checkinCode = checkinCode;
    }

    public Boolean getRequireCheckin() {
        return requireCheckin;
    }

    public void setRequireCheckin(Boolean requireCheckin) {
        this.requireCheckin = requireCheckin;
    }
}
