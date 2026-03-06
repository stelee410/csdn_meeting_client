package com.csdn.meeting.infrastructure.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csdn.meeting.infrastructure.po.MeetingTagPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会议标签关联Mapper接口
 */
@Mapper
public interface MeetingTagMapper extends BaseMapper<MeetingTagPO> {

    /**
     * 批量插入会议标签关联
     */
    int batchInsert(@Param("meetingTags") List<MeetingTagPO> meetingTags);

    /**
     * 根据会议ID删除所有标签关联（软删除）
     */
    int deleteByMeetingId(@Param("meetingId") String meetingId);

    /**
     * 根据会议ID列表批量查询标签关联
     */
    List<MeetingTagPO> selectByMeetingIds(@Param("meetingIds") List<String> meetingIds);

    /**
     * 按标签分组统计指定标签列表下、指定时间之后新增的会议数量
     * @return 每个标签对应的新会议数量列表
     */
    List<com.csdn.meeting.infrastructure.po.TagNewMeetingCountPO> countNewMeetingsByTagIdsSince(
            @Param("tagIds") List<Long> tagIds, @Param("since") java.time.LocalDateTime since);
}
