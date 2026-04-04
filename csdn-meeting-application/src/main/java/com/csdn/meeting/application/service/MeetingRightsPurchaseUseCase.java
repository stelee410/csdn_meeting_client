package com.csdn.meeting.application.service;

import com.csdn.meeting.application.dto.MeetingRightsDTO;
import com.csdn.meeting.application.dto.RightsPurchaseResultDTO;
import com.csdn.meeting.domain.entity.Meeting;
import com.csdn.meeting.domain.entity.MeetingBill;
import com.csdn.meeting.domain.entity.MeetingRights;
import com.csdn.meeting.domain.port.PaymentPort;
import com.csdn.meeting.domain.repository.MeetingBillRepository;
import com.csdn.meeting.domain.repository.MeetingRepository;
import com.csdn.meeting.domain.repository.MeetingRightsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 权益购买用例（agent.prd §2.6）
 * 唤起收银台；支付回调更新 MeetingRights、Meeting.isPremium、MeetingBill
 */
@Service
public class MeetingRightsPurchaseUseCase {

    private static final Logger log = LoggerFactory.getLogger(MeetingRightsPurchaseUseCase.class);

    private final MeetingRepository meetingRepository;
    private final MeetingRightsRepository rightsRepository;
    private final MeetingBillRepository billRepository;
    private final PaymentPort paymentPort;
    private final RightsPriceConfigService rightsPriceConfig;

    public MeetingRightsPurchaseUseCase(MeetingRepository meetingRepository,
                                        MeetingRightsRepository rightsRepository,
                                        MeetingBillRepository billRepository,
                                        PaymentPort paymentPort,
                                        RightsPriceConfigService rightsPriceConfig) {
        this.meetingRepository = meetingRepository;
        this.rightsRepository = rightsRepository;
        this.billRepository = billRepository;
        this.paymentPort = paymentPort;
        this.rightsPriceConfig = rightsPriceConfig;
    }

    public MeetingRightsDTO getRights(Long meetingId) {
        return rightsRepository.findActiveByMeetingId(meetingId)
                .map(this::toRightsDTO)
                .orElse(null);
    }

    private MeetingRightsDTO toRightsDTO(MeetingRights r) {
        MeetingRightsDTO dto = new MeetingRightsDTO();
        dto.setRightsType(r.getRightsType());
        dto.setStatus(r.getStatus());
        dto.setActiveTime(r.getActiveTime());
        return dto;
    }

    /**
     * 唤起收银台，返回支付跳转 URL
     */
    public RightsPurchaseResultDTO purchase(Long meetingId, String userId) {
        BigDecimal price = rightsPriceConfig.getPrice();
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("会议不存在: " + meetingId));
        if (meeting.getStatus() != Meeting.MeetingStatus.PUBLISHED
                && meeting.getStatus() != Meeting.MeetingStatus.IN_PROGRESS
                && meeting.getStatus() != Meeting.MeetingStatus.ENDED) {
            throw new IllegalStateException("仅已发布/进行中/已结束的会议可购买权益");
        }
        if (Boolean.TRUE.equals(meeting.getIsPremium())) {
            throw new IllegalStateException("该会议已购买高阶权益");
        }
        String orderNo = "RIGHTS-" + meetingId + "-" + UUID.randomUUID().toString().substring(0, 8);
        String url = paymentPort.launchCashier(meetingId, price, MeetingBill.FEE_TYPE_DATA_RIGHTS, orderNo);
        RightsPurchaseResultDTO dto = new RightsPurchaseResultDTO();
        dto.setPaymentUrl(url);
        dto.setOrderNo(orderNo);
        return dto;
    }

    /**
     * 支付回调处理（stub：直接模拟成功并更新权益）
     */
    @Transactional
    public void onPaymentCallback(String orderNo, BigDecimal amount) {
        if (!paymentPort.onPaymentCallback(orderNo, amount)) {
            throw new IllegalArgumentException("支付回调验证失败: " + orderNo);
        }
        // Stub flow: 从 orderNo 解析 meetingId（格式 RIGHTS-{meetingId}-xxx）
        String[] parts = orderNo.split("-");
        if (parts.length < 2) return;
        try {
            Long meetingId = Long.parseLong(parts[1]);
            activateRights(meetingId, orderNo, amount);
        } catch (NumberFormatException e) {
            log.warn("Cannot parse meetingId from orderNo: {}", orderNo);
        }
    }

    /** 激活权益：创建 MeetingRights、更新 Meeting.isPremium、记录 MeetingBill */
    @Transactional
    public void activateRights(Long meetingId, String orderNo, BigDecimal amount) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("会议不存在: " + meetingId));

        MeetingRights rights = new MeetingRights();
        rights.setMeetingId(meetingId);
        rights.setRightsType(MeetingRights.RIGHTS_TYPE_DATA_PREMIUM);
        rights.setStatus(MeetingRights.STATUS_ACTIVE);
        rights.setActiveTime(LocalDateTime.now());
        rights.setOrderNo(orderNo);
        rightsRepository.save(rights);

        meeting.setIsPremium(true);
        meetingRepository.save(meeting);

        MeetingBill bill = new MeetingBill();
        bill.setMeetingId(meetingId);
        bill.setFeeType(MeetingBill.FEE_TYPE_DATA_RIGHTS);
        bill.setAmount(amount);
        bill.setPayStatus(MeetingBill.PAY_STATUS_PAID);
        bill.setInvoiceStatus(MeetingBill.INVOICE_STATUS_NONE);
        billRepository.save(bill);

        log.info("[AUDIT] Rights purchased: meetingId={} orderNo={} amount={}", meetingId, orderNo, amount);
    }
}
