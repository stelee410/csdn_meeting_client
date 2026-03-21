package com.csdn.meeting.interfaces.controller;

import com.csdn.meeting.application.dto.MeetingTemplateDTO;
import com.csdn.meeting.application.service.MeetingTemplateUseCase;
import com.csdn.meeting.interfaces.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "活动模板接口")
@RestController
@RequestMapping("/api/meeting-templates")
public class MeetingTemplateController {

    private final MeetingTemplateUseCase templateUseCase;

    public MeetingTemplateController(MeetingTemplateUseCase templateUseCase) {
        this.templateUseCase = templateUseCase;
    }

    @Operation(summary = "模板列表", description = "获取所有启用的活动模板（技术沙龙、新品发布会等）。")
    @GetMapping
    public ResponseEntity<ApiResponse<List<MeetingTemplateDTO>>> listActive() {
        List<MeetingTemplateDTO> list = templateUseCase.listActive();
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @Operation(summary = "模板详情", description = "获取指定活动模板详情。")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MeetingTemplateDTO>> getDetail(@PathVariable Long id) {
        MeetingTemplateDTO dto = templateUseCase.getById(id);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @Operation(summary = "创建模板", description = "管理员创建活动模板。")
    @PostMapping
    public ResponseEntity<ApiResponse<MeetingTemplateDTO>> create(@RequestBody MeetingTemplateDTO dto) {
        MeetingTemplateDTO created = templateUseCase.create(dto);
        return ResponseEntity.ok(ApiResponse.success(created));
    }

    @Operation(summary = "更新模板", description = "管理员更新活动模板。")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MeetingTemplateDTO>> update(@PathVariable Long id, @RequestBody MeetingTemplateDTO dto) {
        MeetingTemplateDTO updated = templateUseCase.update(id, dto);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    @Operation(summary = "下架模板", description = "将模板下架（isActive=false），列表接口不再返回，可随时上架。")
    @PatchMapping("/{id}/offline")
    public ResponseEntity<ApiResponse<MeetingTemplateDTO>> offline(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(templateUseCase.offline(id)));
    }

    @Operation(summary = "上架模板", description = "将已下架模板重新上架（isActive=true）。")
    @PatchMapping("/{id}/shelve")
    public ResponseEntity<ApiResponse<MeetingTemplateDTO>> shelve(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(templateUseCase.shelve(id)));
    }

    @Operation(summary = "删除模板", description = "逻辑删除活动模板。")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        templateUseCase.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
