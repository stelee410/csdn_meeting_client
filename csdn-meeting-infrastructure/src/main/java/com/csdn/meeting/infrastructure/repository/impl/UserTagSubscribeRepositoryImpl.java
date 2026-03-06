package com.csdn.meeting.infrastructure.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csdn.meeting.domain.entity.UserTagSubscribe;
import com.csdn.meeting.domain.repository.UserTagSubscribeRepository;
import com.csdn.meeting.infrastructure.converter.UserTagSubscribeConverter;
import com.csdn.meeting.infrastructure.po.UserTagSubscribePO;
import com.csdn.meeting.infrastructure.repository.mapper.UserTagSubscribeMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户标签订阅仓储实现
 */
@Repository
public class UserTagSubscribeRepositoryImpl implements UserTagSubscribeRepository {

    private final UserTagSubscribeMapper userTagSubscribeMapper;

    public UserTagSubscribeRepositoryImpl(UserTagSubscribeMapper userTagSubscribeMapper) {
        this.userTagSubscribeMapper = userTagSubscribeMapper;
    }

    @Override
    public Optional<UserTagSubscribe> findById(Long id) {
        UserTagSubscribePO po = userTagSubscribeMapper.selectById(id);
        return Optional.ofNullable(po).map(UserTagSubscribeConverter.INSTANCE::poToEntity);
    }

    @Override
    public Optional<UserTagSubscribe> findByUserIdAndTagId(Long userId, Long tagId) {
        UserTagSubscribePO po = userTagSubscribeMapper.selectByUserIdAndTagId(userId, tagId);
        return Optional.ofNullable(po).map(UserTagSubscribeConverter.INSTANCE::poToEntity);
    }

    @Override
    public List<UserTagSubscribe> findByUserId(Long userId) {
        LambdaQueryWrapper<UserTagSubscribePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserTagSubscribePO::getUserId, userId)
                .eq(UserTagSubscribePO::getIsDeleted, 0)
                .orderByDesc(UserTagSubscribePO::getCreateTime);
        List<UserTagSubscribePO> poList = userTagSubscribeMapper.selectList(wrapper);
        return UserTagSubscribeConverter.INSTANCE.poListToEntityList(poList);
    }

    @Override
    public List<UserTagSubscribe> findByUserId(Long userId, int page, int size) {
        Page<UserTagSubscribePO> pageParam = new Page<>(page, size);
        Page<UserTagSubscribePO> resultPage = userTagSubscribeMapper.selectPageByUserId(pageParam, userId);
        return UserTagSubscribeConverter.INSTANCE.poListToEntityList(resultPage.getRecords());
    }

    @Override
    public List<Long> findUserIdsByTagId(Long tagId) {
        return userTagSubscribeMapper.selectUserIdsByTagId(tagId);
    }

    @Override
    public List<Long> findUserIdsByTagIds(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return userTagSubscribeMapper.selectUserIdsByTagIds(tagIds);
    }

    @Override
    public UserTagSubscribe save(UserTagSubscribe subscribe) {
        UserTagSubscribePO po = UserTagSubscribeConverter.INSTANCE.entityToPo(subscribe);

        // 检查是否已存在记录
        UserTagSubscribePO existing = userTagSubscribeMapper.selectByUserIdAndTagId(
                subscribe.getUserId(), subscribe.getTagId());

        if (existing == null) {
            // 新增订阅
            userTagSubscribeMapper.insert(po);
        } else {
            // 已存在则恢复软删除
            if (existing.getIsDeleted() != null && existing.getIsDeleted() == 1) {
                existing.setIsDeleted(0);
                userTagSubscribeMapper.updateById(existing);
            }
            po = existing;
        }

        return UserTagSubscribeConverter.INSTANCE.poToEntity(po);
    }

    @Override
    public void unsubscribe(Long userId, Long tagId) {
        UserTagSubscribePO existing = userTagSubscribeMapper.selectByUserIdAndTagId(userId, tagId);
        if (existing != null) {
            userTagSubscribeMapper.deleteById(existing.getId());
        }
    }

    @Override
    public boolean exists(Long userId, Long tagId) {
        UserTagSubscribePO po = userTagSubscribeMapper.selectByUserIdAndTagId(userId, tagId);
        return po != null;
    }

    @Override
    public Set<Long> findSubscribedTagIdsByUserIdAndTagIds(Long userId, List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return java.util.Collections.emptySet();
        }
        List<Long> list = userTagSubscribeMapper.selectSubscribedTagIdsByUserIdAndTagIds(userId, tagIds);
        return list == null ? java.util.Collections.emptySet() : new java.util.LinkedHashSet<>(list);
    }

    @Override
    public long countByTagId(Long tagId) {
        Long count = userTagSubscribeMapper.countByTagId(tagId);
        return count != null ? count : 0L;
    }

    @Override
    public long countByUserId(Long userId) {
        Long count = userTagSubscribeMapper.countByUserId(userId);
        return count != null ? count : 0L;
    }
}
