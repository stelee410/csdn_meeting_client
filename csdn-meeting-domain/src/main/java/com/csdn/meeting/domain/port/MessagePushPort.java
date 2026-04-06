package com.csdn.meeting.domain.port;

import java.util.List;
import java.util.Map;

/**
 * 消息推送端口
 * 定义系统内部消息推送的标准接口，支持站内信
 * Infrastructure层提供具体实现
 */
public interface MessagePushPort {

    /**
     * 发送站内信（内部存储到数据库）
     * 为每个接收用户创建一条消息记录，供前端拉取
     *
     * @param bizId     业务ID（如会议ID，用于追踪）
     * @param type      消息类型
     * @param userIds   接收用户ID列表
     * @param title     消息标题
     * @param content   消息内容
     * @param extra     扩展数据（如会议标题、标签名等，用于前端展示）
     */
    void sendSiteMessage(String bizId, MessageType type, List<String> userIds,
                         String title, String content, Map<String, Object> extra);

    /**
     * 消息类型枚举
     */
    enum MessageType {
        /**
         * 会议发布通知
         */
        MEETING_PUBLISH,

        /**
         * 报名审核通过
         */
        REGISTRATION_APPROVED,

        /**
         * 报名审核拒绝
         */
        REGISTRATION_REJECTED,

        /**
         * 会议审核通过（通知创建者）
         */
        MEETING_AUDIT_APPROVED,

        /**
         * 会议审核拒绝（通知创建者）
         */
        MEETING_AUDIT_REJECTED,

        /**
         * 会议强制下架（通知创建者）
         */
        MEETING_TAKEDOWN,

        /**
         * 系统公告
         */
        SYSTEM_NOTICE,

        /**
         * 服务更新
         */
        SYSTEM_UPDATE
    }
}
