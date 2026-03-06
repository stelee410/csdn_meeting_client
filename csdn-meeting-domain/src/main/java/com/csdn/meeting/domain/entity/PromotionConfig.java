package com.csdn.meeting.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 推广配置。
 * 订单生成后 30 分钟内支付享 85 折。
 */
public class PromotionConfig {

    private Long id;
    private Long meetingId;
    private String userIntents;      // JSON
    private String behaviorPeriod;   // 7d/15d/1m/2m/3m
    private String targetBehaviors;  // JSON ["SEARCH","CREATE"]
    private String targetRegions;    // JSON 城市ID列表
    private String targetIndustries; // JSON 行业枚举
    private String channels;         // JSON ["SMS","EMAIL","PRIVATE_MSG","PUSH"]
    private String payMode;          // CPM/CPC/CPA
    private Long estimatedReach;
    private Long estimatedImpressions;
    private Long estimatedClicks;
    private BigDecimal basePrice;
    private String orderStatus;      // PENDING, PAID
    private LocalDateTime orderCreatedAt;
    private LocalDateTime createdAt;

    public static final String ORDER_STATUS_PENDING = "PENDING";
    public static final String ORDER_STATUS_PAID = "PAID";

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMeetingId() { return meetingId; }
    public void setMeetingId(Long meetingId) { this.meetingId = meetingId; }
    public String getUserIntents() { return userIntents; }
    public void setUserIntents(String userIntents) { this.userIntents = userIntents; }
    public String getBehaviorPeriod() { return behaviorPeriod; }
    public void setBehaviorPeriod(String behaviorPeriod) { this.behaviorPeriod = behaviorPeriod; }
    public String getTargetBehaviors() { return targetBehaviors; }
    public void setTargetBehaviors(String targetBehaviors) { this.targetBehaviors = targetBehaviors; }
    public String getTargetRegions() { return targetRegions; }
    public void setTargetRegions(String targetRegions) { this.targetRegions = targetRegions; }
    public String getTargetIndustries() { return targetIndustries; }
    public void setTargetIndustries(String targetIndustries) { this.targetIndustries = targetIndustries; }
    public String getChannels() { return channels; }
    public void setChannels(String channels) { this.channels = channels; }
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
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
