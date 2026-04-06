package com.csdn.meeting.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 用户消息实体（站内信）
 * 存储系统内部推送的消息，支持会议发布、报名审核等通知场景
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserMessage extends BaseEntity {

    /**
     * 消息业务ID
     */
    private String messageId;

    /**
     * 接收用户ID
     */
    private String userId;

    /**
     * 消息类型
     */
    private MessageType messageType;

    /**
     * 消息标题
     */
    private String title;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 关联业务ID（如会议ID）
     */
    private String bizId;

    /**
     * 业务类型
     */
    private String bizType;

    /**
     * 扩展数据
     */
    private Map<String, Object> extraData;

    /**
     * 是否已读
     */
    private Boolean isRead;

    /**
     * 阅读时间
     */
    private LocalDateTime readTime;

    /**
     * 是否删除（用户软删除）
     */
    private Boolean isDeleted;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 消息类型枚举
     */
    public enum MessageType {
        /**
         * 会议发布
         */
        MEETING_PUBLISH(1, "会议发布"),

        /**
         * 报名审核通过
         */
        REGISTRATION_APPROVED(2, "报名通过"),

        /**
         * 报名审核拒绝
         */
        REGISTRATION_REJECTED(3, "报名拒绝"),

        /**
         * 系统公告
         */
        SYSTEM_NOTICE(4, "系统公告"),

        /**
         * 服务更新
         */
        SYSTEM_UPDATE(5, "服务更新");

        private final int code;
        private final String desc;

        MessageType(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }

        public static MessageType fromCode(int code) {
            for (MessageType type : values()) {
                if (type.code == code) {
                    return type;
                }
            }
            return null;
        }
    }
}
