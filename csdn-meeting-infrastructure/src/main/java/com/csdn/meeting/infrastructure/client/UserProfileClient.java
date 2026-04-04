package com.csdn.meeting.infrastructure.client;

import com.csdn.meeting.domain.port.UserProfilePort;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用户画像客户端：实现 UserProfilePort。
 * Stub：返回模拟聚合画像 JSON，无真实 API 调用。
 */
@Component
public class UserProfileClient implements UserProfilePort {

    @Override
    public String getAggregatedProfile(List<String> registrationUserIds) {
        // Stub: 返回模拟聚合画像
        if (registrationUserIds == null || registrationUserIds.isEmpty()) {
            return "{}";
        }
        return "{\"companyDistribution\":{\"互联网\":60,\"金融\":20,\"教育\":20}," +
                "\"positionDistribution\":{\"高级工程师\":40,\"中级工程师\":35,\"初级工程师\":25}," +
                "\"industryDistribution\":{\"AI\":30,\"云计算\":25,\"后端开发\":45}}";
    }
}
