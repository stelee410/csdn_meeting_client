package com.csdn.meeting.infrastructure.repository.impl;

import com.csdn.meeting.domain.entity.PromotionConfig;
import com.csdn.meeting.domain.repository.PromotionConfigRepository;
import com.csdn.meeting.infrastructure.po.PromotionConfigPO;
import com.csdn.meeting.infrastructure.repository.mapper.PromotionConfigPOMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class PromotionConfigRepositoryImpl implements PromotionConfigRepository {

    private final PromotionConfigPOMapper configPOMapper;

    public PromotionConfigRepositoryImpl(PromotionConfigPOMapper configPOMapper) {
        this.configPOMapper = configPOMapper;
    }

    @Override
    public PromotionConfig save(PromotionConfig config) {
        PromotionConfigPO po = toPO(config);
        if (po.getId() == null) {
            configPOMapper.insert(po);
        } else {
            configPOMapper.updateById(po);
        }
        return toEntity(po);
    }

    @Override
    public Optional<PromotionConfig> findById(Long id) {
        PromotionConfigPO po = configPOMapper.selectById(id);
        return po == null ? Optional.empty() : Optional.of(toEntity(po));
    }

    @Override
    public Optional<PromotionConfig> findByMeetingId(Long meetingId) {
        PromotionConfigPO po = configPOMapper.selectFirstByMeetingIdOrderByCreatedAtDesc(meetingId);
        return po == null ? Optional.empty() : Optional.of(toEntity(po));
    }

    private PromotionConfigPO toPO(PromotionConfig e) {
        PromotionConfigPO po = new PromotionConfigPO();
        po.setId(e.getId());
        po.setMeetingId(e.getMeetingId());
        po.setUserIntents(e.getUserIntents());
        po.setBehaviorPeriod(e.getBehaviorPeriod());
        po.setTargetBehaviors(e.getTargetBehaviors());
        po.setTargetRegions(e.getTargetRegions());
        po.setTargetIndustries(e.getTargetIndustries());
        po.setChannels(e.getChannels());
        po.setPayMode(e.getPayMode());
        po.setEstimatedReach(e.getEstimatedReach());
        po.setEstimatedImpressions(e.getEstimatedImpressions());
        po.setEstimatedClicks(e.getEstimatedClicks());
        po.setBasePrice(e.getBasePrice());
        po.setOrderStatus(e.getOrderStatus());
        po.setOrderCreatedAt(e.getOrderCreatedAt());
        po.setCreatedAt(e.getCreatedAt());
        return po;
    }

    private PromotionConfig toEntity(PromotionConfigPO po) {
        PromotionConfig e = new PromotionConfig();
        e.setId(po.getId());
        e.setMeetingId(po.getMeetingId());
        e.setUserIntents(po.getUserIntents());
        e.setBehaviorPeriod(po.getBehaviorPeriod());
        e.setTargetBehaviors(po.getTargetBehaviors());
        e.setTargetRegions(po.getTargetRegions());
        e.setTargetIndustries(po.getTargetIndustries());
        e.setChannels(po.getChannels());
        e.setPayMode(po.getPayMode());
        e.setEstimatedReach(po.getEstimatedReach());
        e.setEstimatedImpressions(po.getEstimatedImpressions());
        e.setEstimatedClicks(po.getEstimatedClicks());
        e.setBasePrice(po.getBasePrice());
        e.setOrderStatus(po.getOrderStatus());
        e.setOrderCreatedAt(po.getOrderCreatedAt());
        e.setCreatedAt(po.getCreatedAt());
        return e;
    }
}
