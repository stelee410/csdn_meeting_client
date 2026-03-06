package com.csdn.meeting.infrastructure.repository;

import com.csdn.meeting.infrastructure.po.MeetingFavoritePO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MeetingFavoriteJpaRepository extends JpaRepository<MeetingFavoritePO, Long> {

    Page<MeetingFavoritePO> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    boolean existsByUserIdAndMeetingId(Long userId, Long meetingId);

    Optional<MeetingFavoritePO> findByUserIdAndMeetingId(Long userId, Long meetingId);

    void deleteByUserIdAndMeetingId(Long userId, Long meetingId);
}
