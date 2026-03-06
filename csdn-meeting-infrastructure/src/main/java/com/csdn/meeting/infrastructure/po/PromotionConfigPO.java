package com.csdn.meeting.infrastructure.po;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_promotion_config", indexes = @Index(name = "idx_meeting_id", columnList = "meeting_id"))
public class PromotionConfigPO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "meeting_id", nullable = false)
    private Long meetingId;

    @Column(name = "user_intents", columnDefinition = "json")
    private String userIntents;

    @Column(name = "behavior_period", length = 10)
    private String behaviorPeriod;

    @Column(name = "target_behaviors", columnDefinition = "json")
    private String targetBehaviors;

    @Column(name = "target_regions", columnDefinition = "json")
    private String targetRegions;

    @Column(name = "target_industries", columnDefinition = "json")
    private String targetIndustries;

    @Column(name = "channels", columnDefinition = "json")
    private String channels;

    @Column(name = "pay_mode", length = 20)
    private String payMode;

    @Column(name = "estimated_reach")
    private Long estimatedReach;

    @Column(name = "estimated_impressions")
    private Long estimatedImpressions;

    @Column(name = "estimated_clicks")
    private Long estimatedClicks;

    @Column(name = "base_price", precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "order_status", length = 20)
    private String orderStatus;

    @Column(name = "order_created_at")
    private LocalDateTime orderCreatedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
    }

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
