package com.csdn.meeting.infrastructure.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csdn.meeting.domain.entity.MeetingFavorite;
import com.csdn.meeting.domain.repository.MeetingFavoriteRepository;
import com.csdn.meeting.domain.repository.PageResult;
import com.csdn.meeting.infrastructure.mapper.MeetingFavoriteMapper;
import com.csdn.meeting.infrastructure.po.MeetingFavoritePO;
import com.csdn.meeting.infrastructure.repository.mapper.MeetingFavoritePOMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class MeetingFavoriteRepositoryImpl implements MeetingFavoriteRepository {

    private final MeetingFavoritePOMapper favoritePOMapper;

    public MeetingFavoriteRepositoryImpl(MeetingFavoritePOMapper favoritePOMapper) {
        this.favoritePOMapper = favoritePOMapper;
    }

    @Override
    public MeetingFavorite save(MeetingFavorite favorite) {
        MeetingFavoritePO po = MeetingFavoriteMapper.INSTANCE.toPO(favorite);
        if (po.getId() == null) {
            favoritePOMapper.insert(po);
        } else {
            favoritePOMapper.updateById(po);
        }
        return MeetingFavoriteMapper.INSTANCE.toEntity(po);
    }

    @Override
    public Optional<MeetingFavorite> findById(Long id) {
        MeetingFavoritePO po = favoritePOMapper.selectById(id);
        return po == null ? Optional.empty() : Optional.of(MeetingFavoriteMapper.INSTANCE.toEntity(po));
    }

    @Override
    public PageResult<MeetingFavorite> findByUserIdOrderByCreatedAtDesc(Long userId, int page, int size) {
        Page<MeetingFavoritePO> pageParam = new Page<>(page + 1, size);
        IPage<MeetingFavoritePO> springPage = favoritePOMapper.selectPageByUserIdOrderByCreatedAtDesc(pageParam, userId);
        List<MeetingFavorite> content = springPage.getRecords().stream()
                .map(MeetingFavoriteMapper.INSTANCE::toEntity)
                .collect(Collectors.toList());
        return new PageResult<>(content, springPage.getTotal(), page, size);
    }

    @Override
    public boolean existsByUserIdAndMeetingId(Long userId, Long meetingId) {
        LambdaQueryWrapper<MeetingFavoritePO> qw = new LambdaQueryWrapper<>();
        qw.eq(MeetingFavoritePO::getUserId, userId).eq(MeetingFavoritePO::getMeetingId, meetingId);
        return favoritePOMapper.selectCount(qw) > 0;
    }

    @Override
    public void deleteById(Long id) {
        favoritePOMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByUserIdAndMeetingId(Long userId, Long meetingId) {
        favoritePOMapper.deleteByUserIdAndMeetingId(userId, meetingId);
    }
}
