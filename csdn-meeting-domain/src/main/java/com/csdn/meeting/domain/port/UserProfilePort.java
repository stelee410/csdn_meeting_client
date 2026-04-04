package com.csdn.meeting.domain.port;

import java.util.List;

/**
 * 用户画像端口：获取报名用户聚合画像（isPremium 时）。
 * Infrastructure 实现对接 CSDN 用户画像库。
 */
public interface UserProfilePort {

    /**
     * 获取报名用户聚合画像（公司/职级/行业/技术栈等）。
     *
     * @param registrationUserIds 报名用户 ID 列表
     * @return 聚合画像 JSON 或简单 Map 表示
     */
    String getAggregatedProfile(List<String> registrationUserIds);
}
