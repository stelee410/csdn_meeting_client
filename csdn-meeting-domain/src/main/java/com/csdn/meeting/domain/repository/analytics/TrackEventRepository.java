package com.csdn.meeting.domain.repository.analytics;

import com.csdn.meeting.domain.entity.analytics.TrackEvent;

import java.util.List;

/**
 * 埋点事件仓储接口
 */
public interface TrackEventRepository {

    /**
     * 保存埋点事件
     * @param event 埋点事件实体
     * @return 保存后的实体
     */
    TrackEvent save(TrackEvent event);

    /**
     * 批量保存埋点事件
     * @param events 埋点事件列表
     * @return 保存数量
     */
    int saveBatch(List<TrackEvent> events);
}
