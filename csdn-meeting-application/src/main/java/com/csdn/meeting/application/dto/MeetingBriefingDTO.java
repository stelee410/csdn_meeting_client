package com.csdn.meeting.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

/**
 * 会议简报预览（弹窗）结构化数据；满意度与互动等为占位字段，待业务接入后填充。
 */
@Schema(description = "会议简报预览数据")
public class MeetingBriefingDTO {

    @Schema(description = "会议标题")
    private String meetingTitle;

    @Schema(description = "智能总结正文（当前为规则模板生成，非大模型）")
    private String aiSummary;

    @Schema(description = "亮点卡片：参会人数（优先已签到，无签到则为已通过审核可参会人数）")
    private int attendanceCount;

    @Schema(description = "参会规模副文案")
    private String attendanceSubLabel;

    @Schema(description = "签到率 0~1，分母为已通过审核（已报名+已签到）")
    private double signinRate;

    @Schema(description = "签到率副文案")
    private String signinSubLabel;

    @Schema(description = "满意度均分，未接入时为 null")
    private Double satisfactionScore;

    @Schema(description = "互动次数，未接入时为 null")
    private Integer interactionCount;

    @Schema(description = "会议时间展示")
    private String meetingTimeDisplay;

    @Schema(description = "地点展示")
    private String locationDisplay;

    @Schema(description = "计划规模展示，如 500人")
    private String plannedScaleDisplay;

    @Schema(description = "会议时长展示")
    private String durationDisplay;

    @Schema(description = "会议简介")
    private String intro;

    @Schema(description = "议题/标签名称")
    private List<String> topicTags = new ArrayList<>();

    @Schema(description = "当前报名人数（与会议聚合字段一致口径，含待审核等）")
    private long totalRegistrations;

    @Schema(description = "已签到人数")
    private long checkedInCount;

    @Schema(description = "已通过审核人数（已报名+已签到，用作签到率分母）")
    private long eligibleApprovedCount;

    @Schema(description = "简报生成时间展示 yyyy/M/d")
    private String generatedAt;

    public String getMeetingTitle() {
        return meetingTitle;
    }

    public void setMeetingTitle(String meetingTitle) {
        this.meetingTitle = meetingTitle;
    }

    public String getAiSummary() {
        return aiSummary;
    }

    public void setAiSummary(String aiSummary) {
        this.aiSummary = aiSummary;
    }

    public int getAttendanceCount() {
        return attendanceCount;
    }

    public void setAttendanceCount(int attendanceCount) {
        this.attendanceCount = attendanceCount;
    }

    public String getAttendanceSubLabel() {
        return attendanceSubLabel;
    }

    public void setAttendanceSubLabel(String attendanceSubLabel) {
        this.attendanceSubLabel = attendanceSubLabel;
    }

    public double getSigninRate() {
        return signinRate;
    }

    public void setSigninRate(double signinRate) {
        this.signinRate = signinRate;
    }

    public String getSigninSubLabel() {
        return signinSubLabel;
    }

    public void setSigninSubLabel(String signinSubLabel) {
        this.signinSubLabel = signinSubLabel;
    }

    public Double getSatisfactionScore() {
        return satisfactionScore;
    }

    public void setSatisfactionScore(Double satisfactionScore) {
        this.satisfactionScore = satisfactionScore;
    }

    public Integer getInteractionCount() {
        return interactionCount;
    }

    public void setInteractionCount(Integer interactionCount) {
        this.interactionCount = interactionCount;
    }

    public String getMeetingTimeDisplay() {
        return meetingTimeDisplay;
    }

    public void setMeetingTimeDisplay(String meetingTimeDisplay) {
        this.meetingTimeDisplay = meetingTimeDisplay;
    }

    public String getLocationDisplay() {
        return locationDisplay;
    }

    public void setLocationDisplay(String locationDisplay) {
        this.locationDisplay = locationDisplay;
    }

    public String getPlannedScaleDisplay() {
        return plannedScaleDisplay;
    }

    public void setPlannedScaleDisplay(String plannedScaleDisplay) {
        this.plannedScaleDisplay = plannedScaleDisplay;
    }

    public String getDurationDisplay() {
        return durationDisplay;
    }

    public void setDurationDisplay(String durationDisplay) {
        this.durationDisplay = durationDisplay;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public List<String> getTopicTags() {
        return topicTags;
    }

    public void setTopicTags(List<String> topicTags) {
        this.topicTags = topicTags != null ? topicTags : new ArrayList<>();
    }

    public long getTotalRegistrations() {
        return totalRegistrations;
    }

    public void setTotalRegistrations(long totalRegistrations) {
        this.totalRegistrations = totalRegistrations;
    }

    public long getCheckedInCount() {
        return checkedInCount;
    }

    public void setCheckedInCount(long checkedInCount) {
        this.checkedInCount = checkedInCount;
    }

    public long getEligibleApprovedCount() {
        return eligibleApprovedCount;
    }

    public void setEligibleApprovedCount(long eligibleApprovedCount) {
        this.eligibleApprovedCount = eligibleApprovedCount;
    }

    public String getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(String generatedAt) {
        this.generatedAt = generatedAt;
    }
}
