package com.csdn.meeting.infrastructure.mapper;

import com.csdn.meeting.domain.entity.User;
import com.csdn.meeting.domain.valueobject.Industry;
import com.csdn.meeting.domain.valueobject.UserStatus;
import com.csdn.meeting.domain.valueobject.UserType;
import com.csdn.meeting.infrastructure.po.UserPO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "userType", source = "userType", qualifiedByName = "userTypeToInt")
    @Mapping(target = "status", source = "status", qualifiedByName = "statusToInt")
    @Mapping(target = "industry", source = "industry", qualifiedByName = "industryToString")
    @Mapping(target = "agreementAccepted", source = "agreementAccepted")
    @Mapping(target = "privacyAccepted", source = "privacyAccepted")
    @Mapping(target = "emailVerified", source = "emailVerified")
    UserPO toPO(User user);

    @Mapping(target = "userType", source = "userType", qualifiedByName = "intToUserType")
    @Mapping(target = "status", source = "status", qualifiedByName = "intToStatus")
    @Mapping(target = "industry", source = "industry", qualifiedByName = "stringToIndustry")
    User toEntity(UserPO po);

    @Named("userTypeToInt")
    default Integer userTypeToInt(UserType userType) {
        return userType == null ? null : userType.getCode();
    }

    @Named("intToUserType")
    default UserType intToUserType(Integer code) {
        return UserType.of(code);
    }

    @Named("statusToInt")
    default Integer statusToInt(UserStatus status) {
        return status == null ? null : status.getCode();
    }

    @Named("intToStatus")
    default UserStatus intToStatus(Integer code) {
        return UserStatus.of(code);
    }

    @Named("industryToString")
    default String industryToString(Industry industry) {
        return industry == null ? null : industry.getValue();
    }

    @Named("stringToIndustry")
    default Industry stringToIndustry(String value) {
        return Industry.of(value);
    }
}
