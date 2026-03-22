package com.csdn.meeting.interfaces.controller;

import com.csdn.meeting.application.service.RightsPriceConfigService;
import com.csdn.meeting.interfaces.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

/**
 * 配置接口：权益价格 GET/POST（管理员）
 * agent.prd §4：/api/config/rights-price
 */
@Tag(name = "配置接口")
@RestController
@RequestMapping("/api/config")
public class ConfigController {

    private final RightsPriceConfigService rightsPriceConfig;

    public ConfigController(RightsPriceConfigService rightsPriceConfig) {
        this.rightsPriceConfig = rightsPriceConfig;
    }

    @Operation(summary = "获取权益价格", description = "获取会议数据高阶权益包价格（管理员）。")
    @GetMapping("/rights-price")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> getRightsPrice() {
        ensureAdmin();
        return ResponseEntity.ok(ApiResponse.success(Collections.singletonMap("price", rightsPriceConfig.getPrice())));
    }

    @Operation(summary = "设置权益价格", description = "设置会议数据高阶权益包价格（管理员）。")
    @PostMapping("/rights-price")
    public ResponseEntity<ApiResponse<Void>> setRightsPrice(@RequestBody Map<String, BigDecimal> body) {
        ensureAdmin();
        BigDecimal price = body != null ? body.get("price") : null;
        if (price != null && price.compareTo(BigDecimal.ZERO) >= 0) {
            rightsPriceConfig.setPrice(price);
        }
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    private void ensureAdmin() {
        // TODO: 接入真实权限系统
    }
}
