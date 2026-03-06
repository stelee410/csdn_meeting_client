package com.csdn.meeting.domain.repository;

import com.csdn.meeting.domain.entity.PromotionConfig;

import java.util.Optional;

public interface PromotionConfigRepository {

    PromotionConfig save(PromotionConfig config);

    Optional<PromotionConfig> findById(Long id);

    Optional<PromotionConfig> findByMeetingId(Long meetingId);
}
