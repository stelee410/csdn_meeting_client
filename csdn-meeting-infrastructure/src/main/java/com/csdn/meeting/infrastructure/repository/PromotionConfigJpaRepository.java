package com.csdn.meeting.infrastructure.repository;

import com.csdn.meeting.infrastructure.po.PromotionConfigPO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PromotionConfigJpaRepository extends JpaRepository<PromotionConfigPO, Long> {

    Optional<PromotionConfigPO> findFirstByMeetingIdOrderByCreatedAtDesc(Long meetingId);
}
