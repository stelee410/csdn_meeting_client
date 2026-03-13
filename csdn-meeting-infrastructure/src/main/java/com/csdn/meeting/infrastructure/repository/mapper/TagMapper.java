package com.csdn.meeting.infrastructure.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csdn.meeting.infrastructure.po.TagPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 标签Mapper接口
 */
@Mapper
public interface TagMapper extends BaseMapper<TagPO> {

    /**
     * 根据标签名称查询标签
     */
    @Select("SELECT * FROM t_tag WHERE tag_name = #{tagName} AND is_deleted = 0 LIMIT 1")
    TagPO selectByTagName(@Param("tagName") String tagName);

    /**
     * 根据标签名称列表批量查询
     */
    List<TagPO> selectByTagNamesIn(@Param("tagNames") List<String> tagNames);

    /**
     * 批量插入或更新标签（忽略重复）
     */
    int batchInsertOrIgnore(@Param("tags") List<TagPO> tags);

    /**
     * 热门标签：按使用该标签的已发布会议数量降序，返回前 limit 个
     * issue001-9
     */
    List<TagPO> selectHotTags(@Param("limit") int limit);
}
