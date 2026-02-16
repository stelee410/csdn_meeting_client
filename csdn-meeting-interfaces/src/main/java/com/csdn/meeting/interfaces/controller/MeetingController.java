package com.csdn.meeting.interfaces.controller;

import com.csdn.meeting.application.dto.CreateMeetingCommand;
import com.csdn.meeting.application.dto.JoinMeetingCommand;
import com.csdn.meeting.application.dto.MeetingDTO;
import com.csdn.meeting.application.service.MeetingApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meetings")
public class MeetingController {

    private final MeetingApplicationService meetingApplicationService;

    public MeetingController(MeetingApplicationService meetingApplicationService) {
        this.meetingApplicationService = meetingApplicationService;
    }

    @PostMapping
    public ResponseEntity<MeetingDTO> createMeeting(@RequestBody CreateMeetingCommand command) {
        MeetingDTO meeting = meetingApplicationService.createMeeting(command);
        return ResponseEntity.ok(meeting);
    }

    @GetMapping("/{meetingId}")
    public ResponseEntity<MeetingDTO> getMeeting(@PathVariable String meetingId) {
        MeetingDTO meeting = meetingApplicationService.getMeetingDetail(meetingId);
        return ResponseEntity.ok(meeting);
    }

    @GetMapping
    public ResponseEntity<List<MeetingDTO>> getAllMeetings() {
        List<MeetingDTO> meetings = meetingApplicationService.getAllMeetings();
        return ResponseEntity.ok(meetings);
    }

    @GetMapping("/creator/{creatorId}")
    public ResponseEntity<List<MeetingDTO>> getMeetingsByCreator(@PathVariable Long creatorId) {
        List<MeetingDTO> meetings = meetingApplicationService.getMeetingsByCreator(creatorId);
        return ResponseEntity.ok(meetings);
    }

    @PostMapping("/{meetingId}/join")
    public ResponseEntity<MeetingDTO> joinMeeting(@PathVariable String meetingId, @RequestBody JoinMeetingCommand command) {
        command.setMeetingId(meetingId);
        MeetingDTO meeting = meetingApplicationService.joinMeeting(command);
        return ResponseEntity.ok(meeting);
    }

    @PostMapping("/{meetingId}/leave")
    public ResponseEntity<Void> leaveMeeting(@PathVariable String meetingId, @RequestParam Long userId) {
        meetingApplicationService.leaveMeeting(meetingId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{meetingId}/start")
    public ResponseEntity<Void> startMeeting(@PathVariable String meetingId) {
        meetingApplicationService.startMeeting(meetingId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{meetingId}/end")
    public ResponseEntity<Void> endMeeting(@PathVariable String meetingId) {
        meetingApplicationService.endMeeting(meetingId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{meetingId}/cancel")
    public ResponseEntity<Void> cancelMeeting(@PathVariable String meetingId) {
        meetingApplicationService.cancelMeeting(meetingId);
        return ResponseEntity.ok().build();
    }
}
