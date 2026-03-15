package com.csdn.meeting.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 字典/下拉选项 DTO
 * issue001-2/6：会议时长、会议规模、举办频率、地域、目标人群、开发者类型
 */
@Data
@Schema(description = "字典选项")
public class DictionaryDTO {

    @Schema(description = "会议时长（模板/会议创建）")
    private List<Option> meetingDurations;

    @Schema(description = "会议规模（模板/会议创建）")
    private List<Option> meetingScales;

    @Schema(description = "举办频率（模板）")
    private List<Option> frequencies;

    @Schema(description = "会议举办地域（省/市）")
    private List<RegionOption> regions;

    @Schema(description = "目标人群")
    private List<Option> targetAudiences;

    @Schema(description = "开发者类型")
    private List<Option> developerTypes;

    @Schema(description = "主办方 / 公司（支持模糊匹配的候选列表）")
    private List<Option> organizers;

    @Data
    @Schema(description = "选项项")
    public static class Option {
        @Schema(description = "选项值", example = "half_day")
        private String value;
        @Schema(description = "显示名称", example = "半天")
        private String label;

        public Option() {
        }

        public Option(String value, String label) {
            this.value = value;
            this.label = label;
        }
    }

    @Data
    @Schema(description = "地域选项（省/市）")
    public static class RegionOption {
        @Schema(description = "省编码", example = "110000")
        private String provinceCode;
        @Schema(description = "省名称", example = "北京市")
        private String provinceName;
        @Schema(description = "下属城市列表")
        private List<CityOption> cities;

        public RegionOption() {
        }

        public RegionOption(String code, String name, List<CityOption> cities) {
            this.provinceCode = code;
            this.provinceName = name;
            this.cities = cities;
        }
    }

    @Data
    @Schema(description = "城市选项")
    public static class CityOption {
        @Schema(description = "城市编码", example = "110100")
        private String cityCode;
        @Schema(description = "城市名称", example = "北京市")
        private String cityName;

        public CityOption() {
        }

        public CityOption(String code, String name) {
            this.cityCode = code;
            this.cityName = name;
        }
    }
}
