package com.csdn.meeting.infrastructure.repository.impl.analytics;

import com.csdn.meeting.domain.entity.analytics.TrackEvent;
import com.csdn.meeting.domain.repository.analytics.TrackEventRepository;
import com.csdn.meeting.infrastructure.mapper.analytics.TrackEventMapper;
import com.csdn.meeting.infrastructure.po.analytics.TrackEventPO;
import com.csdn.meeting.infrastructure.repository.mapper.TrackEventPOMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 埋点事件仓储实现
 */
@Repository
public class TrackEventRepositoryImpl implements TrackEventRepository {

    private final TrackEventPOMapper trackEventPOMapper;

    public TrackEventRepositoryImpl(TrackEventPOMapper trackEventPOMapper) {
        this.trackEventPOMapper = trackEventPOMapper;
    }

    @Override
    public TrackEvent save(TrackEvent event) {
        TrackEventPO po = TrackEventMapper.INSTANCE.toPO(event);
        if (po.getReceivedAt() == null) {
            po.setReceivedAt(LocalDateTime.now());
        }
        trackEventPOMapper.insert(po);
        return TrackEventMapper.INSTANCE.toEntity(po);
    }

    @Override
    public int saveBatch(List<TrackEvent> events) {
        if (events == null || events.isEmpty()) {
            return 0;
        }
        LocalDateTime now = LocalDateTime.now();
        List<TrackEventPO> pos = events.stream()
                .map(event -> {
                    TrackEventPO po = TrackEventMapper.INSTANCE.toPO(event);
                    if (po.getReceivedAt() == null) {
                        po.setReceivedAt(now);
                    }
                    return po;
                })
                .collect(Collectors.toList());

        // MyBatis-Plus 的 saveBatch
        for (TrackEventPO po : pos) {
            trackEventPOMapper.insert(po);
        }
        return pos.size();
    }
}
