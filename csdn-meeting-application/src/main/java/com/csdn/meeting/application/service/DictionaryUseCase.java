package com.csdn.meeting.application.service;

import com.csdn.meeting.application.dto.DictionaryDTO;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 字典/下拉选项 UseCase
 * issue001-2/6：提供会议时长、规模、频率、地域、目标人群、开发者类型等选项
 */
@Service
public class DictionaryUseCase {

    public DictionaryDTO getCreateMeetingDictionaries() {
        DictionaryDTO dto = new DictionaryDTO();
        dto.setMeetingDurations(getMeetingDurationOptions());
        dto.setMeetingScales(getMeetingScaleOptions());
        dto.setFrequencies(getFrequencyOptions());
        dto.setRegions(getRegionOptions());
        dto.setTargetAudiences(getTargetAudienceOptions());
        dto.setDeveloperTypes(getDeveloperTypeOptions());
        return dto;
    }

    /**
     * 会议时长选项
     */
    public List<DictionaryDTO.Option> getMeetingDurationOptions() {
        return Arrays.asList(
                new DictionaryDTO.Option("half_day", "半天"),
                new DictionaryDTO.Option("one_day", "1天"),
                new DictionaryDTO.Option("two_days", "2天"),
                new DictionaryDTO.Option("three_days", "3天"),
                new DictionaryDTO.Option("more", "3天以上")
        );
    }

    /**
     * 会议规模选项
     */
    public List<DictionaryDTO.Option> getMeetingScaleOptions() {
        return Arrays.asList(
                new DictionaryDTO.Option("small", "50人以下"),
                new DictionaryDTO.Option("medium", "50-200人"),
                new DictionaryDTO.Option("large", "200-500人"),
                new DictionaryDTO.Option("xlarge", "500人以上")
        );
    }

    /**
     * 举办频率选项
     */
    public List<DictionaryDTO.Option> getFrequencyOptions() {
        return Arrays.asList(
                new DictionaryDTO.Option("once", "一次性"),
                new DictionaryDTO.Option("series", "系列活动"),
                new DictionaryDTO.Option("annual", "每年一届"),
                new DictionaryDTO.Option("irregular", "不定期")
        );
    }

    /**
     * 会议举办地域（省/市），常用省份+重点城市
     */
    public List<DictionaryDTO.RegionOption> getRegionOptions() {
        return Arrays.asList(
                new DictionaryDTO.RegionOption("110000", "北京市",
                        Collections.singletonList(new DictionaryDTO.CityOption("110100", "北京市"))),
                new DictionaryDTO.RegionOption("310000", "上海市",
                        Collections.singletonList(new DictionaryDTO.CityOption("310100", "上海市"))),
                new DictionaryDTO.RegionOption("440000", "广东省",
                        Arrays.asList(
                                new DictionaryDTO.CityOption("440100", "广州市"),
                                new DictionaryDTO.CityOption("440300", "深圳市"),
                                new DictionaryDTO.CityOption("441900", "东莞市")
                        )),
                new DictionaryDTO.RegionOption("330000", "浙江省",
                        Arrays.asList(
                                new DictionaryDTO.CityOption("330100", "杭州市"),
                                new DictionaryDTO.CityOption("330200", "宁波市")
                        )),
                new DictionaryDTO.RegionOption("320000", "江苏省",
                        Arrays.asList(
                                new DictionaryDTO.CityOption("320100", "南京市"),
                                new DictionaryDTO.CityOption("320500", "苏州市")
                        )),
                new DictionaryDTO.RegionOption("370000", "山东省",
                        Arrays.asList(
                                new DictionaryDTO.CityOption("370100", "济南市"),
                                new DictionaryDTO.CityOption("370200", "青岛市")
                        )),
                new DictionaryDTO.RegionOption("510000", "四川省",
                        Collections.singletonList(new DictionaryDTO.CityOption("510100", "成都市"))),
                new DictionaryDTO.RegionOption("420000", "湖北省",
                        Collections.singletonList(new DictionaryDTO.CityOption("420100", "武汉市")))
        );
    }

    /**
     * 目标人群选项
     */
    public List<DictionaryDTO.Option> getTargetAudienceOptions() {
        return Arrays.asList(
                new DictionaryDTO.Option("developer", "开发者"),
                new DictionaryDTO.Option("architect", "架构师"),
                new DictionaryDTO.Option("product_manager", "产品经理"),
                new DictionaryDTO.Option("cto", "技术管理者"),
                new DictionaryDTO.Option("student", "学生"),
                new DictionaryDTO.Option("general", "泛技术人群")
        );
    }

    /**
     * 开发者类型选项
     */
    public List<DictionaryDTO.Option> getDeveloperTypeOptions() {
        return Arrays.asList(
                new DictionaryDTO.Option("frontend", "前端开发"),
                new DictionaryDTO.Option("backend", "后端开发"),
                new DictionaryDTO.Option("fullstack", "全栈开发"),
                new DictionaryDTO.Option("mobile", "移动端开发"),
                new DictionaryDTO.Option("devops", "运维/DevOps"),
                new DictionaryDTO.Option("data", "大数据/AI"),
                new DictionaryDTO.Option("embedded", "嵌入式/物联网"),
                new DictionaryDTO.Option("game", "游戏开发"),
                new DictionaryDTO.Option("other", "其他")
        );
    }
}
