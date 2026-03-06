package com.csdn.meeting.domain.repository;

import com.csdn.meeting.domain.entity.Meeting;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MeetingRepository {

    Meeting save(Meeting meeting);

    Optional<Meeting> findById(Long id);

    Optional<Meeting> findByMeetingId(String meetingId);

    List<Meeting> findByCreatorId(Long creatorId);

    List<Meeting> findByCreatorIdAndStatus(Long creatorId, Meeting.MeetingStatus status);

    List<Meeting> findByCreatorIdAndStartTimeBetween(Long creatorId, LocalDateTime start, LocalDateTime end);

    /**
     * 我创建的会议：分页，支持按 status、startDate、endDate 筛选
     * statuses 为空=不按状态筛；startFrom/endTo 为 null=不按时间筛
     */
    PageResult<Meeting> findPageByCreatorId(Long creatorId,
                                            List<Meeting.MeetingStatus> statuses,
                                            LocalDateTime startFrom,
                                            LocalDateTime endTo,
                                            int page, int size);

    List<Meeting> findByStatus(Meeting.MeetingStatus status);

    /**
     * 查找 PUBLISHED 且 startTime &lt;= threshold 的会议（用于定时任务自动开始）
     */
    List<Meeting> findPublishedWithStartTimeBefore(LocalDateTime threshold);

    /**
     * 查找 IN_PROGRESS 且 endTime &lt;= threshold 的会议（用于定时任务自动结束）
     */
    List<Meeting> findInProgressWithEndTimeBefore(LocalDateTime threshold);

    List<Meeting> findAll();

    void deleteById(Long id);

    void delete(Meeting meeting);
}
