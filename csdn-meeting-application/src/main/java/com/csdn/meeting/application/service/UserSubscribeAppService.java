package com.csdn.meeting.application.service;

import com.csdn.meeting.application.dto.PageResult;
import com.csdn.meeting.application.dto.SubscribeResultDTO;
import com.csdn.meeting.application.dto.SubscriptionCheckDTO;
import com.csdn.meeting.application.dto.UserSubscriptionDTO;
import com.csdn.meeting.domain.entity.Tag;
import com.csdn.meeting.domain.entity.UserTagSubscribe;
import com.csdn.meeting.domain.repository.MeetingSearchRepository;
import com.csdn.meeting.domain.repository.TagRepository;
import com.csdn.meeting.domain.repository.UserTagSubscribeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户订阅应用服务
 * 处理标签订阅、取消订阅、查询订阅列表等操作
 */
@Slf4j
@Service
public class UserSubscribeAppService {

    private final UserTagSubscribeRepository userTagSubscribeRepository;
    private final TagRepository tagRepository;
    private final MeetingSearchRepository meetingSearchRepository;

    public UserSubscribeAppService(UserTagSubscribeRepository userTagSubscribeRepository,
                                   TagRepository tagRepository,
                                   MeetingSearchRepository meetingSearchRepository) {
        this.userTagSubscribeRepository = userTagSubscribeRepository;
        this.tagRepository = tagRepository;
        this.meetingSearchRepository = meetingSearchRepository;
    }

    /**
     * 订阅标签
     */
    public SubscribeResultDTO subscribeTag(String userId, Long tagId) {
        // 检查标签是否存在
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("标签不存在"));

        // 检查是否已订阅
        if (userTagSubscribeRepository.exists(userId, tagId)) {
            return new SubscribeResultDTO(true, "您已订阅该标签");
        }

        // 创建订阅
        UserTagSubscribe subscribe = new UserTagSubscribe();
        subscribe.setUserId(userId);
        subscribe.setTagId(tagId);
        subscribe.setCreateBy(userId);

        userTagSubscribeRepository.save(subscribe);

        log.info("用户[{}]订阅标签[{}]成功", userId, tagId);
        return new SubscribeResultDTO(true, "订阅成功，该标签下新会议发布时将第一时间通知您");
    }

    /**
     * 取消订阅标签
     */
    public SubscribeResultDTO unsubscribeTag(String userId, Long tagId) {
        // 检查是否已订阅
        if (!userTagSubscribeRepository.exists(userId, tagId)) {
            return new SubscribeResultDTO(false, "您未订阅该标签");
        }

        // 取消订阅（软删除）
        userTagSubscribeRepository.unsubscribe(userId, tagId);

        log.info("用户[{}]取消订阅标签[{}]成功", userId, tagId);
        return new SubscribeResultDTO(false, "已取消订阅");
    }

    /**
     * 获取用户订阅的标签列表
     */
    public PageResult<UserSubscriptionDTO> getUserSubscribedTags(String userId, int page, int size) {
        // 真实查询总数（不走简化）
        long total = userTagSubscribeRepository.countByUserId(userId);
        if (total == 0) {
            return PageResult.of(0L, (long) page, (long) size, java.util.Collections.emptyList());
        }

        List<UserTagSubscribe> subscriptions = userTagSubscribeRepository.findByUserId(userId, page, size);
        if (subscriptions.isEmpty()) {
            return PageResult.of(total, (long) page, (long) size, java.util.Collections.emptyList());
        }

        List<Long> tagIds = subscriptions.stream().map(UserTagSubscribe::getTagId).distinct().collect(Collectors.toList());
        List<Tag> tags = tagRepository.findByIds(tagIds);
        Map<Long, Tag> tagMap = tags.stream().collect(Collectors.toMap(Tag::getId, t -> t, (a, b) -> a));

        // 批量查询每个订阅标签的新会议数量（订阅时间之后创建的会议）
        Map<Long, Integer> newMeetingCounts = queryNewMeetingCounts(userId, subscriptions);

        List<UserSubscriptionDTO> records = subscriptions.stream()
                .map(sub -> convertToUserSubscriptionDTO(sub, tagMap.get(sub.getTagId()), newMeetingCounts.getOrDefault(sub.getTagId(), 0)))
                .collect(Collectors.toList());

        return PageResult.of(total, (long) page, (long) size, records);
    }

    /**
     * 批量查询用户订阅标签的新会议数量（订阅时间之后创建的会议）
     * 按标签分组统计，每个标签有独立的新会议数量
     */
    private Map<Long, Integer> queryNewMeetingCounts(String userId, List<UserTagSubscribe> subscriptions) {
        List<Long> tagIds = subscriptions.stream().map(UserTagSubscribe::getTagId).distinct().collect(Collectors.toList());

        // 取最早的订阅时间作为统一查询起点
        LocalDateTime earliestSubscribeTime = subscriptions.stream()
                .map(UserTagSubscribe::getCreateTime)
                .min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now().minusMonths(1));

        // 按标签分组统计新会议数量（SQL 中使用 GROUP BY tag_id）
        Map<Long, Integer> tagNewMeetingCounts = meetingSearchRepository.countNewMeetingsByTagIdsSince(tagIds, earliestSubscribeTime);

        // 确保每个标签都有值（没有新会议的标签设为0）
        Map<Long, Integer> result = new HashMap<>();
        for (Long tagId : tagIds) {
            result.put(tagId, tagNewMeetingCounts.getOrDefault(tagId, 0));
        }
        return result;
    }

    /**
     * 获取用户订阅的所有标签ID列表
     */
    public List<Long> getUserSubscribedTagIds(String userId) {
        List<UserTagSubscribe> subscriptions = userTagSubscribeRepository.findByUserId(userId);
        return subscriptions.stream()
                .map(UserTagSubscribe::getTagId)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 检查用户是否订阅了指定标签
     */
    public SubscriptionCheckDTO checkUserSubscribed(String userId, Long tagId) {
        // 检查标签是否存在
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("标签不存在"));

        SubscriptionCheckDTO dto = new SubscriptionCheckDTO();
        dto.setTagId(tagId);
        dto.setTagName(tag.getTagName());

        UserTagSubscribe subscribe = userTagSubscribeRepository.findByUserIdAndTagId(userId, tagId)
                .orElse(null);

        if (subscribe != null && subscribe.getIsDeleted() == 0) {
            dto.setSubscribed(true);
            dto.setSubscribeTime(subscribe.getCreateTime());
        } else {
            dto.setSubscribed(false);
        }

        return dto;
    }

    /**
     * 将UserTagSubscribe转换为UserSubscriptionDTO（标签信息与新会议数由外部传入，避免循环内查库）
     */
    private UserSubscriptionDTO convertToUserSubscriptionDTO(UserTagSubscribe subscribe, Tag tag, int newMeetingCount) {
        UserSubscriptionDTO dto = new UserSubscriptionDTO();
        dto.setTagId(subscribe.getTagId());
        dto.setSubscribeTime(subscribe.getCreateTime());

        if (tag != null) {
            dto.setTagName(tag.getTagName());
            dto.setTagCategory(tag.getTagCategory() != null ? tag.getTagCategory().name().toLowerCase() : null);
            dto.setTagCategoryName(tag.getTagCategory() != null ? tag.getTagCategory().getDisplayName() : null);
        }

        dto.setNewMeetingCount(newMeetingCount);
        return dto;
    }
}
