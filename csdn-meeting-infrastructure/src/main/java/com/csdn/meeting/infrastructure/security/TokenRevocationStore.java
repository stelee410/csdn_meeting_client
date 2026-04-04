package com.csdn.meeting.infrastructure.security;

/**
 * Token 撤销存储接口
 * 用于管理已登出（撤销）的 JWT Token 黑名单
 */
public interface TokenRevocationStore {

    /**
     * 撤销指定 Token
     * 将 Token 加入黑名单，TTL 为 Token 剩余有效期
     *
     * @param token 原始 JWT Token
     */
    void revokeToken(String token);

    /**
     * 检查 Token 是否已被撤销
     *
     * @param token 原始 JWT Token
     * @return true 表示 Token 已被撤销（在黑名单中）
     */
    boolean isRevoked(String token);
}
