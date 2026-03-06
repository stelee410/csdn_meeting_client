package com.csdn.meeting.domain.port;

/**
 * 管理后台通知端口：推广订单通知留档。
 * Infrastructure 实现（HTTP 回调或消息队列）。
 */
public interface AdminNotificationPort {

    /**
     * 通知管理后台：推广订单已生成。
     *
     * @param meetingId  会议ID
     * @param configId   推广配置ID
     * @param basePrice  原价
     */
    void notifyPromotionOrderCreated(Long meetingId, Long configId, java.math.BigDecimal basePrice);
}
