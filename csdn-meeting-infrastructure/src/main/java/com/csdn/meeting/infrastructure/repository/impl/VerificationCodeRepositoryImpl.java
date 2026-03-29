package com.csdn.meeting.infrastructure.repository.impl;

import com.csdn.meeting.domain.entity.VerificationCode;
import com.csdn.meeting.domain.repository.VerificationCodeRepository;
import com.csdn.meeting.domain.valueobject.VerificationCodeScene;
import com.csdn.meeting.domain.valueobject.VerificationCodeType;
import com.csdn.meeting.infrastructure.mapper.VerificationCodeMapper;
import com.csdn.meeting.infrastructure.po.VerificationCodePO;
import com.csdn.meeting.infrastructure.repository.VerificationCodeBaseMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 验证码仓储实现（MyBatis-Plus版本）
 */
@Repository
public class VerificationCodeRepositoryImpl implements VerificationCodeRepository {

    private final VerificationCodeBaseMapper verificationCodeBaseMapper;

    public VerificationCodeRepositoryImpl(VerificationCodeBaseMapper verificationCodeBaseMapper) {
        this.verificationCodeBaseMapper = verificationCodeBaseMapper;
    }

    @Override
    public VerificationCode save(VerificationCode verificationCode) {
        VerificationCodePO po = VerificationCodeMapper.INSTANCE.toPO(verificationCode);
        if (po.getId() == null) {
            verificationCodeBaseMapper.insert(po);
        } else {
            verificationCodeBaseMapper.updateById(po);
        }
        return VerificationCodeMapper.INSTANCE.toEntity(po);
    }

    @Override
    public Optional<VerificationCode> findById(Long id) {
        VerificationCodePO po = verificationCodeBaseMapper.selectById(id);
        return po != null ? Optional.of(VerificationCodeMapper.INSTANCE.toEntity(po)) : Optional.empty();
    }

    @Override
    public Optional<VerificationCode> findLatestByTarget(String target, VerificationCodeType type, VerificationCodeScene scene) {
        VerificationCodePO po = verificationCodeBaseMapper.selectLatestByTarget(
                target, type.getCode(), scene.getCode());
        return po != null ? Optional.of(VerificationCodeMapper.INSTANCE.toEntity(po)) : Optional.empty();
    }

    @Override
    public Optional<VerificationCode> findLatestValidCode(String target, VerificationCodeType type, VerificationCodeScene scene) {
        VerificationCodePO po = verificationCodeBaseMapper.selectLatestValidCode(
                target, type.getCode(), scene.getCode());
        return po != null ? Optional.of(VerificationCodeMapper.INSTANCE.toEntity(po)) : Optional.empty();
    }

    @Override
    public int countTodayByTarget(String target, VerificationCodeType type, VerificationCodeScene scene) {
        return verificationCodeBaseMapper.countTodayByTarget(
                target, type.getCode(), scene.getCode());
    }

    @Override
    public void deleteExpiredCodes(LocalDateTime before) {
        verificationCodeBaseMapper.deleteExpiredCodes(before);
    }

    @Override
    public void deleteByTarget(String target) {
        // 使用MyBatis-Plus的条件构造器删除
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<VerificationCodePO> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.eq(VerificationCodePO::getTarget, target);
        verificationCodeBaseMapper.delete(wrapper);
    }
}
