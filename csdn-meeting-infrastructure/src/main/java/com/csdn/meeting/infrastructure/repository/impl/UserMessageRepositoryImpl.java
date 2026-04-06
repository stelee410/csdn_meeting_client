package com.csdn.meeting.infrastructure.repository.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csdn.meeting.domain.entity.UserMessage;
import com.csdn.meeting.domain.repository.PageResult;
import com.csdn.meeting.domain.repository.UserMessageRepository;
import com.csdn.meeting.infrastructure.po.UserMessagePO;
import com.csdn.meeting.infrastructure.repository.mapper.UserMessageBaseMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 用户消息仓储实现（MyBatis-Plus版本）
 */
@Repository
public class UserMessageRepositoryImpl implements UserMessageRepository {

    private static final Logger logger = LoggerFactory.getLogger(UserMessageRepositoryImpl.class);

    private final UserMessageBaseMapper userMessageBaseMapper;

    public UserMessageRepositoryImpl(UserMessageBaseMapper userMessageBaseMapper) {
        this.userMessageBaseMapper = userMessageBaseMapper;
    }

    @Override
    public void save(UserMessage message) {
        UserMessagePO po = toPO(message);
        if (po.getMessageId() == null || po.getMessageId().isEmpty()) {
            po.setMessageId(generateMessageId());
        }
        if (po.getCreatedAt() == null) {
            po.setCreatedAt(LocalDateTime.now());
        }
        userMessageBaseMapper.insert(po);
        message.setId(po.getId());
    }

    @Override
    public void saveBatch(List<UserMessage> messages) {
        if (messages == null || messages.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        for (UserMessage message : messages) {
            try {
                UserMessagePO po = toPO(message);
                if (po.getMessageId() == null || po.getMessageId().isEmpty()) {
                    po.setMessageId(generateMessageId());
                }
                if (po.getCreatedAt() == null) {
                    po.setCreatedAt(now);
                }
                userMessageBaseMapper.insert(po);
                message.setId(po.getId());
            } catch (Exception e) {
                logger.error("批量保存消息失败: userId={}, messageType={}, error={}",
                        message.getUserId(), message.getMessageType(), e.getMessage());
                // 继续处理下一条，不阻断
            }
        }
    }

    @Override
    public UserMessage findByMessageId(String messageId) {
        UserMessagePO po = userMessageBaseMapper.selectByMessageId(messageId);
        return po != null ? toEntity(po) : null;
    }

    @Override
    public PageResult<UserMessage> findByUserId(String userId, int page, int size) {
        Page<UserMessagePO> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<UserMessagePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserMessagePO::getUserId, userId)
                .eq(UserMessagePO::getIsDeleted, false)
                // 未读消息排在前面，再按创建时间倒序
                .orderByAsc(UserMessagePO::getIsRead)
                .orderByDesc(UserMessagePO::getCreatedAt);

        IPage<UserMessagePO> resultPage = userMessageBaseMapper.selectPage(pageParam, wrapper);

        List<UserMessage> content = resultPage.getRecords().stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
        return new PageResult<>(content, resultPage.getTotal(), page, size);
    }

    @Override
    public PageResult<UserMessage> findUnreadByUserId(String userId, int page, int size) {
        Page<UserMessagePO> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<UserMessagePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserMessagePO::getUserId, userId)
                .eq(UserMessagePO::getIsRead, false)
                .eq(UserMessagePO::getIsDeleted, false)
                .orderByDesc(UserMessagePO::getCreatedAt);

        IPage<UserMessagePO> resultPage = userMessageBaseMapper.selectPage(pageParam, wrapper);

