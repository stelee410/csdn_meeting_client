package com.csdn.meeting.infrastructure.repository;

import com.csdn.meeting.infrastructure.po.MeetingPO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MeetingJpaRepository extends JpaRepository<MeetingPO, Long> {

    List<MeetingPO> findByCreatorId(Long creatorId);

    Page<MeetingPO> findByCreatorIdOrderByStartTimeDesc(Long creatorId, Pageable pageable);

    Page<MeetingPO> findByCreatorIdAndStatusInOrderByStartTimeDesc(Long creatorId, List<Integer> statuses, Pageable pageable);

    Page<MeetingPO> findByCreatorIdAndStartTimeBetweenOrderByStartTimeDesc(Long creatorId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<MeetingPO> findByCreatorIdAndStatusInAndStartTimeBetweenOrderByStartTimeDesc(Long creatorId, List<Integer> statuses, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<MeetingPO> findByCreatorIdAndStatus(Long creatorId, Integer status);

    List<MeetingPO> findByCreatorIdAndStartTimeBetween(Long creatorId, LocalDateTime start, LocalDateTime end);

    List<MeetingPO> findByStatus(Integer status);

    List<MeetingPO> findByStatusAndStartTimeLessThanEqual(Integer status, LocalDateTime threshold);

    List<MeetingPO> findByStatusAndEndTimeLessThanEqual(Integer status, LocalDateTime threshold);
}
