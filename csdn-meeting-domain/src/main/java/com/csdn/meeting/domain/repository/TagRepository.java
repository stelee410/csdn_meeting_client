package com.csdn.meeting.domain.repository;

import com.csdn.meeting.domain.entity.Tag;

import java.util.List;
import java.util.Optional;

/**
 * 标签仓储接口
 * @author 13786
 */
public interface TagRepository {

    /**
     * 根据ID查询标签
     */
    Optional<Tag> findById(Long id);

    /**
     * 根据ID列表批量查询标签
     */
    List<Tag> findByIds(List<Long> ids);

    /**
     * 根据名称查询标签
     */
    Optional<Tag> findByTagName(String tagName);

    /**
     * 根据名称列表批量查询标签
     */
    List<Tag> findByTagNamesIn(List<String> tagNames);

    /**
     * 查询所有标签
     */
    List<Tag> findAll();

    /**
     * 根据分类查询标签
     */
    List<Tag> findByCategory(Tag.TagCategory category);

    /**
     * 根据会议ID查询关联的标签
     * 通过 t_meeting.tags 字段解析标签名，再查 t_tag 得到标签实体
     */
    List<Tag> findByMeetingId(String meetingId);

    /**
     * 保存标签
     */
    Tag save(Tag tag);

    /**
     * 批量保存标签
     */
    List<Tag> saveAll(List<Tag> tags);

    /**
     * 删除标签
     */
    void deleteById(Long id);
}
