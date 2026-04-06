package com.csdn.meeting.domain.entity;

import com.csdn.meeting.domain.valueobject.Industry;
import com.csdn.meeting.domain.valueobject.UserStatus;
import com.csdn.meeting.domain.valueobject.UserType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户实体 - 聚合根
 * 包含账号信息、个人资料、身份信息、职业信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {

    /**
     * 业务用户ID，对外暴露
     */
    private String userId;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 加密密码
     */
    private String password;

    /**
     * 用户类型
     */
    private UserType userType;

    /**
     * CSDN关联标识
     */
    private String csdnBindId;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像URL
     */
    private String avatarUrl;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 邮箱是否已验证
     */
    private Boolean emailVerified;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 公司
     */
    private String company;

    /**
     * 职位
     */
    private String jobTitle;

    /**
     * 行业
     */
    private Industry industry;

    /**
     * 账号状态
     */
    private UserStatus status;

    /**
     * 是否同意用户协议
     */
    private Boolean agreementAccepted;

    /**
     * 是否同意隐私政策
     */
    private Boolean privacyAccepted;

    /**
     * 注册时间
     */
    private LocalDateTime createdAt;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginAt;

    /**
     * 判断用户是否为正常状态
     */
    public boolean isActive() {
        return status == UserStatus.NORMAL;
    }

    /**
     * 判断是否已完成协议同意（同时同意用户协议和隐私政策）
     */
    public boolean hasAcceptedAgreements() {
        return Boolean.TRUE.equals(agreementAccepted) && Boolean.TRUE.equals(privacyAccepted);
    }

    /**
     * 判断是否为普通用户
     */
    public boolean isNormalUser() {
        return userType == UserType.USER;
    }

    /**
     * 更新最后登录时间
     */
    public void updateLastLoginTime() {
        this.lastLoginAt = LocalDateTime.now();
    }

    /**
     * 冻结账号
     */
    public void freeze() {
        this.status = UserStatus.FROZEN;
    }

    /**
     * 解冻账号
     */
    public void unfreeze() {
        this.status = UserStatus.NORMAL;
    }

    /**
     * 接受协议（注册时必须调用）
     */
    public void acceptAgreements() {
        this.agreementAccepted = true;
        this.privacyAccepted = true;
    }

    /**
     * 验证邮箱
     */
    public void verifyEmail() {
        this.emailVerified = true;
    }

    /**
     * 注销账号
     */
    public void cancel() {
        this.status = UserStatus.CANCELLED;
    }

    /**
     * 注销账号并给手机号加前缀，释放手机号供重新注册
     *
     * @param prefixedMobile 带前缀的手机号（格式：CANCELLED_原手机号_时间戳）
     */
    public void cancelWithMobilePrefix(String prefixedMobile) {
        this.status = UserStatus.CANCELLED;
        this.mobile = prefixedMobile;
    }

    /**
     * 判断账号是否已注销
     */
    public boolean isCancelled() {
        return status == UserStatus.CANCELLED;
    }
}
