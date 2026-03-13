package com.csdn.meeting.application.service;

import com.csdn.meeting.application.dto.CreateMeetingCommand;
import com.csdn.meeting.application.dto.MeetingDTO;
import com.csdn.meeting.application.dto.MeetingTemplateDTO;
import com.csdn.meeting.domain.entity.MeetingTemplate;
import com.csdn.meeting.domain.repository.MeetingTemplateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 活动模板用例：列表、详情、管理 CRUD；应用模板创建草稿
 */
@Service
public class MeetingTemplateUseCase {

    private final MeetingTemplateRepository templateRepository;
    private final MeetingApplicationService meetingApplicationService;

    public MeetingTemplateUseCase(MeetingTemplateRepository templateRepository,
                                 MeetingApplicationService meetingApplicationService) {
        this.templateRepository = templateRepository;
        this.meetingApplicationService = meetingApplicationService;
    }

    /**
     * 获取所有启用的模板（领域实体）
     */
    public List<MeetingTemplate> getAllActive() {
        return templateRepository.findAllActive();
    }

    /**
     * 从模板创建草稿：复制模板字段到新会议，状态为 DRAFT
     */
    @Transactional
    public MeetingDTO applyTemplate(Long templateId, String creatorId, String creatorName) {
        MeetingTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> new IllegalArgumentException("模板不存在: " + templateId));
        CreateMeetingCommand command = new CreateMeetingCommand();
        command.setTitle(template.getName() + " - 新会议");
        command.setDescription(template.getDescriptionTemplate());
        command.setCreatorId(creatorId);
        command.setCreatorName(creatorName);
        command.setScene(template.getScene());
        command.setTags(template.getDefaultTags());
        command.setTargetAudience(template.getTargetAudience());
        command.setScheduleDays(null);
        return meetingApplicationService.createDraft(command);
    }

    public List<MeetingTemplateDTO> listActive() {
        return templateRepository.findAllActive().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public MeetingTemplateDTO getById(Long id) {
        return templateRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("模板不存在: " + id));
    }

    public MeetingTemplateDTO create(MeetingTemplateDTO dto) {
        MeetingTemplate entity = toEntity(dto);
        entity.setIsActive(Boolean.TRUE.equals(dto.getIsActive()));
        MeetingTemplate saved = templateRepository.save(entity);
        return toDTO(saved);
    }

    public MeetingTemplateDTO update(Long id, MeetingTemplateDTO dto) {
        MeetingTemplate entity = templateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("模板不存在: " + id));
        applyUpdate(entity, dto);
        MeetingTemplate saved = templateRepository.save(entity);
        return toDTO(saved);
    }

    public void delete(Long id) {
        if (!templateRepository.findById(id).isPresent()) {
            throw new IllegalArgumentException("模板不存在: " + id);
        }
        templateRepository.deleteById(id);
    }

    /**
     * 模板下架（设置 isActive=false），不影响其他模板列表
     */
    public MeetingTemplateDTO offline(Long id) {
        MeetingTemplate entity = templateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("模板不存在: " + id));
        entity.setIsActive(false);
        return toDTO(templateRepository.save(entity));
    }

    /**
     * 模板上架（设置 isActive=true）
     */
    public MeetingTemplateDTO shelve(Long id) {
        MeetingTemplate entity = templateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("模板不存在: " + id));
        entity.setIsActive(true);
        return toDTO(templateRepository.save(entity));
    }

    private void applyUpdate(MeetingTemplate entity, MeetingTemplateDTO dto) {
        if (dto.getName() != null) entity.setName(dto.getName());
        if (dto.getScene() != null) entity.setScene(dto.getScene());
        if (dto.getDescriptionTemplate() != null) entity.setDescriptionTemplate(dto.getDescriptionTemplate());
        if (dto.getDefaultTags() != null) entity.setDefaultTags(dto.getDefaultTags());
        if (dto.getTargetAudience() != null) entity.setTargetAudience(dto.getTargetAudience());
        if (dto.getMeetingDuration() != null) entity.setMeetingDuration(dto.getMeetingDuration());
        if (dto.getMeetingScale() != null) entity.setMeetingScale(dto.getMeetingScale());
        if (dto.getFrequency() != null) entity.setFrequency(dto.getFrequency());
        if (dto.getSortOrder() != null) entity.setSortOrder(dto.getSortOrder());
        if (dto.getIsActive() != null) entity.setIsActive(dto.getIsActive());
    }

    private MeetingTemplate toEntity(MeetingTemplateDTO dto) {
        MeetingTemplate e = new MeetingTemplate();
        e.setName(dto.getName());
        e.setScene(dto.getScene());
        e.setDescriptionTemplate(dto.getDescriptionTemplate());
        e.setDefaultTags(dto.getDefaultTags());
        e.setTargetAudience(dto.getTargetAudience());
        e.setMeetingDuration(dto.getMeetingDuration());
        e.setMeetingScale(dto.getMeetingScale());
        e.setFrequency(dto.getFrequency());
        e.setSortOrder(dto.getSortOrder());
        e.setIsActive(dto.getIsActive());
        return e;
    }

    private MeetingTemplateDTO toDTO(MeetingTemplate e) {
        MeetingTemplateDTO dto = new MeetingTemplateDTO();
        dto.setId(e.getId());
        dto.setName(e.getName());
        dto.setScene(e.getScene());
        dto.setDescriptionTemplate(e.getDescriptionTemplate());
        dto.setDefaultTags(e.getDefaultTags());
        dto.setTargetAudience(e.getTargetAudience());
        dto.setMeetingDuration(e.getMeetingDuration());
        dto.setMeetingScale(e.getMeetingScale());
        dto.setFrequency(e.getFrequency());
        dto.setSortOrder(e.getSortOrder());
        dto.setIsActive(e.getIsActive());
        return dto;
    }
}
