package com.csdn.meeting.interfaces.controller;

import com.csdn.meeting.application.dto.DictionaryDTO;
import com.csdn.meeting.application.service.DictionaryUseCase;
import com.csdn.meeting.interfaces.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 字典/下拉选项接口
 * issue001-2/6：会议时长、规模、频率、地域、目标人群、开发者类型
 */
@Tag(name = "字典接口")
@RestController
@RequestMapping("/api/dictionaries")
public class DictionaryController {

    private final DictionaryUseCase dictionaryUseCase;

    public DictionaryController(DictionaryUseCase dictionaryUseCase) {
        this.dictionaryUseCase = dictionaryUseCase;
    }

    @Operation(summary = "创建会议/模板用字典", description = "返回会议时长、会议规模、举办频率、地域、目标人群、开发者类型等下拉选项")
    @GetMapping
    public ResponseEntity<ApiResponse<DictionaryDTO>> getCreateMeetingDictionaries() {
        return ResponseEntity.ok(ApiResponse.success(dictionaryUseCase.getCreateMeetingDictionaries()));
    }
}
