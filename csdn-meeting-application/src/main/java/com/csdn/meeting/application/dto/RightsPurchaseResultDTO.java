package com.csdn.meeting.application.dto;

/**
 * 权益购买唤起收银台响应
 */
public class RightsPurchaseResultDTO {

    private String paymentUrl;  // 唤起 CSDN 统一收银台的 URL
    private String orderNo;

    public String getPaymentUrl() { return paymentUrl; }
    public void setPaymentUrl(String paymentUrl) { this.paymentUrl = paymentUrl; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
}
