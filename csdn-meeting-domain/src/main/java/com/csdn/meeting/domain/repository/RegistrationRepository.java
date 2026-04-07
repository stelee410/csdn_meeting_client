package com.csdn.meeting.domain.repository;

import com.csdn.meeting.domain.entity.Meeting;
import com.csdn.meeting.domain.entity.Registration;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RegistrationRepository {

    Registration save(Registration registration);

    Optional<Registration> findById(Long id);

    /**
     * 按 meetingId + status 分页查询；status 为 null 时返回全部
     */
    PageResult<Registration> findByMeetingIdAndStatus(Long meetingId,
                                                      Registration.RegistrationStatus status,
                                                      int page, int size);

    Optional<Registration> findByUserIdAndMeetingId(String userId, Long meetingId);

    /**
     * 按会议 ID + 手机号查询报名记录（用于同一手机号重复报名校验）
     */
    List<Registration> findByMeetingIdAndPhone(Long meetingId, String phone);

    /**
     * 按 userId 查询报名，且关联的会议 status 在指定列表中，按会议 startTime 倒序分页
     */
    PageResult<Registration> findByUserIdAndMeetingStatusIn(String userId,
                                                            List<Meeting.MeetingStatus> meetingStatuses,
                                                            int page, int size);

    void deleteById(Long id);

    /**
     * 统计指定会议在若干报名状态下的记录数（去重按行，不含已逻辑删除）
     */
    long countByMeetingIdAndStatuses(Long meetingId, Collection<Registration.RegistrationStatus> statuses);
}
