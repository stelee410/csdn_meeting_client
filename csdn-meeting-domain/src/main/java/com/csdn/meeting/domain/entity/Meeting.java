package com.csdn.meeting.domain.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Meeting extends BaseEntity {

    private String meetingId;
    private String title;
    private String description;
    private Long creatorId;
    private String creatorName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private MeetingStatus status;
    private Integer maxParticipants;

    // 新增字段（agent.prd §1.1）
    private String organizer;
    private MeetingFormat format;
    private String scene;
    private String venue;
    private String regions;           // JSON
    private String coverImage;
    private String tags;              // JSON
    private String targetAudience;    // JSON
    private Boolean isPremium;
    private String takedownReason;
    private String rejectReason;

    // 四级日程结构
    private List<ScheduleDay> scheduleDays;

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
    }

    // ---- submit / withdraw ----

    /**
     * 提交审核：DRAFT 或 REJECTED -> PENDING_REVIEW
     * 四级日程完整性由 MeetingDomainService 在调用前校验
     */
    public void submit() {
        if (this.status != MeetingStatus.DRAFT && this.status != MeetingStatus.REJECTED) {
            throw new IllegalStateException(
                "只有草稿或已拒绝状态才能提交审核，当前状态: " + this.status);
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
     * 审核通过：PENDING_REVIEW -> PUBLISHED
     * 由管理员触发，审计日志由调用方记录
     */
    public void approve() {
        if (this.status != MeetingStatus.PENDING_REVIEW) {
            throw new IllegalStateException(
                "只有待审核状态才能审核通过，当前状态: " + this.status);
        }
        this.status = MeetingStatus.PUBLISHED;
    }

    /**
     * 审核拒绝：PENDING_REVIEW -> REJECTED
     * 由管理员触发，rejectReason 必填
     *
     * @param reason 拒绝原因，不能为空
     */
    public void reject(String reason) {
        if (this.status != MeetingStatus.PENDING_REVIEW) {
            throw new IllegalStateException(
                "只有待审核状态才能审核拒绝，当前状态: " + this.status);
        }
        if (reason == null || reason.isBlank()) {
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
        if (reason == null || reason.isBlank()) {
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

    // ---- getters / setters ----

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public MeetingStatus getStatus() {
        return status;
    }

    public void setStatus(MeetingStatus status) {
        this.status = status;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
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
}