        List<UserMessage> content = resultPage.getRecords().stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
        return new PageResult<>(content, resultPage.getTotal(), page, size);
    }

    @Override
    public PageResult<UserMessage> findByUserIdAndBizType(String userId, String bizType, int page, int size) {
        Page<UserMessagePO> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<UserMessagePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserMessagePO::getUserId, userId)
                .eq(UserMessagePO::getIsDeleted, false);

        // 业务类型筛选逻辑：MEETING 包含 MEETING 和 REGISTRATION
        if ("MEETING".equals(bizType)) {
            wrapper.in(UserMessagePO::getBizType, "MEETING", "REGISTRATION");
        } else if ("SYSTEM".equals(bizType)) {
            wrapper.eq(UserMessagePO::getBizType, "SYSTEM");
        }

        // 未读消息排在前面，再按创建时间倒序
        wrapper.orderByAsc(UserMessagePO::getIsRead)
               .orderByDesc(UserMessagePO::getCreatedAt);

        IPage<UserMessagePO> resultPage = userMessageBaseMapper.selectPage(pageParam, wrapper);

        List<UserMessage> content = resultPage.getRecords().stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
        return new PageResult<>(content, resultPage.getTotal(), page, size);
    }

    @Override
    public PageResult<UserMessage> findUnreadByUserIdAndBizType(String userId, String bizType, int page, int size) {
        Page<UserMessagePO> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<UserMessagePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserMessagePO::getUserId, userId)
                .eq(UserMessagePO::getIsRead, false)
                .eq(UserMessagePO::getIsDeleted, false);

        // 业务类型筛选逻辑：MEETING 包含 MEETING 和 REGISTRATION
        if ("MEETING".equals(bizType)) {
            wrapper.in(UserMessagePO::getBizType, "MEETING", "REGISTRATION");
        } else if ("SYSTEM".equals(bizType)) {
            wrapper.eq(UserMessagePO::getBizType, "SYSTEM");
        }

        wrapper.orderByDesc(UserMessagePO::getCreatedAt);

        IPage<UserMessagePO> resultPage = userMessageBaseMapper.selectPage(pageParam, wrapper);

        List<UserMessage> content = resultPage.getRecords().stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
        return new PageResult<>(content, resultPage.getTotal(), page, size);
    }

    @Override
    public long countUnreadByUserId(String userId) {
        return userMessageBaseMapper.countUnreadByUserId(userId);
    }

    @Override
    public void markAsRead(String messageId, String userId) {
        int updated = userMessageBaseMapper.markAsRead(messageId, userId, LocalDateTime.now());
        if (updated == 0) {
            logger.warn("标记消息已读失败: messageId={}, userId={}", messageId, userId);
        }
    }

    @Override
    public void markAllAsRead(String userId) {
        int updated = userMessageBaseMapper.markAllAsRead(userId, LocalDateTime.now());
        logger.info("用户全部消息已读: userId={}, updated={}", userId, updated);
    }

    @Override
    public void deleteById(String messageId, String userId) {
        int deleted = userMessageBaseMapper.deleteByMessageId(messageId, userId);
        if (deleted == 0) {
            logger.warn("删除消息失败: messageId={}, userId={}", messageId, userId);
        }
    }

    @Override
    public void deleteByIds(List<String> messageIds, String userId) {
        if (messageIds == null || messageIds.isEmpty()) {
            return;
        }
        for (String messageId : messageIds) {
            deleteById(messageId, userId);
        }
    }

    @Override
    public int deleteAllByUserId(String userId) {
        int deleted = userMessageBaseMapper.deleteAllByUserId(userId);
        logger.info("清空用户全部消息: userId={}, deleted={}", userId, deleted);
        return deleted;
    }

    @Override
    public int cleanupExpiredMessages(LocalDateTime beforeDate) {
        // 删除指定日期之前已读的消息（物理删除）
        LambdaQueryWrapper<UserMessagePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserMessagePO::getIsRead, true)
                .lt(UserMessagePO::getCreatedAt, beforeDate);

        int deleted = userMessageBaseMapper.delete(wrapper);
        logger.info("清理过期消息: beforeDate={}, deleted={}", beforeDate, deleted);
        return deleted;
    }

    @Override
    public List<String> findExpiredMessageIds(LocalDateTime beforeDate, int batchSize) {
        LambdaQueryWrapper<UserMessagePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserMessagePO::getIsRead, true)
                .lt(UserMessagePO::getCreatedAt, beforeDate)
                .select(UserMessagePO::getMessageId)
                .last("LIMIT " + batchSize);

        List<UserMessagePO> list = userMessageBaseMapper.selectList(wrapper);
        return list.stream()
                .map(UserMessagePO::getMessageId)
                .collect(Collectors.toList());
    }

    /**
     * 生成消息业务ID（格式：MSG + 时间戳 + 随机后缀）
     */
    private String generateMessageId() {
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "MSG" + (System.currentTimeMillis() % 1000000000000L) + suffix;
    }

    /**
     * 实体转PO
     */
    private UserMessagePO toPO(UserMessage entity) {
        if (entity == null) {
            return null;
        }
        UserMessagePO po = new UserMessagePO();
        po.setId(entity.getId());
        po.setMessageId(entity.getMessageId());
        po.setUserId(entity.getUserId());
        po.setMessageType(entity.getMessageType() != null ? entity.getMessageType().getCode() : null);
        po.setTitle(entity.getTitle());
        po.setContent(entity.getContent());
        po.setBizId(entity.getBizId());
        po.setBizType(entity.getBizType());
        if (entity.getExtraData() != null) {
            po.setExtraData(JSON.toJSONString(entity.getExtraData()));
        }
        po.setIsRead(entity.getIsRead());
        po.setReadTime(entity.getReadTime());
        po.setIsDeleted(entity.getIsDeleted());
        po.setCreatedAt(entity.getCreatedAt());
        return po;
    }

    /**
     * PO转实体
     */
    private UserMessage toEntity(UserMessagePO po) {
        if (po == null) {
            return null;
        }
        UserMessage entity = new UserMessage();
        entity.setId(po.getId());
        entity.setMessageId(po.getMessageId());
        entity.setUserId(po.getUserId());
        entity.setMessageType(UserMessage.MessageType.fromCode(po.getMessageType()));
        entity.setTitle(po.getTitle());
        entity.setContent(po.getContent());
        entity.setBizId(po.getBizId());
        entity.setBizType(po.getBizType());
        if (po.getExtraData() != null && !po.getExtraData().isEmpty()) {
            try {
                entity.setExtraData(JSON.parseObject(po.getExtraData(), Map.class));
            } catch (Exception e) {
                logger.warn("解析extraData失败: messageId={}, error={}", po.getMessageId(), e.getMessage());
            }
        }
        entity.setIsRead(po.getIsRead());
        entity.setReadTime(po.getReadTime());
        entity.setIsDeleted(po.getIsDeleted());
        entity.setCreatedAt(po.getCreatedAt());
        return entity;
    }
}
