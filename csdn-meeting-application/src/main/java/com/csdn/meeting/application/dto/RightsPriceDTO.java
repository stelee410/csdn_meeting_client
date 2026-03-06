package com.csdn.meeting.application.dto;

import java.math.BigDecimal;

/**
 * 权益价格配置（agent.prd 权益价格由运营后台配置）
 */
public class RightsPriceDTO {

    private BigDecimal price;       // DATA_PREMIUM 权益价格
    private String rightsType;      // DATA_PREMIUM

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getRightsType() { return rightsType; }
    public void setRightsType(String rightsType) { this.rightsType = rightsType; }
}
