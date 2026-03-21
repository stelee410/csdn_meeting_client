package com.csdn.meeting.infrastructure.repository.impl;

import com.csdn.meeting.domain.entity.MeetingTemplate;
import com.csdn.meeting.domain.repository.MeetingTemplateRepository;
import com.csdn.meeting.infrastructure.mapper.MeetingTemplateMapper;
import com.csdn.meeting.infrastructure.po.MeetingTemplatePO;
import com.csdn.meeting.infrastructure.repository.mapper.MeetingTemplatePOMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class MeetingTemplateRepositoryImpl implements MeetingTemplateRepository {

    private final MeetingTemplatePOMapper templatePOMapper;

    public MeetingTemplateRepositoryImpl(MeetingTemplatePOMapper templatePOMapper) {
        this.templatePOMapper = templatePOMapper;
    }

    @Override
    public MeetingTemplate save(MeetingTemplate template) {
        MeetingTemplatePO po = MeetingTemplateMapper.INSTANCE.toPO(template);
        if (po.getId() == null) {
            templatePOMapper.insert(po);
        } else {
            templatePOMapper.updateById(po);
        }
        return MeetingTemplateMapper.INSTANCE.toEntity(po);
    }

    @Override
    public Optional<MeetingTemplate> findById(Long id) {
        MeetingTemplatePO po = templatePOMapper.selectById(id);
        return po == null ? Optional.empty() : Optional.of(MeetingTemplateMapper.INSTANCE.toEntity(po));
    }

    @Override
    public List<MeetingTemplate> findAllActive() {
        return templatePOMapper.selectListedOrderBySortWeightDesc().stream()
                .map(MeetingTemplateMapper.INSTANCE::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        templatePOMapper.deleteById(id);
    }
}
