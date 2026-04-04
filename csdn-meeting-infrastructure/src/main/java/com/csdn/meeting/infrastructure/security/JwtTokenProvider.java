package com.csdn.meeting.infrastructure.security;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT令牌提供者
 * 负责生成、解析和验证JWT令牌
 * 使用Hutool JWT工具
 */
@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret:csdn_meeting_jwt_secret_key_2026}")
    private String jwtSecret;

    @Value("${jwt.expiration:7200}")
    private long jwtExpirationInSeconds;

    private byte[] getSigningKey() {
        return jwtSecret.getBytes(StandardCharsets.UTF_8);
    }

    private JWTSigner getSigner() {
        return JWTSignerUtil.hs256(getSigningKey());
    }

    /**
     * 生成JWT令牌
     *
     * @param userId 用户ID
     * @return JWT令牌
     */
    public String generateToken(String userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInSeconds * 1000);

        return JWT.create()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiresAt(expiryDate)
                .sign(getSigner());
    }

    /**
     * 从JWT令牌中提取用户ID
     *
     * @param token JWT令牌
     * @return 用户ID
     */
    public String getUserIdFromToken(String token) {
        JWT jwt = JWTUtil.parseToken(token);
        return (String) jwt.getPayload("sub");
    }

    /**
     * 验证JWT令牌是否有效
     *
     * @param token JWT令牌
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            return JWTUtil.verify(token, getSigner());
        } catch (Exception ex) {
            log.error("JWT验证失败: {}", ex.getMessage());
            return false;
        }
    }

    /**
     * 获取令牌过期时间（秒）
     */
    public long getExpirationSeconds() {
        return jwtExpirationInSeconds;
    }

    /**
     * 从 JWT 令牌中提取过期时间（exp）
     *
     * @param token JWT令牌
     * @return 过期时间（毫秒时间戳），解析失败返回 null
     */
    public Long getExpirationTime(String token) {
        try {
            JWT jwt = JWTUtil.parseToken(token);
            Object expObj = jwt.getPayload("exp");
            if (expObj instanceof Number) {
                long expSeconds = ((Number) expObj).longValue();
                return expSeconds * 1000;
            }
        } catch (Exception e) {
            log.warn("无法从 Token 解析过期时间: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 计算 Token 剩余有效时间（秒）
     * 用于黑名单 TTL 计算
     *
     * @param token JWT令牌
     * @return 剩余秒数，若已过期或解析失败返回 0
     */
    public long getRemainingSeconds(String token) {
        Long expTime = getExpirationTime(token);
        if (expTime == null) {
            return 0;
        }
        long remaining = (expTime - System.currentTimeMillis()) / 1000;
        return Math.max(0, remaining);
    }
}
