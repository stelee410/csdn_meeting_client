package com.csdn.meeting.infrastructure.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csdn.meeting.domain.entity.UserTagSubscribe;
import com.csdn.meeting.domain.repository.UserTagSubscribeRepository;
import com.csdn.meeting.infrastructure.converter.UserTagSubscribeConverter;
import com.csdn.meeting.infrastructure.po.UserTagSubscribePO;
import com.csdn.meeting.infrastructure.repository.mapper.UserTagSubscribeMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    public Optional<UserTagSubscribe> findByUserIdAndTagId(String userId, Long tagId) {
        UserTagSubscribePO po = userTagSubscribeMapper.selectByUserIdAndTagId(userId, tagId);
        return Optional.ofNullable(po).map(UserTagSubscribeConverter.INSTANCE::poToEntity);
    }

    @Override
    public List<UserTagSubscribe> findByUserId(String userId) {
        LambdaQueryWrapper<UserTagSubscribePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserTagSubscribePO::getUserId, userId)
                .eq(UserTagSubscribePO::getIsDeleted, 0)
                .orderByDesc(UserTagSubscribePO::getCreatedAt);
        List<UserTagSubscribePO> poList = userTagSubscribeMapper.selectList(wrapper);
        return UserTagSubscribeConverter.INSTANCE.poListToEntityList(poList);
    }

    @Override
    public List<UserTagSubscribe> findByUserId(String userId, int page, int size) {
        Page<UserTagSubscribePO> pageParam = new Page<>(page, size);
        Page<UserTagSubscribePO> resultPage = userTagSubscribeMapper.selectPageByUserId(pageParam, userId);
        return UserTagSubscribeConverter.INSTANCE.poListToEntityList(resultPage.getRecords());
    }

    @Override
    public List<String> findUserIdsByTagId(Long tagId) {
        return userTagSubscribeMapper.selectUserIdsByTagId(tagId);
    }

    @Override
    public List<String> findUserIdsByTagIds(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return userTagSubscribeMapper.selectUserIdsByTagIds(tagIds);
    }

    @Override
    public UserTagSubscribe save(UserTagSubscribe subscribe) {
        UserTagSubscribePO po = UserTagSubscribeConverter.INSTANCE.entityToPo(subscribe);

        // 检查是否已存在记录
        UserTagSubscribePO existing = userTagSubscribeMapper.selectAllByUserIdAndTagId(
                subscribe.getUserId(), subscribe.getTagId());

        if (existing == null) {
            // 新增订阅
            userTagSubscribeMapper.insert(po);
        } else {
            // 已存在则恢复软删除（不能用 updateById，否则 TableLogic 会在 WHERE 中加 is_deleted=0 导致更新不到已删记录）
            if (existing.getIsDeleted() != null && existing.getIsDeleted() == 1) {
                userTagSubscribeMapper.restoreById(existing.getId());
            }
            po = existing;
        }

        return UserTagSubscribeConverter.INSTANCE.poToEntity(po);
    }

    @Override
    public void unsubscribe(String userId, Long tagId) {
        UserTagSubscribePO existing = userTagSubscribeMapper.selectByUserIdAndTagId(userId, tagId);
        if (existing != null) {
            userTagSubscribeMapper.deleteById(existing.getId());
        }
    }

    @Override
    public boolean exists(String userId, Long tagId) {
        UserTagSubscribePO po = userTagSubscribeMapper.selectByUserIdAndTagId(userId, tagId);
        return po != null;
    }

    @Override
    public Set<Long> findSubscribedTagIdsByUserIdAndTagIds(String userId, List<Long> tagIds) {
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
    public long countByUserId(String userId) {
        Long count = userTagSubscribeMapper.countByUserId(userId);
        return count != null ? count : 0L;
    }
}
