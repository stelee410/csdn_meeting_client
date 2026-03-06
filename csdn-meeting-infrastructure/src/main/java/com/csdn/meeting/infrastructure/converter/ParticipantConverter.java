package com.csdn.meeting.infrastructure.converter;

import com.csdn.meeting.domain.entity.Participant;
import com.csdn.meeting.infrastructure.po.ParticipantPO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 参与者转换器
 * 用于 ParticipantPO 和 Participant 实体之间的转换
 */
@Mapper
public interface ParticipantConverter {

    ParticipantConverter INSTANCE = Mappers.getMapper(ParticipantConverter.class);

    /**
     * PO 转 Entity
     */
    Participant poToEntity(ParticipantPO participantPO);

    /**
     * Entity 转 PO
     */
    ParticipantPO entityToPo(Participant participant);

    /**
     * PO列表转Entity列表
     */
    List<Participant> poListToEntityList(List<ParticipantPO> participantPOList);

    /**
     * Entity列表转PO列表
     */
    List<ParticipantPO> entityListToPoList(List<Participant> participantList);
}
