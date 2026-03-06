package com.csdn.meeting.application.dto;

import java.util.List;

/**
 * 推广估算请求参数（agent.prd §2.8, §1.7）
 */
public class PromotionEstimateCommand {

    private List<String> userIntents;      // 用户意图
    private String behaviorPeriod;         // 7d/15d/1m/2m/3m
    private List<String> targetBehaviors;  // ["SEARCH","CREATE"]
    private List<Long> targetRegions;      // 城市 ID 列表
    private List<String> targetIndustries; // 行业枚举
    private List<String> channels;         // ["SMS","EMAIL","PRIVATE_MSG","PUSH"]
    private String payMode;                // CPM/CPC/CPA

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
}
