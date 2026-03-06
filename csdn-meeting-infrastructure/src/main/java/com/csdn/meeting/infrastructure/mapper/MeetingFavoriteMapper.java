package com.csdn.meeting.infrastructure.mapper;

import com.csdn.meeting.domain.entity.MeetingFavorite;
import com.csdn.meeting.infrastructure.po.MeetingFavoritePO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MeetingFavoriteMapper {

    MeetingFavoriteMapper INSTANCE = Mappers.getMapper(MeetingFavoriteMapper.class);

    MeetingFavorite toEntity(MeetingFavoritePO po);

    MeetingFavoritePO toPO(MeetingFavorite entity);
}
