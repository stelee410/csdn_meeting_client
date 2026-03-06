package com.csdn.meeting.application.dto;

import java.math.BigDecimal;

/**
 * 推广配置命令（用于 estimate / createOrder）
 */
public class PromotionConfigCommand {

    private String userIntents;
    private String behaviorPeriod;
    private String targetBehaviors;
    private String targetRegions;
    private String targetIndustries;
    private String channels;
    private String payMode;

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
}
