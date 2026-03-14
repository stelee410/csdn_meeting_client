package com.csdn.meeting.infrastructure.repository.impl;

import com.csdn.meeting.domain.entity.Meeting;
import com.csdn.meeting.infrastructure.po.MeetingPO;
import com.csdn.meeting.infrastructure.repository.mapper.MeetingAgendaItemPOMapper;
import com.csdn.meeting.infrastructure.repository.mapper.MeetingPOMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MeetingRepositoryImpl: targetAudience 逗号分隔转 JSON")
class MeetingRepositoryImplTargetAudienceTest {

    @Mock
    private MeetingPOMapper meetingPOMapper;
    @Mock
    private MeetingAgendaItemPOMapper agendaItemPOMapper;

    private MeetingRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new MeetingRepositoryImpl(meetingPOMapper, agendaItemPOMapper, new com.csdn.meeting.infrastructure.mapper.AgendaTreeConverter());
    }

    @Test
    @DisplayName("save: 逗号分隔 targetAudience 存入时转为 JSON 数组")
    void save_commaSeparatedTargetAudience_convertedToJsonArray() {
        Meeting meeting = new Meeting();
        meeting.setTitle("Test");
        meeting.setStatus(Meeting.MeetingStatus.DRAFT);
        meeting.setTargetAudience("developer,architect,product_manager");

        when(meetingPOMapper.insert(any(MeetingPO.class))).thenAnswer(inv -> {
            MeetingPO po = inv.getArgument(0);
            po.setId(1L); // 模拟 MyBatis 回填 id
            return 1;
        });
        doNothing().when(agendaItemPOMapper).deleteByMeetingId(any());

        repository.save(meeting);

        ArgumentCaptor<MeetingPO> captor = ArgumentCaptor.forClass(MeetingPO.class);
        verify(meetingPOMapper).insert(captor.capture());
        String targetAudience = captor.getValue().getTargetAudience();
        assertNotNull(targetAudience);
        assertTrue(targetAudience.startsWith("["), "应为 JSON 数组: " + targetAudience);
        assertTrue(targetAudience.contains("\"developer\""), "应含 developer: " + targetAudience);
    }
}
