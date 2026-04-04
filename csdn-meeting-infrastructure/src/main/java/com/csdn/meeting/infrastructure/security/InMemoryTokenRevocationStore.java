package com.csdn.meeting.infrastructure.security;

import cn.hutool.crypto.digest.DigestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 内存实现的 Token 撤销存储
 * 使用 ConcurrentHashMap 存储，支持 TTL 自动过期清理
 * 零外部依赖，适合单机部署
 */
@Slf4j
@Component
public class InMemoryTokenRevocationStore implements TokenRevocationStore {

    /**
     * 存储结构：key = SHA-256(token), value = 过期时间戳（毫秒）
     */
    private final Map<String, Long> revokedTokens = new ConcurrentHashMap<>();

    /**
     * 计算 Token 的哈希值（SHA-256）
     * 避免存储原始 JWT，同时保持唯一性
     *
     * @param token 原始 JWT Token
     * @return SHA-256 哈希值（小写 hex）
     */
    private String computeTokenHash(String token) {
        return DigestUtil.sha256Hex(token);
    }

    /**
     * 惰性清理：移除已过期的条目
     * 在操作前调用，避免内存无限增长
     */
    private void cleanupExpiredEntries() {
        long now = System.currentTimeMillis();
        int removedCount = 0;

        Iterator<Map.Entry<String, Long>> iterator = revokedTokens.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Long> entry = iterator.next();
            if (entry.getValue() < now) {
                iterator.remove();
                removedCount++;
            }
        }

        if (removedCount > 0) {
            log.debug("清理过期 Token 黑名单条目: {} 条", removedCount);
        }
    }

    @Override
    public void revokeToken(String token) {
        if (token == null || token.isEmpty()) {
            return;
        }

        // 惰性清理
        cleanupExpiredEntries();

        String tokenHash = computeTokenHash(token);

        // 从 Token 中解析过期时间
        Date expiryDate = extractExpiryDate(token);
        long expiryTime = expiryDate != null ? expiryDate.getTime() : System.currentTimeMillis() + 7200 * 1000;

        revokedTokens.put(tokenHash, expiryTime);
        log.info("Token 已加入黑名单: hash={}, 过期时间={}", tokenHash.substring(0, 8) + "...", expiryDate);
    }

    @Override
    public boolean isRevoked(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        // 惰性清理
        cleanupExpiredEntries();

        String tokenHash = computeTokenHash(token);
        Long expiryTime = revokedTokens.get(tokenHash);

        if (expiryTime == null) {
            return false;
        }

        // 检查是否已过期（如果是，顺便清理）
        if (expiryTime < System.currentTimeMillis()) {
            revokedTokens.remove(tokenHash);
            return false;
        }

        return true;
    }

    /**
     * 从 JWT Token 中提取过期时间
     * 使用 Hutool JWT 解析
     *
     * @param token JWT Token
     * @return 过期时间，解析失败返回 null
     */
    private Date extractExpiryDate(String token) {
        try {
            cn.hutool.jwt.JWT jwt = cn.hutool.jwt.JWTUtil.parseToken(token);
            Object expObj = jwt.getPayload("exp");
            if (expObj instanceof Number) {
                long expSeconds = ((Number) expObj).longValue();
                return new Date(expSeconds * 1000);
            }
        } catch (Exception e) {
            log.warn("无法从 Token 解析过期时间: {}", e.getMessage());
        }
        return null;
    }
}
