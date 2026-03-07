package com.csdn.meeting.infrastructure.po;

import com.baomidou.mybatisplus.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("t_promotion_config")
public class PromotionConfigPO {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("meeting_id")
    private Long meetingId;

    @TableField("user_intents")
    private String userIntents;

    @TableField("behavior_period")
    private String behaviorPeriod;

    @TableField("target_behaviors")
    private String targetBehaviors;

    @TableField("target_regions")
    private String targetRegions;

    @TableField("target_industries")
    private String targetIndustries;

    @TableField("channels")
    private String channels;

    @TableField("pay_mode")
    private String payMode;

    @TableField("estimated_reach")
    private Long estimatedReach;

    @TableField("estimated_impressions")
    private Long estimatedImpressions;

    @TableField("estimated_clicks")
    private Long estimatedClicks;

    @TableField("base_price")
    private BigDecimal basePrice;

    @TableField("order_status")
    private String orderStatus;

    @TableField("order_created_at")
    private LocalDateTime orderCreatedAt;

    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 软删除标志：0-未删除, 1-已删除
     */
    @TableField(value = "is_deleted", fill = FieldFill.INSERT)
    @TableLogic
    private Integer isDeleted;

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
    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }
}
