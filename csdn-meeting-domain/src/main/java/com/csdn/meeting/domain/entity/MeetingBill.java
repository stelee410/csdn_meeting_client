package com.csdn.meeting.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 账单明细（本期不提供前端，仅建表记录流水）。
 */
public class MeetingBill {

    private Long id;
    private Long meetingId;
    private String feeType;     // PROMOTION, DATA_RIGHTS
    private BigDecimal amount;
    private String payStatus;   // PAID, UNPAID
    private String invoiceStatus;  // NONE, APPLIED, ISSUED
    private LocalDateTime createdAt;

    public static final String FEE_TYPE_PROMOTION = "PROMOTION";
    public static final String FEE_TYPE_DATA_RIGHTS = "DATA_RIGHTS";
    public static final String PAY_STATUS_PAID = "PAID";
    public static final String PAY_STATUS_UNPAID = "UNPAID";
    public static final String INVOICE_STATUS_NONE = "NONE";
    public static final String INVOICE_STATUS_APPLIED = "APPLIED";
    public static final String INVOICE_STATUS_ISSUED = "ISSUED";

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMeetingId() { return meetingId; }
    public void setMeetingId(Long meetingId) { this.meetingId = meetingId; }
    public String getFeeType() { return feeType; }
    public void setFeeType(String feeType) { this.feeType = feeType; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getPayStatus() { return payStatus; }
    public void setPayStatus(String payStatus) { this.payStatus = payStatus; }
    public String getInvoiceStatus() { return invoiceStatus; }
    public void setInvoiceStatus(String invoiceStatus) { this.invoiceStatus = invoiceStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
