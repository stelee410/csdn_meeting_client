package com.csdn.meeting.application.service;

import com.csdn.meeting.application.dto.DictionaryDTO;
import com.csdn.meeting.infrastructure.po.DictionaryPO;
import com.csdn.meeting.infrastructure.repository.mapper.DictionaryPOMapper;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 字典/下拉选项 UseCase
 * V14 起：flat 类型选项从 t_dictionary 表读取，地域仍保持静态（层级结构较复杂）。
 */
@Service
public class DictionaryUseCase {

    private final DictionaryPOMapper dictionaryPOMapper;

    public DictionaryUseCase(DictionaryPOMapper dictionaryPOMapper) {
        this.dictionaryPOMapper = dictionaryPOMapper;
    }

    public DictionaryDTO getCreateMeetingDictionaries() {
        DictionaryDTO dto = new DictionaryDTO();
        dto.setMeetingDurations(loadOptions("meeting_duration"));
        dto.setMeetingScales(loadOptions("meeting_scale"));
        dto.setFrequencies(loadOptions("meeting_frequency"));
        dto.setRegions(getRegionOptions());
        dto.setTargetAudiences(loadOptions("target_audience"));
        dto.setDeveloperTypes(loadOptions("developer_type"));
        dto.setOrganizers(loadOptions("organizer"));
        return dto;
    }

    /**
     * 从 t_dictionary 按分类读取启用项并转换为 Option 列表。
     */
    private List<DictionaryDTO.Option> loadOptions(String dictType) {
        List<DictionaryPO> items = dictionaryPOMapper.selectActiveByType(dictType);
        return items.stream()
                .map(po -> new DictionaryDTO.Option(po.getItemCode(), po.getItemLabel()))
                .collect(Collectors.toList());
    }

    /**
     * 会议举办地域（省/市），层级结构保持静态配置。
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
}
