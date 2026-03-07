package com.csdn.meeting.domain.repository;

import com.csdn.meeting.domain.entity.Meeting;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 会议搜索仓储接口
 * 提供多维度筛选和关键词搜索功能，分页查询使用 MyBatis-Plus IPage 一次返回列表与总数
 */
public interface MeetingSearchRepository {

    /**
     * 根据会议ID查询
     */
    Optional<Meeting> findByMeetingId(String meetingId);

    /**
     * 分页查询会议列表（支持多维度筛选），一次查询返回列表与总数
     */
    PageResult<Meeting> findMeetingList(Integer format, Integer meetingType, Integer scene,
                                       LocalDateTime startTimeFrom, LocalDateTime startTimeTo,
                                       String keyword, int page, int size);

    /**
     * 关键词搜索会议（分页）
     */
    PageResult<Meeting> searchByKeyword(String keyword, int page, int size);

    /**
     * 增加会议热度
     */
    void incrementHotScore(String meetingId, int score);

    /**
     * 更新当前报名人数
     */
    void updateCurrentParticipants(String meetingId, int delta);

    /**
     * 按标签分组统计指定标签下、指定时间之后新增的会议数量（用于计算用户订阅标签的新会议数）
     * @return key=tagId, value=该标签下新会议数量
     */
    java.util.Map<Long, Integer> countNewMeetingsByTagIdsSince(List<Long> tagIds, LocalDateTime since);
}
