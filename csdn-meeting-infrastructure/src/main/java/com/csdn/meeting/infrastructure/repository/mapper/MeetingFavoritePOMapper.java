package com.csdn.meeting.infrastructure.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csdn.meeting.infrastructure.po.MeetingFavoritePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MeetingFavoritePOMapper extends BaseMapper<MeetingFavoritePO> {

    IPage<MeetingFavoritePO> selectPageByUserIdOrderByCreatedAtDesc(Page<MeetingFavoritePO> page,
                                                                    @Param("userId") String userId);

    void deleteByUserIdAndMeetingId(@Param("userId") String userId, @Param("meetingId") Long meetingId);
}
