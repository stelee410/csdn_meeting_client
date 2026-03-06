package com.csdn.meeting.infrastructure.repository;

import com.csdn.meeting.infrastructure.po.RegistrationPO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RegistrationJpaRepository extends JpaRepository<RegistrationPO, Long> {

    Page<RegistrationPO> findByMeetingIdAndStatus(Long meetingId, String status, Pageable pageable);

    long countByMeetingIdAndStatus(Long meetingId, String status);

    Page<RegistrationPO> findByMeetingId(Long meetingId, Pageable pageable);

    long countByMeetingId(Long meetingId);

    Optional<RegistrationPO> findByUserIdAndMeetingId(Long userId, Long meetingId);

    @Query("SELECT r FROM RegistrationPO r INNER JOIN MeetingPO m ON r.meetingId = m.id " +
            "WHERE r.userId = :userId AND m.status IN :statusCodes ORDER BY m.startTime DESC")
    Page<RegistrationPO> findByUserIdAndMeetingStatusIn(@Param("userId") Long userId,
                                                        @Param("statusCodes") List<Integer> statusCodes,
                                                        Pageable pageable);
}
