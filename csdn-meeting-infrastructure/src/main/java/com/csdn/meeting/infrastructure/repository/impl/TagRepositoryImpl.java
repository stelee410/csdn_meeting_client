package com.csdn.meeting.infrastructure.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.csdn.meeting.domain.entity.Tag;
import com.csdn.meeting.domain.repository.TagRepository;
import com.csdn.meeting.infrastructure.converter.TagConverter;
import com.csdn.meeting.infrastructure.po.TagPO;
import com.csdn.meeting.infrastructure.repository.mapper.TagMapper;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 标签仓储实现
 * 会议与标签的关联通过 t_meeting.tags 字段（逗号分隔的 tagId），不依赖 t_meeting_tag 表
 */
@Repository
public class TagRepositoryImpl implements TagRepository {

    private final TagMapper tagMapper;
    private final com.csdn.meeting.domain.repository.MeetingSearchRepository meetingSearchRepository;

    public TagRepositoryImpl(TagMapper tagMapper,
                            com.csdn.meeting.domain.repository.MeetingSearchRepository meetingSearchRepository) {
        this.tagMapper = tagMapper;
        this.meetingSearchRepository = meetingSearchRepository;
    }

    @Override
    public Optional<Tag> findById(Long id) {
        TagPO tagPO = tagMapper.selectById(id);
        return Optional.ofNullable(tagPO).map(TagConverter.INSTANCE::poToEntity);
    }

    @Override
    public List<Tag> findByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        List<TagPO> poList = tagMapper.selectBatchIds(ids);
        if (poList == null || poList.isEmpty()) {
            return Collections.emptyList();
        }
        return TagConverter.INSTANCE.poListToEntityList(poList);
    }

    @Override
    public Optional<Tag> findByTagName(String tagName) {
        TagPO tagPO = tagMapper.selectByTagName(tagName);
        return Optional.ofNullable(tagPO).map(TagConverter.INSTANCE::poToEntity);
    }

    @Override
    public List<Tag> findByTagNamesIn(List<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return Collections.emptyList();
        }
        List<TagPO> poList = tagMapper.selectByTagNamesIn(tagNames);
        if (poList == null || poList.isEmpty()) {
            return Collections.emptyList();
        }
        return TagConverter.INSTANCE.poListToEntityList(poList);
    }

    @Override
    public List<Tag> findAll() {
        List<TagPO> tagPOList = tagMapper.selectList(
                new LambdaQueryWrapper<TagPO>().eq(TagPO::getIsDeleted, 0)
        );
        return TagConverter.INSTANCE.poListToEntityList(tagPOList);
    }

    @Override
    public List<Tag> findByCategory(Tag.TagCategory category) {
        List<TagPO> tagPOList = tagMapper.selectList(
                new LambdaQueryWrapper<TagPO>()
                        .eq(TagPO::getTagCategory, category.name().toLowerCase())
                        .eq(TagPO::getIsDeleted, 0)
        );
        return TagConverter.INSTANCE.poListToEntityList(tagPOList);
    }

    @Override
    public List<Tag> findHotTags(int limit) {
        if (limit <= 0) return Collections.emptyList();
        List<TagPO> poList = tagMapper.selectHotTags(limit);
        return poList == null || poList.isEmpty()
                ? Collections.emptyList()
                : TagConverter.INSTANCE.poListToEntityList(poList);
    }

    @Override
    public List<Tag> findByMeetingId(String meetingId) {
        return meetingSearchRepository.findByMeetingId(meetingId)
                .map(meeting -> parseTagIdsFromTagsString(meeting.getTags()))
                .filter(ids -> !ids.isEmpty())
                .map(this::findByIds)
                .orElse(Collections.emptyList());
    }

    /**
     * 从 t_meeting.tags 字符串解析出标签ID列表（逗号分隔，如 "1,2,3"）
     */
    private static List<Long> parseTagIdsFromTagsString(String tagsStr) {
        if (tagsStr == null || tagsStr.trim().isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> ids = new java.util.ArrayList<>();
        for (String s : tagsStr.split(",")) {
            String trimmed = s.trim();
            if (trimmed.isEmpty()) continue;
            try {
                ids.add(Long.parseLong(trimmed));
            } catch (NumberFormatException ignored) {
                // 忽略非法 ID
            }
        }
        return ids.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public Tag save(Tag tag) {
        TagPO tagPO = TagConverter.INSTANCE.entityToPo(tag);
        if (tag.getId() == null) {
            tagMapper.insert(tagPO);
        } else {
            tagMapper.updateById(tagPO);
        }
        return TagConverter.INSTANCE.poToEntity(tagPO);
    }

    @Override
    public List<Tag> saveAll(List<Tag> tags) {
        if (tags == null || tags.isEmpty()) {
            return Collections.emptyList();
        }
        List<TagPO> tagPOList = TagConverter.INSTANCE.entityListToPoList(tags);
        tagMapper.batchInsertOrIgnore(tagPOList);
        List<String> tagNames = tags.stream().map(Tag::getTagName).distinct().collect(Collectors.toList());
        return findByTagNamesIn(tagNames);
    }

    @Override
    public void deleteById(Long id) {
        tagMapper.deleteById(id);
    }
}
