package com.csdn.meeting.application.service;

import com.csdn.meeting.application.dto.CreateMeetingCommand;
import com.csdn.meeting.application.dto.JoinMeetingCommand;
import com.csdn.meeting.application.dto.MeetingDTO;
import com.csdn.meeting.application.dto.ParticipantDTO;
import com.csdn.meeting.domain.entity.Meeting;
import com.csdn.meeting.domain.entity.Participant;
import com.csdn.meeting.domain.repository.MeetingRepository;
import com.csdn.meeting.domain.repository.ParticipantRepository;
import com.csdn.meeting.domain.service.MeetingDomainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MeetingApplicationService {

    private final MeetingRepository meetingRepository;
    private final ParticipantRepository participantRepository;
    private final MeetingDomainService meetingDomainService;

    public MeetingApplicationService(MeetingRepository meetingRepository,
                                     ParticipantRepository participantRepository,
                                     MeetingDomainService meetingDomainService) {
        this.meetingRepository = meetingRepository;
        this.participantRepository = participantRepository;
        this.meetingDomainService = meetingDomainService;
    }

    @Transactional
    public MeetingDTO createMeeting(CreateMeetingCommand command) {
        Meeting meeting = new Meeting();
        meeting.setMeetingId(meetingDomainService.generateMeetingId());
        meeting.setTitle(command.getTitle());
        meeting.setDescription(command.getDescription());
        meeting.setCreatorId(command.getCreatorId());
        meeting.setCreatorName(command.getCreatorName());
        meeting.setStartTime(command.getStartTime());
        meeting.setEndTime(command.getEndTime());
        meeting.setMaxParticipants(command.getMaxParticipants());
        meeting.setStatus(Meeting.MeetingStatus.CREATED);

        Meeting savedMeeting = meetingRepository.save(meeting);
        return toMeetingDTO(savedMeeting);
    }

    @Transactional
    public MeetingDTO joinMeeting(JoinMeetingCommand command) {
        Meeting meeting = meetingRepository.findByMeetingId(command.getMeetingId())
                .orElseThrow(() -> new IllegalArgumentException("会议不存在: " + command.getMeetingId()));

        if (meeting.getStatus() == Meeting.MeetingStatus.ENDED || meeting.getStatus() == Meeting.MeetingStatus.CANCELLED) {
            throw new IllegalStateException("会议已结束或已取消");
        }

        Optional<Participant> existingParticipant = participantRepository
                .findByMeetingIdAndUserId(command.getMeetingId(), command.getUserId());

        Participant participant;
        if (existingParticipant.isPresent()) {
            participant = existingParticipant.get();
            participant.join();
        } else {
            participant = new Participant();
            participant.setMeetingId(command.getMeetingId());
            participant.setUserId(command.getUserId());
            participant.setUserName(command.getUserName());
            participant.setRole(Participant.ParticipantRole.ATTENDEE);
            participant.setStatus(Participant.ParticipantStatus.JOINED);
        }

        participantRepository.save(participant);
        return getMeetingDetail(command.getMeetingId());
    }

    @Transactional
    public void leaveMeeting(String meetingId, Long userId) {
        Participant participant = participantRepository
                .findByMeetingIdAndUserId(meetingId, userId)
                .orElseThrow(() -> new IllegalArgumentException("参与者不存在"));

        participant.leave();
        participantRepository.save(participant);
    }

    @Transactional
    public void startMeeting(String meetingId) {
        Meeting meeting = meetingRepository.findByMeetingId(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("会议不存在"));
        meeting.start();
        meetingRepository.save(meeting);
    }

    @Transactional
    public void endMeeting(String meetingId) {
        Meeting meeting = meetingRepository.findByMeetingId(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("会议不存在"));
        meeting.end();
        meetingRepository.save(meeting);
    }

    @Transactional
    public void cancelMeeting(String meetingId) {
        Meeting meeting = meetingRepository.findByMeetingId(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("会议不存在"));
        meeting.cancel();
        meetingRepository.save(meeting);
    }

    public MeetingDTO getMeetingDetail(String meetingId) {
        Meeting meeting = meetingRepository.findByMeetingId(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("会议不存在"));

        MeetingDTO dto = toMeetingDTO(meeting);
        List<Participant> participants = participantRepository.findByMeetingId(meetingId);
        dto.setParticipants(participants.stream()
                .map(this::toParticipantDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    public List<MeetingDTO> getMeetingsByCreator(Long creatorId) {
        return meetingRepository.findByCreatorId(creatorId).stream()
                .map(this::toMeetingDTO)
                .collect(Collectors.toList());
    }

    public List<MeetingDTO> getAllMeetings() {
        return meetingRepository.findAll().stream()
                .map(this::toMeetingDTO)
                .collect(Collectors.toList());
    }

    private MeetingDTO toMeetingDTO(Meeting meeting) {
        MeetingDTO dto = new MeetingDTO();
        dto.setId(meeting.getId());
        dto.setMeetingId(meeting.getMeetingId());
        dto.setTitle(meeting.getTitle());
        dto.setDescription(meeting.getDescription());
        dto.setCreatorId(meeting.getCreatorId());
        dto.setCreatorName(meeting.getCreatorName());
        dto.setStartTime(meeting.getStartTime());
        dto.setEndTime(meeting.getEndTime());
        dto.setStatus(meeting.getStatus().name());
        dto.setMaxParticipants(meeting.getMaxParticipants());
        return dto;
    }

    private ParticipantDTO toParticipantDTO(Participant participant) {
        ParticipantDTO dto = new ParticipantDTO();
        dto.setId(participant.getId());
        dto.setUserId(participant.getUserId());
        dto.setUserName(participant.getUserName());
        dto.setMeetingId(participant.getMeetingId());
        dto.setRole(participant.getRole().name());
        dto.setStatus(participant.getStatus().name());
        return dto;
    }
}
