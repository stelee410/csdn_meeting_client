package com.csdn.meeting.interfaces.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 消息视图对象
 * 用于向前端展示用户站内信消息
 */
@Schema(description = "消息视图对象")
public class MessageVO {

    @Schema(description = "消息业务ID", example = "MSG202603290001")
    private String messageId;

    @Schema(description = "消息类型: 1-会议发布 2-报名通过 3-报名拒绝 4-系统公告 5-服务更新", example = "1")
    private Integer messageType;

    @Schema(description = "消息类型描述", example = "会议发布")
    private String messageTypeDesc;

    @Schema(description = "消息标题", example = "【新会议】AI技术峰会")
    private String title;

    @Schema(description = "消息内容", example = "您关注的「AI人工智能」领域有新会议：AI技术峰会")
    private String content;

    @Schema(description = "关联业务ID（如会议ID）", example = "MT202603290001")
    private String bizId;

    @Schema(description = "业务类型: MEETING(会议发布)/REGISTRATION(报名通知)/SYSTEM(系统消息),MEETING/REGISTRATION为会议通知大类、SYSTEM为系统通知", example = "MEETING")
    private String bizType;

    @Schema(description = "扩展数据，包含会议标题、标签名等")
    private Map<String, Object> extraData;

    @Schema(description = "是否已读", example = "false")
    private Boolean isRead;

    @Schema(description = "阅读时间", example = "2026-03-29T10:30:00")
    private LocalDateTime readTime;

    @Schema(description = "创建时间", example = "2026-03-29T10:00:00")
    private LocalDateTime createdAt;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

    public String getMessageTypeDesc() {
        return messageTypeDesc;
    }

    public void setMessageTypeDesc(String messageTypeDesc) {
        this.messageTypeDesc = messageTypeDesc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    public Map<String, Object> getExtraData() {
        return extraData;
    }

    public void setExtraData(Map<String, Object> extraData) {
        this.extraData = extraData;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public LocalDateTime getReadTime() {
        return readTime;
    }

    public void setReadTime(LocalDateTime readTime) {
        this.readTime = readTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
