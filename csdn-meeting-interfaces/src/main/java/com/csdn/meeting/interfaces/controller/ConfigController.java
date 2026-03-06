package com.csdn.meeting.interfaces.controller;

import com.csdn.meeting.application.service.RightsPriceConfigService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

/**
 * 配置接口：权益价格 GET/POST（管理员）
 * agent.prd §4：/api/config/rights-price
 */
@RestController
@RequestMapping("/api/config")
public class ConfigController {

    private final RightsPriceConfigService rightsPriceConfig;

    public ConfigController(RightsPriceConfigService rightsPriceConfig) {
        this.rightsPriceConfig = rightsPriceConfig;
    }

    @GetMapping("/rights-price")
    public ResponseEntity<Map<String, BigDecimal>> getRightsPrice() {
        ensureAdmin();
        return ResponseEntity.ok(Collections.singletonMap("price", rightsPriceConfig.getPrice()));
    }

    @PostMapping("/rights-price")
    public ResponseEntity<Void> setRightsPrice(@RequestBody Map<String, BigDecimal> body) {
        ensureAdmin();
        BigDecimal price = body != null ? body.get("price") : null;
        if (price != null && price.compareTo(BigDecimal.ZERO) >= 0) {
            rightsPriceConfig.setPrice(price);
        }
        return ResponseEntity.ok().build();
    }

    private void ensureAdmin() {
        // TODO: 接入真实权限系统
    }
}
