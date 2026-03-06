package com.csdn.meeting.domain.port;

import java.math.BigDecimal;

/**
 * 支付端口：唤起收银台、支付回调处理。
 * Infrastructure 实现对接 CSDN 统一收银台。
 */
public interface PaymentPort {

    /**
     * 唤起收银台，返回支付跳转 URL 或订单号。
     *
     * @param meetingId  会议ID
     * @param amount     金额
     * @param feeType    费用类型（DATA_RIGHTS / PROMOTION）
     * @param orderNo    商户订单号
     * @return 收银台跳转 URL（stub 可返回空字符串）
     */
    String launchCashier(Long meetingId, BigDecimal amount, String feeType, String orderNo);

    /**
     * 处理支付回调，验证签名并返回是否成功。
     *
     * @param orderNo 订单号
     * @param amount  实际支付金额
     * @return 是否支付成功
     */
    boolean onPaymentCallback(String orderNo, BigDecimal amount);
}
