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
     * 热门标签：按使用该标签的已发布会议数降序，返回前 limit 个
     * issue001-9
     */
    List<Tag> findHotTags(int limit);

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

    /**
     * 按名称批量查找或新建标签，返回含 ID 的完整标签列表。
     * 已存在的标签直接返回，不存在的以 defaultCategory 新建后返回。
     * AI 解析/生成场景使用，确保标签名可安全转换为 ID 存入 t_meeting.tags。
     */
    List<Tag> findOrCreateByNames(List<String> tagNames, Tag.TagCategory defaultCategory);
}
