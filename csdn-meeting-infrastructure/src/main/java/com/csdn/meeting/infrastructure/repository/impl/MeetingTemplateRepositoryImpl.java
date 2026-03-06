package com.csdn.meeting.infrastructure.repository.impl;

import com.csdn.meeting.domain.entity.MeetingTemplate;
import com.csdn.meeting.domain.repository.MeetingTemplateRepository;
import com.csdn.meeting.infrastructure.mapper.MeetingTemplateMapper;
import com.csdn.meeting.infrastructure.po.MeetingTemplatePO;
import com.csdn.meeting.infrastructure.repository.MeetingTemplateJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class MeetingTemplateRepositoryImpl implements MeetingTemplateRepository {

    private final MeetingTemplateJpaRepository jpaRepository;

    public MeetingTemplateRepositoryImpl(MeetingTemplateJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public MeetingTemplate save(MeetingTemplate template) {
        MeetingTemplatePO po = MeetingTemplateMapper.INSTANCE.toPO(template);
        MeetingTemplatePO saved = jpaRepository.save(po);
        return MeetingTemplateMapper.INSTANCE.toEntity(saved);
    }

    @Override
    public Optional<MeetingTemplate> findById(Long id) {
        return jpaRepository.findById(id).map(MeetingTemplateMapper.INSTANCE::toEntity);
    }

    @Override
    public List<MeetingTemplate> findAllActive() {
        return jpaRepository.findByIsActiveTrueOrderBySortOrderAsc().stream()
                .map(MeetingTemplateMapper.INSTANCE::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
