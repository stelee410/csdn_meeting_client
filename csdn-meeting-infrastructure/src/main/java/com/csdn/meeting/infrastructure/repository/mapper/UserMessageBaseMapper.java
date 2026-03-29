package com.csdn.meeting.infrastructure.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csdn.meeting.infrastructure.po.UserMessagePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus BaseMapper for t_user_message
 */
@Mapper
public interface UserMessageBaseMapper extends BaseMapper<UserMessagePO> {

    /**
     * 根据消息ID查询
     */
    @Select("SELECT * FROM t_user_message WHERE message_id = #{messageId} AND is_deleted = FALSE")
    UserMessagePO selectByMessageId(@Param("messageId") String messageId);

    /**
     * 统计用户未读消息数
     */
    @Select("SELECT COUNT(*) FROM t_user_message WHERE user_id = #{userId} AND is_read = FALSE AND is_deleted = FALSE")
    long countUnreadByUserId(@Param("userId") String userId);

    /**
     * 标记消息已读
     */
    @Update("UPDATE t_user_message SET is_read = TRUE, read_time = #{readTime} " +
            "WHERE message_id = #{messageId} AND user_id = #{userId} AND is_deleted = FALSE")
    int markAsRead(@Param("messageId") String messageId, @Param("userId") String userId,
                   @Param("readTime") LocalDateTime readTime);

    /**
     * 标记用户全部消息已读
     */
    @Update("UPDATE t_user_message SET is_read = TRUE, read_time = #{readTime} " +
            "WHERE user_id = #{userId} AND is_read = FALSE AND is_deleted = FALSE")
    int markAllAsRead(@Param("userId") String userId, @Param("readTime") LocalDateTime readTime);

    /**
     * 软删除消息
     */
    @Update("UPDATE t_user_message SET is_deleted = TRUE " +
            "WHERE message_id = #{messageId} AND user_id = #{userId} AND is_deleted = FALSE")
    int deleteByMessageId(@Param("messageId") String messageId, @Param("userId") String userId);
}
