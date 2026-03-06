package com.csdn.meeting.interfaces.controller;

import com.csdn.meeting.application.dto.PromotionConfigDTO;
import com.csdn.meeting.application.dto.PromotionEstimateCommand;
import com.csdn.meeting.application.dto.PromotionEstimateDTO;
import com.csdn.meeting.application.dto.PromotionOrderResultDTO;
import com.csdn.meeting.application.service.PromotionUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 推广配置 REST API（agent.prd §2.8）
 */
@RestController
@RequestMapping("/api/meetings")
public class PromotionController {

    private final PromotionUseCase promotionUseCase;

    public PromotionController(PromotionUseCase promotionUseCase) {
        this.promotionUseCase = promotionUseCase;
    }

    /** 实时估算（agent.prd §2.8） */
    @PostMapping("/{id}/promotion/estimate")
    public ResponseEntity<PromotionEstimateDTO> estimate(
            @PathVariable Long id,
            @RequestBody(required = false) PromotionEstimateCommand command) {
        PromotionEstimateDTO dto = promotionUseCase.estimate(id, command);
        return ResponseEntity.ok(dto);
    }

    /** 生成推广订单（agent.prd §2.8） */
    @PostMapping("/{id}/promotion/order")
    public ResponseEntity<PromotionOrderResultDTO> createOrder(
            @PathVariable Long id,
            @RequestBody(required = false) PromotionEstimateCommand command) {
        PromotionOrderResultDTO dto = promotionUseCase.createOrder(id, command);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    /** 查询推广配置与状态（agent.prd §2.8） */
    @GetMapping("/{id}/promotion")
    public ResponseEntity<PromotionConfigDTO> getPromotion(@PathVariable Long id) {
        PromotionConfigDTO dto = promotionUseCase.getPromotion(id);
        return ResponseEntity.ok(dto);
    }
}
