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
     * 获取所有已上架的模板（领域实体）
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
        command.setTitle(template.getDefaultTitlePrefix() != null
                ? template.getDefaultTitlePrefix() + " - 新会议"
                : template.getName() + " - 新会议");
        command.setDescription(template.getDefaultIntro());
        command.setCreatorId(creatorId);
        command.setCreatorName(creatorName);
        command.setScene(template.getDefaultScene());
        command.setTags(template.getDefaultTags());
        command.setTargetAudience(template.getDefaultAudience());
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
        if (entity.getStatus() == null) {
            entity.setStatus(0);
        }
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

    /** 模板下架（status → UNLISTED=1） */
    public MeetingTemplateDTO offline(Long id) {
        MeetingTemplate entity = templateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("模板不存在: " + id));
        entity.setStatus(1);
        return toDTO(templateRepository.save(entity));
    }

    /** 模板上架（status → LISTED=2） */
    public MeetingTemplateDTO shelve(Long id) {
        MeetingTemplate entity = templateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("模板不存在: " + id));
        entity.setStatus(2);
        return toDTO(templateRepository.save(entity));
    }

    private void applyUpdate(MeetingTemplate entity, MeetingTemplateDTO dto) {
        if (dto.getName() != null) entity.setName(dto.getName());
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
        if (dto.getIconEmoji() != null) entity.setIconEmoji(dto.getIconEmoji());
        if (dto.getSortWeight() != null) entity.setSortWeight(dto.getSortWeight());
        if (dto.getStatus() != null) entity.setStatus(dto.getStatus());
        if (dto.getDefaultMeetingType() != null) entity.setDefaultMeetingType(dto.getDefaultMeetingType());
        if (dto.getDefaultForm() != null) entity.setDefaultForm(dto.getDefaultForm());
        if (dto.getDefaultScene() != null) entity.setDefaultScene(dto.getDefaultScene());
        if (dto.getDefaultScale() != null) entity.setDefaultScale(dto.getDefaultScale());
        if (dto.getDefaultDuration() != null) entity.setDefaultDuration(dto.getDefaultDuration());
        if (dto.getDefaultRecurrence() != null) entity.setDefaultRecurrence(dto.getDefaultRecurrence());
        if (dto.getDefaultTitlePrefix() != null) entity.setDefaultTitlePrefix(dto.getDefaultTitlePrefix());
        if (dto.getDefaultHostCompany() != null) entity.setDefaultHostCompany(dto.getDefaultHostCompany());
        if (dto.getDefaultDepartment() != null) entity.setDefaultDepartment(dto.getDefaultDepartment());
        if (dto.getDefaultContact() != null) entity.setDefaultContact(dto.getDefaultContact());
        if (dto.getDefaultContactTitle() != null) entity.setDefaultContactTitle(dto.getDefaultContactTitle());
        if (dto.getDefaultContactPhone() != null) entity.setDefaultContactPhone(dto.getDefaultContactPhone());
        if (dto.getDefaultIntro() != null) entity.setDefaultIntro(dto.getDefaultIntro());
        if (dto.getCoverUrl() != null) entity.setCoverUrl(dto.getCoverUrl());
        if (dto.getDefaultAudience() != null) entity.setDefaultAudience(dto.getDefaultAudience());
        if (dto.getDefaultTags() != null) entity.setDefaultTags(dto.getDefaultTags());
        if (dto.getDefaultTopicSkeleton() != null) entity.setDefaultTopicSkeleton(dto.getDefaultTopicSkeleton());
        if (dto.getDefaultPanelSkeleton() != null) entity.setDefaultPanelSkeleton(dto.getDefaultPanelSkeleton());
        if (dto.getDefaultOtherContent() != null) entity.setDefaultOtherContent(dto.getDefaultOtherContent());
        if (dto.getDefaultImageMedia() != null) entity.setDefaultImageMedia(dto.getDefaultImageMedia());
        if (dto.getDefaultTextMedia() != null) entity.setDefaultTextMedia(dto.getDefaultTextMedia());
        if (dto.getDefaultDevType() != null) entity.setDefaultDevType(dto.getDefaultDevType());
        if (dto.getDefaultIndustry() != null) entity.setDefaultIndustry(dto.getDefaultIndustry());
        if (dto.getDefaultProducts() != null) entity.setDefaultProducts(dto.getDefaultProducts());
        if (dto.getDefaultRegions() != null) entity.setDefaultRegions(dto.getDefaultRegions());
        if (dto.getDefaultUniversities() != null) entity.setDefaultUniversities(dto.getDefaultUniversities());
        if (dto.getDefaultLocation() != null) entity.setDefaultLocation(dto.getDefaultLocation());
    }

    private MeetingTemplate toEntity(MeetingTemplateDTO dto) {
        MeetingTemplate e = new MeetingTemplate();
        e.setName(dto.getName());
        e.setDescription(dto.getDescription());
        e.setIconEmoji(dto.getIconEmoji());
        e.setSortWeight(dto.getSortWeight());
        e.setStatus(dto.getStatus());
        e.setDefaultMeetingType(dto.getDefaultMeetingType());
        e.setDefaultForm(dto.getDefaultForm());
        e.setDefaultScene(dto.getDefaultScene());
        e.setDefaultScale(dto.getDefaultScale());
        e.setDefaultDuration(dto.getDefaultDuration());
        e.setDefaultRecurrence(dto.getDefaultRecurrence());
        e.setDefaultTitlePrefix(dto.getDefaultTitlePrefix());
        e.setDefaultHostCompany(dto.getDefaultHostCompany());
        e.setDefaultDepartment(dto.getDefaultDepartment());
        e.setDefaultContact(dto.getDefaultContact());
        e.setDefaultContactTitle(dto.getDefaultContactTitle());
        e.setDefaultContactPhone(dto.getDefaultContactPhone());
        e.setDefaultIntro(dto.getDefaultIntro());
        e.setCoverUrl(dto.getCoverUrl());
        e.setDefaultAudience(dto.getDefaultAudience());
        e.setDefaultTags(dto.getDefaultTags());
        e.setDefaultTopicSkeleton(dto.getDefaultTopicSkeleton());
        e.setDefaultPanelSkeleton(dto.getDefaultPanelSkeleton());
        e.setDefaultOtherContent(dto.getDefaultOtherContent());
        e.setDefaultImageMedia(dto.getDefaultImageMedia());
        e.setDefaultTextMedia(dto.getDefaultTextMedia());
        e.setDefaultDevType(dto.getDefaultDevType());
        e.setDefaultIndustry(dto.getDefaultIndustry());
        e.setDefaultProducts(dto.getDefaultProducts());
        e.setDefaultRegions(dto.getDefaultRegions());
        e.setDefaultUniversities(dto.getDefaultUniversities());
        e.setDefaultLocation(dto.getDefaultLocation());
        return e;
    }

    private MeetingTemplateDTO toDTO(MeetingTemplate e) {
        MeetingTemplateDTO dto = new MeetingTemplateDTO();
        dto.setId(e.getId());
        dto.setName(e.getName());
        dto.setDescription(e.getDescription());
        dto.setIconEmoji(e.getIconEmoji());
        dto.setSortWeight(e.getSortWeight());
        dto.setStatus(e.getStatus());
        dto.setUseCount(e.getUseCount());
        dto.setDefaultMeetingType(e.getDefaultMeetingType());
        dto.setDefaultForm(e.getDefaultForm());
        dto.setDefaultScene(e.getDefaultScene());
        dto.setDefaultScale(e.getDefaultScale());
        dto.setDefaultScaleDisplay(resolveScaleDisplay(e.getDefaultScale()));
        dto.setDefaultDuration(e.getDefaultDuration());
        dto.setDefaultRecurrence(e.getDefaultRecurrence());
        dto.setDefaultTitlePrefix(e.getDefaultTitlePrefix());
        dto.setDefaultHostCompany(e.getDefaultHostCompany());
        dto.setDefaultDepartment(e.getDefaultDepartment());
        dto.setDefaultContact(e.getDefaultContact());
        dto.setDefaultContactTitle(e.getDefaultContactTitle());
        dto.setDefaultContactPhone(e.getDefaultContactPhone());
        dto.setDefaultIntro(e.getDefaultIntro());
        dto.setCoverUrl(e.getCoverUrl());
        dto.setDefaultAudience(e.getDefaultAudience());
        dto.setDefaultTags(e.getDefaultTags());
        dto.setDefaultTopicSkeleton(e.getDefaultTopicSkeleton());
        dto.setDefaultPanelSkeleton(e.getDefaultPanelSkeleton());
        dto.setDefaultOtherContent(e.getDefaultOtherContent());
        dto.setDefaultImageMedia(e.getDefaultImageMedia());
        dto.setDefaultTextMedia(e.getDefaultTextMedia());
        dto.setDefaultDevType(e.getDefaultDevType());
        dto.setDefaultIndustry(e.getDefaultIndustry());
        dto.setDefaultProducts(e.getDefaultProducts());
        dto.setDefaultRegions(e.getDefaultRegions());
        dto.setDefaultUniversities(e.getDefaultUniversities());
        dto.setDefaultLocation(e.getDefaultLocation());
        return dto;
    }

    /**
     * 将 scale code 转换为中文显示
     * 50 -> 50人以下, 200 -> 50-200人, 500 -> 200-500人, 1000 -> 500人以上
     */
    private String resolveScaleDisplay(String scale) {
        if (scale == null || scale.isEmpty()) {
            return null;
        }
        switch (scale) {
            case "50":
                return "50人以下";
            case "200":
                return "50-200人";
            case "500":
                return "200-500人";
            case "1000":
                return "500人以上";
            default:
                return scale;
        }
    }
}
