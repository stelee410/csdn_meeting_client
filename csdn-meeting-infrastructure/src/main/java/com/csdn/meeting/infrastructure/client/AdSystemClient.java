package com.csdn.meeting.infrastructure.client;

import com.csdn.meeting.domain.port.AdSystemPort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 广告系统客户端：实现 AdSystemPort。
 * Stub：返回模拟估算数据，无真实 API 调用。
 */
@Component
public class AdSystemClient implements AdSystemPort {

    @Override
    public PromotionEstimate estimate(String userIntents, String behaviorPeriod,
                                      String targetBehaviors, String targetRegions,
                                      String targetIndustries, String channels, String payMode) {
        // Stub: 返回模拟数据
        long reach = 10000;
        long impressions = 50000;
        long clicks = 1000;
        BigDecimal basePrice = BigDecimal.valueOf(999.00);
        return new PromotionEstimate(reach, impressions, clicks, basePrice);
    }
}
