package com.csdn.meeting.application.service;

import com.csdn.meeting.application.dto.PromotionConfigDTO;
import com.csdn.meeting.application.dto.PromotionEstimateCommand;
import com.csdn.meeting.application.dto.PromotionEstimateDTO;
import com.csdn.meeting.application.dto.PromotionOrderResultDTO;
import com.csdn.meeting.domain.entity.Meeting;
import com.csdn.meeting.domain.entity.PromotionConfig;
import com.csdn.meeting.domain.port.AdSystemPort;
import com.csdn.meeting.domain.port.AdminNotificationPort;
import com.csdn.meeting.domain.repository.MeetingRepository;
import com.csdn.meeting.domain.repository.PromotionConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 推广用例（agent.prd §2.8）
 * estimate：调用广告系统；createOrder：生成订单、通知管理后台、85 折倒计时
 */
@Service
public class PromotionUseCase {

    private static final Logger log = LoggerFactory.getLogger(PromotionUseCase.class);
    private static final int DISCOUNT_MINUTES = 30;
    private static final double DISCOUNT_RATE = 0.85;

    private final MeetingRepository meetingRepository;
    private final PromotionConfigRepository promotionConfigRepository;
    private final AdSystemPort adSystemPort;
    private final AdminNotificationPort adminNotificationPort;

    public PromotionUseCase(MeetingRepository meetingRepository,
                            PromotionConfigRepository promotionConfigRepository,
                            AdSystemPort adSystemPort,
                            AdminNotificationPort adminNotificationPort) {
        this.meetingRepository = meetingRepository;
        this.promotionConfigRepository = promotionConfigRepository;
        this.adSystemPort = adSystemPort;
        this.adminNotificationPort = adminNotificationPort;
    }

    public PromotionEstimateDTO estimate(Long meetingId, PromotionEstimateCommand command) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("会议不存在: " + meetingId));
        if (meeting.getStatus() != Meeting.MeetingStatus.PUBLISHED) {
            throw new IllegalStateException("仅已发布状态的会议可配置推广");
        }
        String ui = toJson(command != null ? command.getUserIntents() : null);
        String tb = toJson(command != null ? command.getTargetBehaviors() : null);
        String tr = toJson(command != null ? command.getTargetRegions() : null);
        String ti = toJson(command != null ? command.getTargetIndustries() : null);
        String ch = toJson(command != null ? command.getChannels() : null);
        AdSystemPort.PromotionEstimate est = adSystemPort.estimate(
                ui, command != null ? command.getBehaviorPeriod() : null, tb, tr, ti, ch,
                command != null ? command.getPayMode() : null);
        PromotionEstimateDTO dto = new PromotionEstimateDTO();
        dto.setEstimatedReach(est.getEstimatedReach());
        dto.setEstimatedImpressions(est.getEstimatedImpressions());
        dto.setEstimatedClicks(est.getEstimatedClicks());
        dto.setBasePrice(est.getBasePrice());
        return dto;
    }

    @Transactional
    public PromotionOrderResultDTO createOrder(Long meetingId, PromotionEstimateCommand command) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("会议不存在: " + meetingId));
        if (meeting.getStatus() != Meeting.MeetingStatus.PUBLISHED) {
            throw new IllegalStateException("仅已发布状态的会议可创建推广订单");
        }
        String ui = toJson(command != null ? command.getUserIntents() : null);
        String tb = toJson(command != null ? command.getTargetBehaviors() : null);
        String tr = toJson(command != null ? command.getTargetRegions() : null);
        String ti = toJson(command != null ? command.getTargetIndustries() : null);
        String ch = toJson(command != null ? command.getChannels() : null);
        AdSystemPort.PromotionEstimate est = adSystemPort.estimate(
                ui, command != null ? command.getBehaviorPeriod() : null, tb, tr, ti, ch,
                command != null ? command.getPayMode() : null);

        PromotionConfig config = new PromotionConfig();
        config.setMeetingId(meetingId);
        config.setUserIntents(ui);
        config.setBehaviorPeriod(command != null ? command.getBehaviorPeriod() : null);
        config.setTargetBehaviors(tb);
        config.setTargetRegions(tr);
        config.setTargetIndustries(ti);
        config.setChannels(ch);
        config.setPayMode(command != null ? command.getPayMode() : null);
        config.setEstimatedReach(est.getEstimatedReach());
        config.setEstimatedImpressions(est.getEstimatedImpressions());
        config.setEstimatedClicks(est.getEstimatedClicks());
        config.setBasePrice(est.getBasePrice());
        config.setOrderStatus(PromotionConfig.ORDER_STATUS_PENDING);
        config.setOrderCreatedAt(LocalDateTime.now());
        PromotionConfig saved = promotionConfigRepository.save(config);

        adminNotificationPort.notifyPromotionOrderCreated(meetingId, saved.getId(), saved.getBasePrice());
        log.info("[AUDIT] Promotion order created: meetingId={} configId={} basePrice={}",
                meetingId, saved.getId(), saved.getBasePrice());

        LocalDateTime deadline = saved.getOrderCreatedAt().plusMinutes(DISCOUNT_MINUTES);
        PromotionOrderResultDTO dto = new PromotionOrderResultDTO();
        dto.setConfigId(saved.getId());
        dto.setOrderCreatedAt(saved.getOrderCreatedAt());
        dto.setDiscountDeadline(deadline);
        return dto;
    }

    public PromotionConfigDTO getPromotion(Long meetingId) {
        return promotionConfigRepository.findByMeetingId(meetingId)
                .map(this::toConfigDTO)
                .orElse(null);
    }

    private PromotionConfigDTO toConfigDTO(PromotionConfig c) {
        PromotionConfigDTO dto = new PromotionConfigDTO();
        dto.setConfigId(c.getId());
        dto.setMeetingId(c.getMeetingId());
        dto.setBehaviorPeriod(c.getBehaviorPeriod());
        dto.setPayMode(c.getPayMode());
        dto.setEstimatedReach(c.getEstimatedReach());
        dto.setEstimatedImpressions(c.getEstimatedImpressions());
        dto.setEstimatedClicks(c.getEstimatedClicks());
        dto.setBasePrice(c.getBasePrice());
        dto.setOrderStatus(c.getOrderStatus());
        dto.setOrderCreatedAt(c.getOrderCreatedAt());
        return dto;
    }

    private static String toJson(java.util.List<?> list) {
        if (list == null || list.isEmpty()) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append("\"").append(list.get(i).toString().replace("\"", "\\\"")).append("\"");
        }
        sb.append("]");
        return sb.toString();
    }

    /** 计算实际价格（30 分钟内 85 折） */
    public static java.math.BigDecimal calcActualPrice(java.math.BigDecimal basePrice, LocalDateTime orderCreatedAt) {
        if (basePrice == null) return java.math.BigDecimal.ZERO;
        if (orderCreatedAt == null) return basePrice;
        if (LocalDateTime.now().isBefore(orderCreatedAt.plusMinutes(DISCOUNT_MINUTES))) {
            return basePrice.multiply(java.math.BigDecimal.valueOf(DISCOUNT_RATE));
        }
        return basePrice;
    }
}
