package com.csdn.meeting.infrastructure.mapper;

import com.csdn.meeting.domain.entity.Registration;
import com.csdn.meeting.infrastructure.po.RegistrationPO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RegistrationMapper {

    RegistrationMapper INSTANCE = Mappers.getMapper(RegistrationMapper.class);

    @Mapping(target = "status", source = "status", qualifiedByName = "stringToStatus")
    Registration toEntity(RegistrationPO po);

    @Mapping(target = "status", source = "status", qualifiedByName = "statusToString")
    RegistrationPO toPO(Registration entity);

    @Named("stringToStatus")
    default Registration.RegistrationStatus stringToStatus(String s) {
        if (s == null || s.isEmpty()) return null;
        try {
            return Registration.RegistrationStatus.valueOf(s);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Named("statusToString")
    default String statusToString(Registration.RegistrationStatus status) {
        return status == null ? null : status.name();
    }
}
