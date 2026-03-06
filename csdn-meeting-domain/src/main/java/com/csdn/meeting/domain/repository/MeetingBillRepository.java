package com.csdn.meeting.domain.repository;

import com.csdn.meeting.domain.entity.MeetingBill;

import java.util.List;

public interface MeetingBillRepository {

    MeetingBill save(MeetingBill bill);

    List<MeetingBill> findByMeetingId(Long meetingId);
}
