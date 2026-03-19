package com.csdn.meeting.infrastructure.mapper.analytics;

import com.csdn.meeting.domain.entity.analytics.TrackEvent;
import com.csdn.meeting.infrastructure.po.analytics.TrackEventPO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import com.alibaba.fastjson2.JSON;
import java.util.Map;

/**
 * MapStruct 转换器：TrackEvent 与 TrackEventPO 之间的转换
 */
@Mapper
public interface TrackEventMapper {

    TrackEventMapper INSTANCE = Mappers.getMapper(TrackEventMapper.class);

    @Mapping(target = "properties", source = "properties", qualifiedByName = "mapToJsonString")
    TrackEventPO toPO(TrackEvent event);

    @Mapping(target = "properties", source = "properties", qualifiedByName = "jsonStringToMap")
    TrackEvent toEntity(TrackEventPO po);

    @Named("mapToJsonString")
    default String mapToJsonString(Map<String, Object> properties) {
        if (properties == null) {
            return null;
        }
        return JSON.toJSONString(properties);
    }

    @Named("jsonStringToMap")
    default Map<String, Object> jsonStringToMap(String properties) {
        if (properties == null || properties.isEmpty()) {
            return null;
        }
        return JSON.parseObject(properties, Map.class);
    }
}
