package com.csdn.meeting.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

public class MeetingDTO {

    private Long id;
    private String meetingId;
    private String title;
    private String description;
    private String creatorId;
    private String creatorName;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime startTime;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime endTime;
    private String status;
    private Integer maxParticipants;
    private List<ParticipantDTO> participants;

    // agent.prd §1.1 新增字段
    private String organizer;
    private String format;           // ONLINE/OFFLINE/HYBRID
    private String scene;
    private String venue;
    private String regions;          // JSON
    private String coverImage;       // 封面图（列表/详情均返回）
    private String posterUrl;        // 海报图（详情页，API 文档要求）
    private String tags;             // 逗号分隔的 tagId，如 1,2,3
    private String targetAudience;   // JSON
    private Boolean isPremium;
    private String takedownReason;
    private String rejectReason;

    // 四级日程结构
    private List<ScheduleDayDTO> scheduleDays;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public List<ParticipantDTO> getParticipants() {
        return participants;
    }

    public void setParticipants(List<ParticipantDTO> participants) {
        this.participants = participants;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
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

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
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

    public List<ScheduleDayDTO> getScheduleDays() {
        return scheduleDays;
    }

    public void setScheduleDays(List<ScheduleDayDTO> scheduleDays) {
        this.scheduleDays = scheduleDays;
    }

    // ========== 移动端可操作标记 ==========
    private MobileOperationDTO mobileOperations;

    public MobileOperationDTO getMobileOperations() {
        return mobileOperations;
    }

    public void setMobileOperations(MobileOperationDTO mobileOperations) {
        this.mobileOperations = mobileOperations;
    }

    /**
     * 移动端可操作标记
     * 用于指示该会议在移动端可执行的操作
     */
    public static class MobileOperationDTO {
        private boolean canViewDetail = true;      // 可查看详情
        private boolean canViewBrief = true;       // 可查看简报
        private boolean canDownloadBrief = false;    // 可下载简报（会议结束后）

        // 以下操作移动端不提供（默认false）
        private boolean canPromote = false;          // 移动端不可推广
        private boolean canAudit = false;            // 移动端不可审核报名
        private boolean canSubmit = false;           // 移动端不可提交审核
        private boolean canWithdraw = false;         // 移动端不可撤回审核
        private boolean canTakedown = false;         // 移动端不可下架
        private boolean canDelete = false;           // 移动端不可删除

        public boolean isCanViewDetail() {
            return canViewDetail;
        }

        public void setCanViewDetail(boolean canViewDetail) {
            this.canViewDetail = canViewDetail;
        }

        public boolean isCanViewBrief() {
            return canViewBrief;
        }

        public void setCanViewBrief(boolean canViewBrief) {
            this.canViewBrief = canViewBrief;
        }

        public boolean isCanDownloadBrief() {
            return canDownloadBrief;
        }

        public void setCanDownloadBrief(boolean canDownloadBrief) {
            this.canDownloadBrief = canDownloadBrief;
        }

        public boolean isCanPromote() {
            return canPromote;
        }

        public void setCanPromote(boolean canPromote) {
            this.canPromote = canPromote;
        }

        public boolean isCanAudit() {
            return canAudit;
        }

        public void setCanAudit(boolean canAudit) {
            this.canAudit = canAudit;
        }

        public boolean isCanSubmit() {
            return canSubmit;
        }

        public void setCanSubmit(boolean canSubmit) {
            this.canSubmit = canSubmit;
        }

        public boolean isCanWithdraw() {
            return canWithdraw;
        }

        public void setCanWithdraw(boolean canWithdraw) {
            this.canWithdraw = canWithdraw;
        }

        public boolean isCanTakedown() {
            return canTakedown;
        }

        public void setCanTakedown(boolean canTakedown) {
            this.canTakedown = canTakedown;
        }

        public boolean isCanDelete() {
            return canDelete;
        }

        public void setCanDelete(boolean canDelete) {
            this.canDelete = canDelete;
        }
    }
}
