package com.csdn.meeting.infrastructure.client;

import com.csdn.meeting.domain.port.AdminNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 管理后台通知客户端：实现 AdminNotificationPort。
 * Stub：仅打日志，无真实 HTTP/消息队列调用。
 */
@Component
public class AdminNotificationClient implements AdminNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(AdminNotificationClient.class);

    @Override
    public void notifyPromotionOrderCreated(Long meetingId, Long configId, BigDecimal basePrice) {
        log.info("[STUB] AdminNotification: promotion order created meetingId={} configId={} basePrice={}",
                meetingId, configId, basePrice);
    }
}
