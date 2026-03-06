package com.csdn.meeting.interfaces.controller;

import com.csdn.meeting.application.dto.MeetingTemplateDTO;
import com.csdn.meeting.application.service.MeetingTemplateUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meeting-templates")
public class MeetingTemplateController {

    private final MeetingTemplateUseCase templateUseCase;

    public MeetingTemplateController(MeetingTemplateUseCase templateUseCase) {
        this.templateUseCase = templateUseCase;
    }

    /** 列表（仅启用） */
    @GetMapping
    public ResponseEntity<List<MeetingTemplateDTO>> listActive() {
        List<MeetingTemplateDTO> list = templateUseCase.listActive();
        return ResponseEntity.ok(list);
    }

    /** 详情 */
    @GetMapping("/{id}")
    public ResponseEntity<MeetingTemplateDTO> getDetail(@PathVariable Long id) {
        MeetingTemplateDTO dto = templateUseCase.getById(id);
        return ResponseEntity.ok(dto);
    }

    /** 管理员创建 */
    @PostMapping
    public ResponseEntity<MeetingTemplateDTO> create(@RequestBody MeetingTemplateDTO dto) {
        MeetingTemplateDTO created = templateUseCase.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /** 管理员更新 */
    @PutMapping("/{id}")
    public ResponseEntity<MeetingTemplateDTO> update(@PathVariable Long id, @RequestBody MeetingTemplateDTO dto) {
        MeetingTemplateDTO updated = templateUseCase.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    /** 管理员删除 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        templateUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
