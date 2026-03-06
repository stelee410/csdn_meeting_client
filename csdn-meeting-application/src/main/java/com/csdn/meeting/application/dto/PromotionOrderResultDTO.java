package com.csdn.meeting.application.dto;

import java.time.LocalDateTime;

/**
 * 推广订单生成响应（agent.prd §2.8）
 */
public class PromotionOrderResultDTO {

    private Long configId;
    private LocalDateTime orderCreatedAt;
    private LocalDateTime discountDeadline;  // 85 折截止时间（30 分钟内）

    public Long getConfigId() { return configId; }
    public void setConfigId(Long configId) { this.configId = configId; }
    public LocalDateTime getOrderCreatedAt() { return orderCreatedAt; }
    public void setOrderCreatedAt(LocalDateTime orderCreatedAt) { this.orderCreatedAt = orderCreatedAt; }
    public LocalDateTime getDiscountDeadline() { return discountDeadline; }
    public void setDiscountDeadline(LocalDateTime discountDeadline) { this.discountDeadline = discountDeadline; }
}
