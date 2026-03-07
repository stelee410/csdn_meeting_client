package com.csdn.meeting.infrastructure.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csdn.meeting.infrastructure.po.UserTagSubscribePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户标签订阅Mapper接口
 */
@Mapper
public interface UserTagSubscribeMapper extends BaseMapper<UserTagSubscribePO> {

    /**
     * 根据用户ID和标签ID查询订阅记录
     */
    @Select("SELECT * FROM t_user_tag_subscribe " +
            "WHERE user_id = #{userId} AND tag_id = #{tagId} AND is_deleted = 0 LIMIT 1")
    UserTagSubscribePO selectByUserIdAndTagId(@Param("userId") String userId, @Param("tagId") Long tagId);

    /**
     * 不考虑软删 根据用户ID和标签ID查询所有订阅记录
     */
    @Select("SELECT * FROM t_user_tag_subscribe " +
            "WHERE user_id = #{userId} AND tag_id = #{tagId} LIMIT 1")
    UserTagSubscribePO selectAllByUserIdAndTagId(@Param("userId") String userId, @Param("tagId") Long tagId);

    /**
     * 查询用户订阅的标签ID列表
     */
    @Select("SELECT tag_id FROM t_user_tag_subscribe " +
            "WHERE user_id = #{userId} AND is_deleted = 0")
    List<Long> selectTagIdsByUserId(@Param("userId") String userId);

    /**
     * 分页查询用户订阅的标签
     */
    Page<UserTagSubscribePO> selectPageByUserId(Page<UserTagSubscribePO> page, @Param("userId") String userId);

    /**
     * 根据标签ID查询所有订阅用户ID
     */
    @Select("SELECT user_id FROM t_user_tag_subscribe " +
            "WHERE tag_id = #{tagId} AND is_deleted = 0")
    List<String> selectUserIdsByTagId(@Param("tagId") Long tagId);

    /**
     * 根据标签ID列表查询订阅用户ID（用于批量推送）
     */
    List<String> selectUserIdsByTagIds(@Param("tagIds") List<Long> tagIds);

    /**
     * 统计标签的订阅用户数
     */
    @Select("SELECT COUNT(*) FROM t_user_tag_subscribe " +
            "WHERE tag_id = #{tagId} AND is_deleted = 0")
    Long countByTagId(@Param("tagId") Long tagId);

    /**
     * 查询用户在给定标签ID列表中已订阅的标签ID
     */
    List<Long> selectSubscribedTagIdsByUserIdAndTagIds(@Param("userId") String userId, @Param("tagIds") List<Long> tagIds);

    /**
     * 统计用户的订阅标签数
     */
    @Select("SELECT COUNT(*) FROM t_user_tag_subscribe " +
            "WHERE user_id = #{userId} AND is_deleted = 0")
    Long countByUserId(@Param("userId") String userId);

    /**
     * 恢复软删除记录（绕过 TableLogic，仅按 id 更新 is_deleted 与 updated_at）
     */
    @Update("UPDATE t_user_tag_subscribe SET is_deleted = 0 WHERE id = #{id}")
    int restoreById(@Param("id") Long id);
}
