package com.csdn.meeting.application.service.analytics;

import com.csdn.meeting.application.dto.TrackEventCommand;

import java.util.List;

/**
 * 埋点追踪服务接口
 * 提供统一的埋点事件处理能力
 */
public interface AnalyticsTrackService {

    /**
     * 单条上报埋点事件
     * @param command 埋点事件命令
     * @param ipAddress 客户端IP地址
     * @param userAgent 客户端User-Agent
     */
    void trackEvent(TrackEventCommand command, String ipAddress, String userAgent);

    /**
     * 批量上报埋点事件
     * @param commands 埋点事件命令列表
     * @param ipAddress 客户端IP地址
     * @param userAgent 客户端User-Agent
     */
    void trackEvents(List<TrackEventCommand> commands, String ipAddress, String userAgent);
}
