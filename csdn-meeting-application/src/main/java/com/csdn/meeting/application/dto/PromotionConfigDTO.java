package com.csdn.meeting.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 推广配置与状态（agent.prd §2.8, §1.7）
 */
public class PromotionConfigDTO {

    private Long configId;
    private Long meetingId;
    private List<String> userIntents;
    private String behaviorPeriod;
    private List<String> targetBehaviors;
    private List<Long> targetRegions;
    private List<String> targetIndustries;
    private List<String> channels;
    private String payMode;
    private Long estimatedReach;
    private Long estimatedImpressions;
    private Long estimatedClicks;
    private BigDecimal basePrice;
    private String orderStatus;  // PENDING/PAID
    private LocalDateTime orderCreatedAt;

    public Long getConfigId() { return configId; }
    public void setConfigId(Long configId) { this.configId = configId; }
    public Long getMeetingId() { return meetingId; }
    public void setMeetingId(Long meetingId) { this.meetingId = meetingId; }
    public List<String> getUserIntents() { return userIntents; }
    public void setUserIntents(List<String> userIntents) { this.userIntents = userIntents; }
    public String getBehaviorPeriod() { return behaviorPeriod; }
    public void setBehaviorPeriod(String behaviorPeriod) { this.behaviorPeriod = behaviorPeriod; }
    public List<String> getTargetBehaviors() { return targetBehaviors; }
    public void setTargetBehaviors(List<String> targetBehaviors) { this.targetBehaviors = targetBehaviors; }
    public List<Long> getTargetRegions() { return targetRegions; }
    public void setTargetRegions(List<Long> targetRegions) { this.targetRegions = targetRegions; }
    public List<String> getTargetIndustries() { return targetIndustries; }
    public void setTargetIndustries(List<String> targetIndustries) { this.targetIndustries = targetIndustries; }
    public List<String> getChannels() { return channels; }
    public void setChannels(List<String> channels) { this.channels = channels; }
    public String getPayMode() { return payMode; }
    public void setPayMode(String payMode) { this.payMode = payMode; }
    public Long getEstimatedReach() { return estimatedReach; }
    public void setEstimatedReach(Long estimatedReach) { this.estimatedReach = estimatedReach; }
    public Long getEstimatedImpressions() { return estimatedImpressions; }
    public void setEstimatedImpressions(Long estimatedImpressions) { this.estimatedImpressions = estimatedImpressions; }
    public Long getEstimatedClicks() { return estimatedClicks; }
    public void setEstimatedClicks(Long estimatedClicks) { this.estimatedClicks = estimatedClicks; }
    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }
    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
    public LocalDateTime getOrderCreatedAt() { return orderCreatedAt; }
    public void setOrderCreatedAt(LocalDateTime orderCreatedAt) { this.orderCreatedAt = orderCreatedAt; }
}
