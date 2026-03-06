package com.csdn.meeting.domain.port;

import java.math.BigDecimal;

/**
 * 广告系统端口：推广效果估算。
 * Infrastructure 实现对接 CSDN 广告系统 API。
 */
public interface AdSystemPort {

    /**
     * 推广效果估算。
     *
     * @param userIntents      用户意图 JSON
     * @param behaviorPeriod   行为周期 7d/15d/1m 等
     * @param targetBehaviors  目标行为 JSON
     * @param targetRegions    目标地域 JSON
     * @param targetIndustries 目标行业 JSON
     * @param channels         渠道 JSON
     * @param payMode          CPM/CPC/CPA
     * @return 估算结果 [estimatedReach, estimatedImpressions, estimatedClicks, basePrice]
     */
    PromotionEstimate estimate(String userIntents, String behaviorPeriod,
                               String targetBehaviors, String targetRegions,
                               String targetIndustries, String channels, String payMode);

    /** 推广估算结果 */
    class PromotionEstimate {
        private long estimatedReach;
        private long estimatedImpressions;
        private long estimatedClicks;
        private BigDecimal basePrice;

        public PromotionEstimate(long reach, long impressions, long clicks, BigDecimal basePrice) {
            this.estimatedReach = reach;
            this.estimatedImpressions = impressions;
            this.estimatedClicks = clicks;
            this.basePrice = basePrice != null ? basePrice : BigDecimal.ZERO;
        }

        public long getEstimatedReach() { return estimatedReach; }
        public long getEstimatedImpressions() { return estimatedImpressions; }
        public long getEstimatedClicks() { return estimatedClicks; }
        public BigDecimal getBasePrice() { return basePrice; }
    }
}
