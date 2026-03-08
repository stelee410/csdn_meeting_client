package com.csdn.meeting.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * 筛选选项DTO
 * 返回各筛选维度的可选值，供前端初始化筛选器
 */
@Schema(description = "筛选选项DTO，返回各筛选维度的可选值列表")
public class FilterOptionsDTO {

    /**
     * 会议形式选项
     */
    @Schema(description = "会议形式选项列表：ONLINE/OFFLINE/HYBRID")
    private List<FilterOption> formatOptions;

    /**
     * 会议类型选项
     */
    @Schema(description = "会议类型选项列表：SUMMIT/SALON/WORKSHOP")
    private List<FilterOption> typeOptions;

    /**
     * 会议场景选项
     */
    @Schema(description = "会议场景选项列表：DEVELOPER/INDUSTRY/PRODUCT/REGIONAL/UNIVERSITY")
    private List<FilterOption> sceneOptions;

    /**
     * 召开时间选项
     */
    @Schema(description = "召开时间选项列表：THIS_WEEK/THIS_MONTH/NEXT_3_MONTHS")
    private List<FilterOption> timeRangeOptions;

    public List<FilterOption> getFormatOptions() {
        return formatOptions;
    }

    public void setFormatOptions(List<FilterOption> formatOptions) {
        this.formatOptions = formatOptions;
    }

    public List<FilterOption> getTypeOptions() {
        return typeOptions;
    }

    public void setTypeOptions(List<FilterOption> typeOptions) {
        this.typeOptions = typeOptions;
    }

    public List<FilterOption> getSceneOptions() {
        return sceneOptions;
    }

    public void setSceneOptions(List<FilterOption> sceneOptions) {
        this.sceneOptions = sceneOptions;
    }

    public List<FilterOption> getTimeRangeOptions() {
        return timeRangeOptions;
    }

    public void setTimeRangeOptions(List<FilterOption> timeRangeOptions) {
        this.timeRangeOptions = timeRangeOptions;
    }

    /**
     * 筛选选项项
     */
    @Getter
    @Setter
    @Schema(description = "筛选选项项")
    public static class FilterOption {
        @Schema(description = "选项ID（传给后端的编码）", example = "1")
        private Integer code;

        @Schema(description = "选项值", example = "ONLINE")
        private String value;

        @Schema(description = "选项显示名称", example = "线上")
        private String label;

        @Schema(description = "选项描述/提示", example = "纯线上举办的会议")
        private String description;

        public FilterOption() {
        }

        public FilterOption(Integer code, String value, String label) {
            this.value = value;
            this.code = code;
            this.label = label;
        }

        public FilterOption(Integer code, String value, String label, String description) {
            this.code = code;
            this.value = value;
            this.label = label;
            this.description = description;
        }
    }
}
