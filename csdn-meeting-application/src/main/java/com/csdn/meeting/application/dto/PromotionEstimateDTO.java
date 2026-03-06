package com.csdn.meeting.application.dto;

import java.math.BigDecimal;

/**
 * 推广估算结果（agent.prd §2.8）
 */
public class PromotionEstimateDTO {

    private long estimatedReach;
    private long estimatedImpressions;
    private long estimatedClicks;
    private BigDecimal basePrice;

    public long getEstimatedReach() { return estimatedReach; }
    public void setEstimatedReach(long estimatedReach) { this.estimatedReach = estimatedReach; }
    public long getEstimatedImpressions() { return estimatedImpressions; }
    public void setEstimatedImpressions(long estimatedImpressions) { this.estimatedImpressions = estimatedImpressions; }
    public long getEstimatedClicks() { return estimatedClicks; }
    public void setEstimatedClicks(long estimatedClicks) { this.estimatedClicks = estimatedClicks; }
    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }
}
