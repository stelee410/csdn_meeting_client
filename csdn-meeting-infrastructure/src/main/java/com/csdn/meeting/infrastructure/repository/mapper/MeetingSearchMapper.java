package com.csdn.meeting.infrastructure.repository.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csdn.meeting.infrastructure.po.MeetingPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会议搜索Mapper接口
 * 处理多维度筛选和关键词搜索
 */
@Mapper
public interface MeetingSearchMapper {

    /**
     * 分页查询会议列表（支持多维度筛选）
     */
    Page<MeetingPO> selectMeetingList(Page<MeetingPO> page,
                                       @Param("format") Integer format,
                                       @Param("meetingType") Integer meetingType,
                                       @Param("scene") Integer scene,
                                       @Param("startTimeFrom") String startTimeFrom,
                                       @Param("startTimeTo") String startTimeTo,
                                       @Param("keyword") String keyword);

    /**
     * 查询会议列表（不分页，用于导出等场景）
     */
    List<MeetingPO> selectMeetingList(@Param("format") Integer format,
                                     @Param("meetingType") Integer meetingType,
                                     @Param("scene") Integer scene,
                                     @Param("startTimeFrom") String startTimeFrom,
                                     @Param("startTimeTo") String startTimeTo,
                                     @Param("keyword") String keyword);

    /**
     * 根据会议ID查询详情
     */
    MeetingPO selectByMeetingId(@Param("meetingId") String meetingId);

    /**
     * 增加会议热度分数
     */
    int incrementHotScore(@Param("meetingId") String meetingId, @Param("score") int score);

    /**
     * 更新当前报名人数
     */
    int updateCurrentParticipants(@Param("meetingId") String meetingId, @Param("delta") int delta);

    /**
     * 按标签分组统计指定时间之后新增的会议数量（基于 t_meeting.tags 字段）
     */
    List<com.csdn.meeting.infrastructure.po.TagNewMeetingCountPO> countNewMeetingsByTagIdsSince(
            @Param("tagIds") List<Long> tagIds, @Param("since") java.time.LocalDateTime since);
}
