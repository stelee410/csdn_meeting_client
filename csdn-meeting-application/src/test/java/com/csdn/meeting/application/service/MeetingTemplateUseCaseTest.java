package com.csdn.meeting.application.service;

import com.csdn.meeting.application.dto.MeetingDTO;
import com.csdn.meeting.application.dto.MeetingTemplateDTO;
import com.csdn.meeting.domain.entity.MeetingTemplate;
import com.csdn.meeting.domain.repository.MeetingTemplateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MeetingTemplateUseCase: getAllActive, getById, applyTemplate")
class MeetingTemplateUseCaseTest {

    @Mock
    private MeetingTemplateRepository templateRepository;
    @Mock
    private MeetingApplicationService meetingApplicationService;

    private MeetingTemplateUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new MeetingTemplateUseCase(templateRepository, meetingApplicationService);
    }

    @Test
    @DisplayName("getAllActive returns templates from repository")
    void getAllActive_returnsTemplates() {
        MeetingTemplate t1 = new MeetingTemplate();
        t1.setId(1L);
        t1.setName("技术沙龙");
        t1.setStatus(2);
        when(templateRepository.findAllActive()).thenReturn(Arrays.asList(t1));

        List<MeetingTemplate> result = useCase.getAllActive();

        assertEquals(1, result.size());
        assertEquals("技术沙龙", result.get(0).getName());
        verify(templateRepository).findAllActive();
    }

    @Test
    @DisplayName("getById returns template DTO")
    void getById_returnsDTO() {
        MeetingTemplate t = new MeetingTemplate();
        t.setId(1L);
        t.setName("技术沙龙");
        when(templateRepository.findById(1L)).thenReturn(Optional.of(t));

        MeetingTemplateDTO result = useCase.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("技术沙龙", result.getName());
    }

    @Test
    @DisplayName("getById: not found throws")
    void getById_notFound_throws() {
        when(templateRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> useCase.getById(999L));
    }

    @Test
    @DisplayName("applyTemplate creates draft from template")
    void applyTemplate_createsDraft() {
        MeetingTemplate template = new MeetingTemplate();
        template.setId(1L);
        template.setName("技术沙龙");
        template.setDefaultIntro("简介骨架");
        template.setDefaultScene("开发者会议");
        template.setDefaultTags("Java");
        template.setDefaultAudience("中级工程师");
        when(templateRepository.findById(1L)).thenReturn(Optional.of(template));

        MeetingDTO expectedDto = new MeetingDTO();
        expectedDto.setMeetingId("M123");
        expectedDto.setTitle("技术沙龙 - 新会议");
        expectedDto.setStatus("DRAFT");
        when(meetingApplicationService.createDraft(any())).thenReturn(expectedDto);

        MeetingDTO result = useCase.applyTemplate(1L, "100", "张三");

        assertEquals("M123", result.getMeetingId());
        assertEquals("技术沙龙 - 新会议", result.getTitle());
        assertEquals("DRAFT", result.getStatus());
        verify(meetingApplicationService).createDraft(argThat(cmd ->
                "技术沙龙 - 新会议".equals(cmd.getTitle())
                        && "100".equals(cmd.getCreatorId())
                        && "张三".equals(cmd.getCreatorName())
                        && "开发者会议".equals(cmd.getScene())
        ));
    }

    @Test
    @DisplayName("applyTemplate: template not found throws")
    void applyTemplate_templateNotFound_throws() {
        when(templateRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> useCase.applyTemplate(999L, "1", "张三"));
        verify(meetingApplicationService, never()).createDraft(any());
    }

    @Test
    @DisplayName("create: 新建模板默认 status=0（DRAFT）")
    void create_defaultStatusDraft() {
        MeetingTemplateDTO dto = new MeetingTemplateDTO();
        dto.setName("新模板");
        dto.setDefaultScene("开发者会议");
        MeetingTemplate saved = new MeetingTemplate();
        saved.setId(1L);
        saved.setName("新模板");
        saved.setStatus(0);
        when(templateRepository.save(any(MeetingTemplate.class))).thenAnswer(inv -> {
            MeetingTemplate e = inv.getArgument(0);
            saved.setName(e.getName());
            saved.setStatus(e.getStatus());
            return saved;
        });

        MeetingTemplateDTO result = useCase.create(dto);

        assertNotNull(result);
        assertEquals(0, result.getStatus());
    }

    @Test
    @DisplayName("offline: 下架模板（status → UNLISTED=1）")
    void offline_setsUnlisted() {
        MeetingTemplate t = new MeetingTemplate();
        t.setId(1L);
        t.setName("技术沙龙");
        t.setStatus(2);
        when(templateRepository.findById(1L)).thenReturn(Optional.of(t));
        when(templateRepository.save(any(MeetingTemplate.class))).thenAnswer(inv -> inv.getArgument(0));

        MeetingTemplateDTO result = useCase.offline(1L);

        assertNotNull(result);
        assertEquals(1, result.getStatus());
        verify(templateRepository).save(argThat(e -> Integer.valueOf(1).equals(e.getStatus())));
    }

    @Test
    @DisplayName("update: 更新 defaultTags 和 defaultAudience")
    void update_updatesTagsAndAudience() {
        MeetingTemplate existing = new MeetingTemplate();
        existing.setId(1L);
        existing.setName("旧模板");
        existing.setDefaultTags("old");
        existing.setDefaultAudience("old");
        when(templateRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(templateRepository.save(any(MeetingTemplate.class))).thenAnswer(inv -> inv.getArgument(0));

        MeetingTemplateDTO dto = new MeetingTemplateDTO();
        dto.setDefaultTags("Java,Python,Go");
        dto.setDefaultAudience("开发者,架构师");

        useCase.update(1L, dto);

        verify(templateRepository).save(argThat(e ->
                "Java,Python,Go".equals(e.getDefaultTags())
                        && "开发者,架构师".equals(e.getDefaultAudience())
        ));
    }

    @Test
    @DisplayName("shelve: 上架模板（status → LISTED=2）")
    void shelve_setsListed() {
        MeetingTemplate t = new MeetingTemplate();
        t.setId(1L);
        t.setName("技术沙龙");
        t.setStatus(1);
        when(templateRepository.findById(1L)).thenReturn(Optional.of(t));
        when(templateRepository.save(any(MeetingTemplate.class))).thenAnswer(inv -> inv.getArgument(0));

        MeetingTemplateDTO result = useCase.shelve(1L);

        assertNotNull(result);
        assertEquals(2, result.getStatus());
    }
}
