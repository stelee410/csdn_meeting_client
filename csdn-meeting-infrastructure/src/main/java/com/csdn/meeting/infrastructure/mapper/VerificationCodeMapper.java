package com.csdn.meeting.infrastructure.mapper;

import com.csdn.meeting.domain.entity.VerificationCode;
import com.csdn.meeting.domain.valueobject.VerificationCodeScene;
import com.csdn.meeting.domain.valueobject.VerificationCodeType;
import com.csdn.meeting.infrastructure.po.VerificationCodePO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface VerificationCodeMapper {

    VerificationCodeMapper INSTANCE = Mappers.getMapper(VerificationCodeMapper.class);

    @Mapping(target = "type", source = "type", qualifiedByName = "typeToInt")
    @Mapping(target = "scene", source = "scene", qualifiedByName = "sceneToInt")
    @Mapping(target = "used", source = "used")
    VerificationCodePO toPO(VerificationCode verificationCode);

    @Mapping(target = "type", source = "type", qualifiedByName = "intToType")
    @Mapping(target = "scene", source = "scene", qualifiedByName = "intToScene")
    VerificationCode toEntity(VerificationCodePO po);

    @Named("typeToInt")
    default Integer typeToInt(VerificationCodeType type) {
        return type == null ? null : type.getCode();
    }

    @Named("intToType")
    default VerificationCodeType intToType(Integer code) {
        return VerificationCodeType.of(code);
    }

    @Named("sceneToInt")
    default Integer sceneToInt(VerificationCodeScene scene) {
        return scene == null ? null : scene.getCode();
    }

    @Named("intToScene")
    default VerificationCodeScene intToScene(Integer code) {
        return VerificationCodeScene.of(code);
    }
}
