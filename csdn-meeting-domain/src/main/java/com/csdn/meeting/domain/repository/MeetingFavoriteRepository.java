package com.csdn.meeting.domain.repository;

import com.csdn.meeting.domain.entity.MeetingFavorite;

import java.util.Optional;

public interface MeetingFavoriteRepository {

    MeetingFavorite save(MeetingFavorite favorite);

    Optional<MeetingFavorite> findById(Long id);

    /**
     * 按 userId 分页查询，按 createdAt 倒序
     */
    PageResult<MeetingFavorite> findByUserIdOrderByCreatedAtDesc(String userId, int page, int size);

    boolean existsByUserIdAndMeetingId(String userId, Long meetingId);

    void deleteById(Long id);

    void deleteByUserIdAndMeetingId(String userId, Long meetingId);
}
