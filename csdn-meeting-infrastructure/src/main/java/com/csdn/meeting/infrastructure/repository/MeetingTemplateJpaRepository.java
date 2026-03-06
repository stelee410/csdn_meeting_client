package com.csdn.meeting.infrastructure.repository;

import com.csdn.meeting.infrastructure.po.MeetingTemplatePO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeetingTemplateJpaRepository extends JpaRepository<MeetingTemplatePO, Long> {

    List<MeetingTemplatePO> findByIsActiveTrueOrderBySortOrderAsc();
}
