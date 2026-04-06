package com.csdn.meeting.infrastructure.client;

import com.csdn.meeting.domain.entity.UserMessage;
import com.csdn.meeting.domain.port.MessagePushPort;
import com.csdn.meeting.domain.repository.UserMessageRepository;
import com.csdn.meeting.infrastructure.websocket.WebSocketSessionManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 内部消息推送客户端
 * 实现MessagePushPort端口，将消息存储到数据库并推送给在线用户
 * 替代原CsdnMessagePushClient，不再调用CSDN消息中心
 * 支持WebSocket实时推送
 */
@Component
public class InternalMessagePushClient implements MessagePushPort {

    private static final Logger logger = LoggerFactory.getLogger(InternalMessagePushClient.class);

    private static final int MAX_BATCH_SIZE = 500;

    private final UserMessageRepository messageRepository;
    private final WebSocketSessionManager webSocketSessionManager;
    private final ObjectMapper objectMapper;

    public InternalMessagePushClient(UserMessageRepository messageRepository,
                                      WebSocketSessionManager webSocketSessionManager) {
        this.messageRepository = messageRepository;
        this.webSocketSessionManager = webSocketSessionManager;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void sendSiteMessage(String bizId, MessageType type, List<String> userIds,
                                 String title, String content, Map<String, Object> extra) {
        if (userIds == null || userIds.isEmpty()) {
            logger.warn("站内信发送：用户列表为空, bizId={}", bizId);
            return;
        }

        logger.info("[站内信推送] 开始发送: bizId={}, type={}, users={}, title={}",
                bizId, type, userIds.size(), title);

        // 分批处理，每批最多500人
        List<List<String>> batches = partition(userIds, MAX_BATCH_SIZE);
        int totalCount = 0;
        int successCount = 0;

        for (int i = 0; i < batches.size(); i++) {
            List<String> batch = batches.get(i);
            int batchNum = i + 1;
            int batchSize = batch.size();
            totalCount += batchSize;

            try {
                // 为每个用户创建消息实体
                List<UserMessage> messages = new ArrayList<>(batchSize);
                LocalDateTime now = LocalDateTime.now();

                for (String userId : batch) {
                    UserMessage message = createMessage(userId, type, title, content, bizId, extra, now);
                    messages.add(message);
                }

                // 批量保存
                messageRepository.saveBatch(messages);
                successCount += batchSize;

                logger.info("[站内信推送] 批次发送成功: bizId={}, batch={}/{}, users={}",
                        bizId, batchNum, batches.size(), batchSize);

            } catch (Exception e) {
                logger.error("[站内信推送] 批次发送失败: bizId={}, batch={}/{}, error={}",
                        bizId, batchNum, batches.size(), e.getMessage(), e);
                // 继续处理下一批，不阻断
            }
        }

        logger.info("[站内信推送] 发送完成: bizId={}, type={}, totalUsers={}, successCount={}",
                bizId, type, totalCount, successCount);

        // WebSocket实时推送：尝试推送给在线用户
        tryWebSocketPush(userIds, type, title, bizId);
    }

    /**
     * 尝试通过WebSocket推送给在线用户
     */
    private void tryWebSocketPush(List<String> userIds, MessageType type, String title, String bizId) {
        int onlineCount = 0;
        int pushCount = 0;
        String bizType = detectBizType(type);
        String messageId = generateMessageId();

        for (String userId : userIds) {
            // 检查用户是否在线
            if (webSocketSessionManager.isUserOnline(userId)) {
                onlineCount++;

                try {
                    // 查询用户未读消息数
                    long unreadCount = messageRepository.countUnreadByUserId(userId);

                    // 构建推送消息（JDK 1.8兼容）
                    Map<String, Object> pushMessage = new HashMap<>();
                    pushMessage.put("type", "NEW_MESSAGE");
                    pushMessage.put("messageId", messageId);
                    pushMessage.put("messageType", type.name());
                    pushMessage.put("bizType", bizType);
                    pushMessage.put("title", title);
                    pushMessage.put("bizId", bizId);
                    pushMessage.put("unreadCount", unreadCount);
                    pushMessage.put("timestamp", System.currentTimeMillis());

                    String jsonMessage = objectMapper.writeValueAsString(pushMessage);
                    boolean sent = webSocketSessionManager.sendMessageToUser(userId, jsonMessage);

                    if (sent) {
                        pushCount++;
                    }

                } catch (Exception e) {
                    logger.error("[站内信推送] WebSocket推送失败: userId={}", userId, e);
                }
            }
        }

        if (onlineCount > 0) {
            logger.info("[站内信推送] WebSocket推送: 在线用户={}, 推送成功={}", onlineCount, pushCount);
        }
    }

    /**
     * 创建消息实体
     */
    private UserMessage createMessage(String userId, MessageType type, String title, String content,
                                       String bizId, Map<String, Object> extra, LocalDateTime now) {
        UserMessage message = new UserMessage();
        message.setMessageId(generateMessageId());
        message.setUserId(userId);
        message.setMessageType(convertMessageType(type));
        message.setTitle(title);
        message.setContent(content);
        message.setBizId(bizId);
        message.setBizType(detectBizType(type));
        message.setExtraData(extra);
        message.setIsRead(false);
        message.setIsDeleted(false);
        message.setCreatedAt(now);
        return message;
    }

    /**
     * 转换消息类型
     */
    private UserMessage.MessageType convertMessageType(MessageType type) {
        switch (type) {
            case MEETING_PUBLISH:
                return UserMessage.MessageType.MEETING_PUBLISH;
            case REGISTRATION_APPROVED:
                return UserMessage.MessageType.REGISTRATION_APPROVED;
            case REGISTRATION_REJECTED:
                return UserMessage.MessageType.REGISTRATION_REJECTED;
            case SYSTEM_NOTICE:
                return UserMessage.MessageType.SYSTEM_NOTICE;
            case SYSTEM_UPDATE:
                return UserMessage.MessageType.SYSTEM_UPDATE;
            default:
                return UserMessage.MessageType.MEETING_PUBLISH;
        }
    }

    /**
     * 根据消息类型推断业务类型
     */
    private String detectBizType(MessageType type) {
        switch (type) {
            case MEETING_PUBLISH:
                return "MEETING";
            case REGISTRATION_APPROVED:
            case REGISTRATION_REJECTED:
                return "REGISTRATION";
            case SYSTEM_NOTICE:
            case SYSTEM_UPDATE:
                return "SYSTEM";
            default:
                return "MEETING";
        }
    }

    /**
     * 生成消息业务ID
     */
    private String generateMessageId() {
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "MSG" + (System.currentTimeMillis() % 1000000000000L) + suffix;
    }

    /**
     * 分批处理列表
     */
    private <T> List<List<T>> partition(List<T> list, int size) {
        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            partitions.add(new ArrayList<>(list.subList(i, Math.min(i + size, list.size()))));
        }
        return partitions;
    }
}
