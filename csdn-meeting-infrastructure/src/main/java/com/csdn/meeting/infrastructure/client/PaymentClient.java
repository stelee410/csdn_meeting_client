package com.csdn.meeting.infrastructure.client;

import com.csdn.meeting.domain.port.PaymentPort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 支付客户端：实现 PaymentPort。
 * Stub：模拟收银台与回调，内存存储用于测试。
 */
@Component
public class PaymentClient implements PaymentPort {

    private final ConcurrentHashMap<String, StubPaymentRecord> records = new ConcurrentHashMap<>();

    @Override
    public String launchCashier(Long meetingId, BigDecimal amount, String feeType, String orderNo) {
        records.put(orderNo, new StubPaymentRecord(meetingId, amount, feeType));
        return "stub://cashier?orderNo=" + orderNo;
    }

    @Override
    public boolean onPaymentCallback(String orderNo, BigDecimal amount) {
        if (records.containsKey(orderNo)) {
            return amount != null && amount.compareTo(BigDecimal.ZERO) >= 0;
        }
        return false;
    }

    private static class StubPaymentRecord {
        final Long meetingId;
        final BigDecimal amount;
        final String feeType;

        StubPaymentRecord(Long meetingId, BigDecimal amount, String feeType) {
            this.meetingId = meetingId;
            this.amount = amount;
            this.feeType = feeType;
        }
    }
}
