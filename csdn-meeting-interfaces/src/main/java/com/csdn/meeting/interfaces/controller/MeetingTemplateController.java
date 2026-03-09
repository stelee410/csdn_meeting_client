package com.csdn.meeting.interfaces.controller;

import com.csdn.meeting.application.dto.MeetingTemplateDTO;
import com.csdn.meeting.application.service.MeetingTemplateUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<List<MeetingTemplateDTO>> listActive() {
        List<MeetingTemplateDTO> list = templateUseCase.listActive();
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "模板详情", description = "获取指定活动模板详情。")
    @GetMapping("/{id}")
    public ResponseEntity<MeetingTemplateDTO> getDetail(@PathVariable Long id) {
        MeetingTemplateDTO dto = templateUseCase.getById(id);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "创建模板", description = "管理员创建活动模板。")
    @PostMapping
    public ResponseEntity<MeetingTemplateDTO> create(@RequestBody MeetingTemplateDTO dto) {
        MeetingTemplateDTO created = templateUseCase.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "更新模板", description = "管理员更新活动模板。")
    @PutMapping("/{id}")
    public ResponseEntity<MeetingTemplateDTO> update(@PathVariable Long id, @RequestBody MeetingTemplateDTO dto) {
        MeetingTemplateDTO updated = templateUseCase.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "删除模板", description = "管理员删除/下线活动模板。")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        templateUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
