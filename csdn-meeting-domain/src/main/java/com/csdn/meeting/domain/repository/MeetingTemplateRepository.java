package com.csdn.meeting.domain.repository;

import com.csdn.meeting.domain.entity.MeetingTemplate;

import java.util.List;
import java.util.Optional;

public interface MeetingTemplateRepository {

    MeetingTemplate save(MeetingTemplate template);

    Optional<MeetingTemplate> findById(Long id);

    List<MeetingTemplate> findAllActive();

    void deleteById(Long id);
}
