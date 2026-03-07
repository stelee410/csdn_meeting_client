package com.csdn.meeting.infrastructure.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.csdn.meeting.domain.entity.Tag;
import com.csdn.meeting.domain.repository.TagRepository;
import com.csdn.meeting.infrastructure.converter.TagConverter;
import com.csdn.meeting.infrastructure.po.MeetingTagPO;
import com.csdn.meeting.infrastructure.po.TagPO;
import com.csdn.meeting.infrastructure.repository.mapper.MeetingTagMapper;
import com.csdn.meeting.infrastructure.repository.mapper.TagMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 标签仓储实现
 */
@Repository
public class TagRepositoryImpl implements TagRepository {

    private final TagMapper tagMapper;
    private final MeetingTagMapper meetingTagMapper;

    public TagRepositoryImpl(TagMapper tagMapper, MeetingTagMapper meetingTagMapper) {
        this.tagMapper = tagMapper;
        this.meetingTagMapper = meetingTagMapper;
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
    public List<Tag> findByMeetingId(String meetingId) {
        List<TagPO> tagPOList = tagMapper.selectByMeetingId(meetingId);
        return TagConverter.INSTANCE.poListToEntityList(tagPOList);
    }

    @Override
    public Map<String, List<Tag>> findTagsByMeetingIds(List<String> meetingIds) {
        if (meetingIds == null || meetingIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<MeetingTagPO> meetingTagPOList = meetingTagMapper.selectByMeetingIds(meetingIds);
        if (meetingTagPOList == null || meetingTagPOList.isEmpty()) {
            Map<String, List<Tag>> empty = new LinkedHashMap<>();
            for (String mid : meetingIds) {
                empty.put(mid, Collections.emptyList());
            }
            return empty;
        }
        List<Long> tagIds = meetingTagPOList.stream()
                .map(MeetingTagPO::getTagId)
                .distinct()
                .collect(Collectors.toList());
        List<Tag> allTags = findByIds(tagIds);
        Map<Long, Tag> tagMap = allTags.stream().collect(Collectors.toMap(Tag::getId, t -> t, (a, b) -> a));
        Map<String, List<Tag>> result = new LinkedHashMap<>();
        for (String mid : meetingIds) {
            result.put(mid, new ArrayList<>());
        }
        for (MeetingTagPO mt : meetingTagPOList) {
            Tag tag = tagMap.get(mt.getTagId());
            if (tag != null) {
                result.get(mt.getMeetingId()).add(tag);
            }
        }
        return result;
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

    @Override
    public void deleteByMeetingId(String meetingId) {
        meetingTagMapper.deleteByMeetingId(meetingId);
    }

    @Override
    public void addMeetingTag(String meetingId, Long tagId) {
        MeetingTagPO meetingTagPO = new MeetingTagPO();
        meetingTagPO.setMeetingId(meetingId);
        meetingTagPO.setTagId(tagId);
        meetingTagMapper.insert(meetingTagPO);
    }

    @Override
    public void addMeetingTags(String meetingId, List<Long> tagIds) {
        List<MeetingTagPO> meetingTagPOList = tagIds.stream().map(tagId -> {
            MeetingTagPO meetingTagPO = new MeetingTagPO();
            meetingTagPO.setMeetingId(meetingId);
            meetingTagPO.setTagId(tagId);
            return meetingTagPO;
        }).collect(Collectors.toList());
        meetingTagMapper.batchInsert(meetingTagPOList);
    }
}
