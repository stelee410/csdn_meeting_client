package com.csdn.meeting.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 报名表单字段配置DTO
 * 用于定义报名表单中各个字段的配置
 */
@Data
@Schema(description = "报名表单字段配置")
public class FormFieldConfigDTO {

    @Schema(description = "字段标识", example = "name")
    private String fieldName;

    @Schema(description = "字段显示名称", example = "姓名")
    private String fieldLabel;

    @Schema(description = "字段类型：TEXT/PHONE/EMAIL/SELECT/TEXTAREA", example = "TEXT")
    private String fieldType;

    @Schema(description = "是否必填", example = "true")
    private Boolean required;

    @Schema(description = "是否允许用户编辑", example = "true")
    private Boolean editable;

    @Schema(description = "数据来源：user_profile用户画像/none无预填", example = "user_profile")
    private String source;

    @Schema(description = "输入提示", example = "请输入您的真实姓名")
    private String placeholder;

    @Schema(description = "排序顺序", example = "1")
    private Integer sortOrder;

    @Schema(description = "是否启用", example = "true")
    private Boolean enabled;

    @Schema(description = "选项列表（SELECT类型使用）")
    private List<Map<String, String>> options;

    @Schema(description = "字段默认值")
    private String defaultValue;

    @Schema(description = "字段验证规则（正则表达式）")
    private String validationRegex;

    @Schema(description = "验证失败提示信息")
    private String validationMessage;
}
