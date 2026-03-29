package com.csdn.meeting.infrastructure.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csdn.meeting.domain.entity.User;
import com.csdn.meeting.domain.repository.PageResult;
import com.csdn.meeting.domain.repository.UserRepository;
import com.csdn.meeting.domain.valueobject.UserStatus;
import com.csdn.meeting.domain.valueobject.UserType;
import com.csdn.meeting.infrastructure.mapper.UserMapper;
import com.csdn.meeting.infrastructure.po.UserPO;
import com.csdn.meeting.infrastructure.repository.UserBaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户仓储实现（MyBatis-Plus版本）
 */
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserBaseMapper userBaseMapper;

    public UserRepositoryImpl(UserBaseMapper userBaseMapper) {
        this.userBaseMapper = userBaseMapper;
    }

    @Override
    public User save(User user) {
        UserPO po = UserMapper.INSTANCE.toPO(user);
        if (po.getId() == null) {
            userBaseMapper.insert(po);
        } else {
            userBaseMapper.updateById(po);
        }
        return UserMapper.INSTANCE.toEntity(po);
    }

    @Override
    public Optional<User> findById(Long id) {
        UserPO po = userBaseMapper.selectById(id);
        return po != null ? Optional.of(UserMapper.INSTANCE.toEntity(po)) : Optional.empty();
    }

    @Override
    public Optional<User> findByUserId(String userId) {
        UserPO po = userBaseMapper.selectByUserId(userId);
        return po != null ? Optional.of(UserMapper.INSTANCE.toEntity(po)) : Optional.empty();
    }

    @Override
    public Optional<User> findByMobile(String mobile) {
        UserPO po = userBaseMapper.selectByMobile(mobile);
        return po != null ? Optional.of(UserMapper.INSTANCE.toEntity(po)) : Optional.empty();
    }

    @Override
    public Optional<User> findByCsdnBindId(String csdnBindId) {
        UserPO po = userBaseMapper.selectByCsdnBindId(csdnBindId);
        return po != null ? Optional.of(UserMapper.INSTANCE.toEntity(po)) : Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        UserPO po = userBaseMapper.selectByEmail(email);
        return po != null ? Optional.of(UserMapper.INSTANCE.toEntity(po)) : Optional.empty();
    }

    @Override
    public PageResult<User> findByUserType(UserType userType, int page, int size) {
        Page<UserPO> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<UserPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPO::getUserType, userType.getCode());
        IPage<UserPO> resultPage = userBaseMapper.selectPage(pageParam, wrapper);

        List<User> content = resultPage.getRecords().stream()
                .map(UserMapper.INSTANCE::toEntity)
                .collect(Collectors.toList());
        return new PageResult<>(content, resultPage.getTotal(), page, size);
    }

    @Override
    public List<User> findByStatus(UserStatus status) {
        LambdaQueryWrapper<UserPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPO::getStatus, status.getCode());
        return userBaseMapper.selectList(wrapper).stream()
                .map(UserMapper.INSTANCE::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<User> findAll(int page, int size) {
        Page<UserPO> pageParam = new Page<>(page, size);
        IPage<UserPO> resultPage = userBaseMapper.selectPage(pageParam, null);

        List<User> content = resultPage.getRecords().stream()
                .map(UserMapper.INSTANCE::toEntity)
                .collect(Collectors.toList());
        return new PageResult<>(content, resultPage.getTotal(), page, size);
    }

    @Override
    public PageResult<User> findByUserTypeAndStatus(UserType userType, UserStatus status, int page, int size) {
        Page<UserPO> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<UserPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPO::getUserType, userType.getCode())
                .eq(UserPO::getStatus, status.getCode());
        IPage<UserPO> resultPage = userBaseMapper.selectPage(pageParam, wrapper);

        List<User> content = resultPage.getRecords().stream()
                .map(UserMapper.INSTANCE::toEntity)
                .collect(Collectors.toList());
        return new PageResult<>(content, resultPage.getTotal(), page, size);
    }

    @Override
    public void delete(User user) {
        if (user.getId() != null) {
            userBaseMapper.deleteById(user.getId());
        }
    }

    @Override
    public void deleteById(Long id) {
        userBaseMapper.deleteById(id);
    }
}
