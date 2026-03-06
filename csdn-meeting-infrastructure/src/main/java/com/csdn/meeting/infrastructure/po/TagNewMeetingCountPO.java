package com.csdn.meeting.infrastructure.po;

import lombok.Data;

/**
 * 标签新会议数量统计PO
 * 用于按标签分组统计指定时间之后新增的会议数量
 */
@Data
public class TagNewMeetingCountPO {

    /**
     * 标签ID
     */
    private Long tagId;

    /**
     * 新会议数量
     */
    private Integer newMeetingCount;
}
