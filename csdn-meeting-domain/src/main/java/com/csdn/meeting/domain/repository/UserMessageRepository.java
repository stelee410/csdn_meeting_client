package com.csdn.meeting.domain.repository;

import com.csdn.meeting.domain.entity.UserMessage;

import java.util.List;

/**
 * 用户消息仓储接口
 * 提供站内信消息的存储、查询、状态变更等操作
 */
public interface UserMessageRepository {

    /**
     * 保存单条消息
     *
     * @param message 消息实体
     */
    void save(UserMessage message);

    /**
     * 批量保存消息
     *
     * @param messages 消息实体列表
     */
    void saveBatch(List<UserMessage> messages);

    /**
     * 根据消息ID查询
     *
     * @param messageId 消息业务ID
     * @return 消息实体
     */
    UserMessage findByMessageId(String messageId);

    /**
     * 查询用户的消息列表（分页）
     *
     * @param userId 用户ID
     * @param page   页码（从1开始）
     * @param size   每页条数
     * @return 分页结果
     */
    PageResult<UserMessage> findByUserId(String userId, int page, int size);

    /**
     * 查询用户的未读消息列表（分页）
     *
     * @param userId 用户ID
     * @param page   页码（从1开始）
     * @param size   每页条数
     * @return 分页结果
     */
    PageResult<UserMessage> findUnreadByUserId(String userId, int page, int size);

    /**
     * 统计用户未读消息数
     *
     * @param userId 用户ID
     * @return 未读数量
     */
    long countUnreadByUserId(String userId);

    /**
     * 标记消息已读
     *
     * @param messageId 消息业务ID
     * @param userId    用户ID（校验权限）
     */
    void markAsRead(String messageId, String userId);

    /**
     * 标记用户全部消息已读
     *
     * @param userId 用户ID
     */
    void markAllAsRead(String userId);

    /**
     * 删除消息（软删除）
     *
     * @param messageId 消息业务ID
     * @param userId    用户ID（校验权限）
     */
    void deleteById(String messageId, String userId);

    /**
     * 批量删除消息（软删除）
     *
     * @param messageIds 消息业务ID列表
     * @param userId     用户ID（校验权限）
     */
    void deleteByIds(List<String> messageIds, String userId);

    /**
     * 清理过期消息（物理删除）
     * 删除指定日期之前已读的消息
     *
     * @param beforeDate 截止日期
     * @return 删除的消息数量
     */
    int cleanupExpiredMessages(java.time.LocalDateTime beforeDate);

    /**
     * 查询过期消息ID列表（用于批量删除）
     *
     * @param beforeDate 截止日期
     * @param batchSize  批次大小
     * @return 消息ID列表
     */
    List<String> findExpiredMessageIds(java.time.LocalDateTime beforeDate, int batchSize);
}
