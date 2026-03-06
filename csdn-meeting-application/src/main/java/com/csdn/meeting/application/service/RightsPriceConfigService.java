package com.csdn.meeting.application.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 权益价格配置（agent.prd §4）
 * GET/POST 供管理员配置；内存存储，生产可改用 DB/配置中心
 */
@Service
public class RightsPriceConfigService {

    private final AtomicReference<BigDecimal> price = new AtomicReference<>(BigDecimal.valueOf(299.00));

    public BigDecimal getPrice() {
        return price.get();
    }

    public void setPrice(BigDecimal newPrice) {
        if (newPrice != null && newPrice.compareTo(BigDecimal.ZERO) >= 0) {
            price.set(newPrice);
        }
    }
}
