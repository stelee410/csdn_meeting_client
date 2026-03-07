package com.csdn.meeting.infrastructure.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csdn.meeting.infrastructure.po.MeetingPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface MeetingPOMapper extends BaseMapper<MeetingPO> {

    List<MeetingPO> selectByCreatorId(@Param("creatorId") Long creatorId);

    IPage<MeetingPO> selectPageByCreatorId(Page<MeetingPO> page, @Param("creatorId") Long creatorId);

    IPage<MeetingPO> selectPageByCreatorIdAndStatusIn(Page<MeetingPO> page,
                                                      @Param("creatorId") Long creatorId,
                                                      @Param("statuses") List<Integer> statuses);

    IPage<MeetingPO> selectPageByCreatorIdAndStartTimeBetween(Page<MeetingPO> page,
                                                              @Param("creatorId") Long creatorId,
                                                              @Param("start") LocalDateTime start,
                                                              @Param("end") LocalDateTime end);

    IPage<MeetingPO> selectPageByCreatorIdAndStatusInAndStartTimeBetween(Page<MeetingPO> page,
                                                                         @Param("creatorId") Long creatorId,
                                                                         @Param("statuses") List<Integer> statuses,
                                                                         @Param("start") LocalDateTime start,
                                                                         @Param("end") LocalDateTime end);

    List<MeetingPO> selectByCreatorIdAndStatus(@Param("creatorId") Long creatorId, @Param("status") Integer status);

    List<MeetingPO> selectByCreatorIdAndStartTimeBetween(@Param("creatorId") Long creatorId,
                                                         @Param("start") LocalDateTime start,
                                                         @Param("end") LocalDateTime end);

    List<MeetingPO> selectByStatus(@Param("status") Integer status);

    List<MeetingPO> selectByStatusAndStartTimeLessThanEqual(@Param("status") Integer status,
                                                            @Param("threshold") LocalDateTime threshold);

    List<MeetingPO> selectByStatusAndEndTimeLessThanEqual(@Param("status") Integer status,
                                                          @Param("threshold") LocalDateTime threshold);
}
