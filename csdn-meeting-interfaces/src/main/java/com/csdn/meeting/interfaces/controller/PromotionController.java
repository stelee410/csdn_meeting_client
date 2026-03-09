package com.csdn.meeting.interfaces.controller;

import com.csdn.meeting.application.dto.PromotionConfigDTO;
import com.csdn.meeting.application.dto.PromotionEstimateCommand;
import com.csdn.meeting.application.dto.PromotionEstimateDTO;
import com.csdn.meeting.application.dto.PromotionOrderResultDTO;
import com.csdn.meeting.application.service.PromotionUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 推广配置 REST API（agent.prd §2.8）
 */
@Tag(name = "推广配置接口")
@RestController
@RequestMapping("/api/meetings")
public class PromotionController {

    private final PromotionUseCase promotionUseCase;

    public PromotionController(PromotionUseCase promotionUseCase) {
        this.promotionUseCase = promotionUseCase;
    }

    @Operation(summary = "推广实时估算", description = "根据圈选条件实时计算预计覆盖、曝光、点击及费用。")
    @PostMapping("/{id}/promotion/estimate")
    public ResponseEntity<PromotionEstimateDTO> estimate(
            @PathVariable Long id,
            @RequestBody(required = false) PromotionEstimateCommand command) {
        PromotionEstimateDTO dto = promotionUseCase.estimate(id, command);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "生成推广订单", description = "创建待支付推广订单，30 分钟内支付享 85 折；通知管理后台留档。")
    @PostMapping("/{id}/promotion/order")
    public ResponseEntity<PromotionOrderResultDTO> createOrder(
            @PathVariable Long id,
            @RequestBody(required = false) PromotionEstimateCommand command) {
        PromotionOrderResultDTO dto = promotionUseCase.createOrder(id, command);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @Operation(summary = "查询推广配置", description = "获取会议当前推广配置与订单状态。")
    @GetMapping("/{id}/promotion")
    public ResponseEntity<PromotionConfigDTO> getPromotion(@PathVariable Long id) {
        PromotionConfigDTO dto = promotionUseCase.getPromotion(id);
        return ResponseEntity.ok(dto);
    }
}
