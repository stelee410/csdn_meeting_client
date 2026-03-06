package com.csdn.meeting.infrastructure.repository.impl;

import com.csdn.meeting.domain.entity.MeetingFavorite;
import com.csdn.meeting.domain.repository.MeetingFavoriteRepository;
import com.csdn.meeting.domain.repository.PageResult;
import com.csdn.meeting.infrastructure.mapper.MeetingFavoriteMapper;
import com.csdn.meeting.infrastructure.po.MeetingFavoritePO;
import com.csdn.meeting.infrastructure.repository.MeetingFavoriteJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class MeetingFavoriteRepositoryImpl implements MeetingFavoriteRepository {

    private final MeetingFavoriteJpaRepository jpaRepository;

    public MeetingFavoriteRepositoryImpl(MeetingFavoriteJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public MeetingFavorite save(MeetingFavorite favorite) {
        MeetingFavoritePO po = MeetingFavoriteMapper.INSTANCE.toPO(favorite);
        MeetingFavoritePO saved = jpaRepository.save(po);
        return MeetingFavoriteMapper.INSTANCE.toEntity(saved);
    }

    @Override
    public Optional<MeetingFavorite> findById(Long id) {
        return jpaRepository.findById(id).map(MeetingFavoriteMapper.INSTANCE::toEntity);
    }

    @Override
    public PageResult<MeetingFavorite> findByUserIdOrderByCreatedAtDesc(Long userId, int page, int size) {
        Page<MeetingFavoritePO> springPage = jpaRepository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(page, size));
        List<MeetingFavorite> content = springPage.getContent().stream()
                .map(MeetingFavoriteMapper.INSTANCE::toEntity)
                .collect(Collectors.toList());
        return new PageResult<>(content, springPage.getTotalElements(), page, size);
    }

    @Override
    public boolean existsByUserIdAndMeetingId(Long userId, Long meetingId) {
        return jpaRepository.existsByUserIdAndMeetingId(userId, meetingId);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByUserIdAndMeetingId(Long userId, Long meetingId) {
        jpaRepository.deleteByUserIdAndMeetingId(userId, meetingId);
    }
}
