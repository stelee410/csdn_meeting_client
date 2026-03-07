package com.csdn.meeting.infrastructure.converter;

import com.csdn.meeting.domain.entity.UserTagSubscribe;
import com.csdn.meeting.infrastructure.po.UserTagSubscribePO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 用户标签订阅转换器
 * 用于 UserTagSubscribePO 和 UserTagSubscribe 实体之间的转换
 */
@Mapper
public interface UserTagSubscribeConverter {

    UserTagSubscribeConverter INSTANCE = Mappers.getMapper(UserTagSubscribeConverter.class);

    /**
     * PO 转 Entity
     */
    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "userId", target = "userId"),
            @Mapping(source = "tagId", target = "tagId"),
            @Mapping(source = "isDeleted", target = "isDeleted")
    })
    UserTagSubscribe poToEntity(UserTagSubscribePO userTagSubscribePO);

    /**
     * Entity 转 PO
     */
    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "userId", target = "userId"),
            @Mapping(source = "tagId", target = "tagId"),
            @Mapping(source = "isDeleted", target = "isDeleted")
    })
    UserTagSubscribePO entityToPo(UserTagSubscribe userTagSubscribe);

    /**
     * PO列表转Entity列表
     */
    List<UserTagSubscribe> poListToEntityList(List<UserTagSubscribePO> userTagSubscribePOList);

    /**
     * Entity列表转PO列表
     */
    List<UserTagSubscribePO> entityListToPoList(List<UserTagSubscribe> userTagSubscribeList);
}
