package com.csdn.meeting.infrastructure.repository.impl.analytics;

import com.csdn.meeting.domain.entity.analytics.AnalyticsTagSubscription;
import com.csdn.meeting.domain.repository.analytics.AnalyticsTagSubscriptionRepository;
import com.csdn.meeting.infrastructure.mapper.analytics.AnalyticsTagSubscriptionMapper;
import com.csdn.meeting.infrastructure.po.analytics.AnalyticsTagSubscriptionPO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 标签订阅埋点仓储实现类
 */
@Repository
@RequiredArgsConstructor
public class AnalyticsTagSubscriptionRepositoryImpl implements AnalyticsTagSubscriptionRepository {

    private final AnalyticsTagSubscriptionMapper subscriptionMapper;

    @Override
    public void save(AnalyticsTagSubscription subscription) {
        AnalyticsTagSubscriptionPO po = convertToPO(subscription);
        subscriptionMapper.insert(po);
    }

    @Override
    public List<AnalyticsTagSubscription> findByUserId(String userId) {
        List<AnalyticsTagSubscriptionPO> pos = subscriptionMapper.selectByUserId(userId);
        return pos.stream().map(this::convertToEntity).collect(Collectors.toList());
    }

    @Override
    public List<AnalyticsTagSubscription> findByTagId(Long tagId) {
        List<AnalyticsTagSubscriptionPO> pos = subscriptionMapper.selectByTagId(tagId);
        return pos.stream().map(this::convertToEntity).collect(Collectors.toList());
    }

    @Override
    public List<AnalyticsTagSubscription> findByUserIdAndTagId(String userId, Long tagId) {
        List<AnalyticsTagSubscriptionPO> pos = subscriptionMapper.selectByUserIdAndTagId(userId, tagId);
        return pos.stream().map(this::convertToEntity).collect(Collectors.toList());
    }

    @Override
    public List<AnalyticsTagSubscription> findByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        List<AnalyticsTagSubscriptionPO> pos = subscriptionMapper.selectByTimeRange(startTime, endTime);
        return pos.stream().map(this::convertToEntity).collect(Collectors.toList());
    }

    @Override
    public long countSubscribedTagsByUserId(String userId) {
        Long count = subscriptionMapper.countSubscribedTagsByUserId(userId);
        return count != null ? count : 0;
    }

    @Override
    public long countSubscriptionsByTagId(Long tagId) {
        Long count = subscriptionMapper.countSubscriptionsByTagId(tagId);
        return count != null ? count : 0;
    }

    private AnalyticsTagSubscriptionPO convertToPO(AnalyticsTagSubscription subscription) {
        AnalyticsTagSubscriptionPO po = new AnalyticsTagSubscriptionPO();
        po.setEventId(subscription.getEventId());
        po.setUserId(subscription.getUserId());
        po.setTagId(subscription.getTagId());
        po.setTagName(subscription.getTagName());
        po.setAction(subscription.getAction());
        po.setSource(subscription.getSource());
        po.setOccurredAt(subscription.getOccurredAt());
        return po;
    }

    private AnalyticsTagSubscription convertToEntity(AnalyticsTagSubscriptionPO po) {
        AnalyticsTagSubscription subscription = new AnalyticsTagSubscription();
        subscription.setId(po.getId());
        subscription.setEventId(po.getEventId());
        subscription.setUserId(po.getUserId());
        subscription.setTagId(po.getTagId());
        subscription.setTagName(po.getTagName());
        subscription.setAction(po.getAction());
        subscription.setSource(po.getSource());
        subscription.setOccurredAt(po.getOccurredAt());
        subscription.setCreatedAt(po.getCreatedAt());
        return subscription;
    }
}
