package com.csdn.meeting.infrastructure.client;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;

/**
 * CSDN消息推送签名工具
 * 按照接口文档要求实现HMAC-SHA256签名算法
 */
public class CsdnMessageSigner {

    /**
     * 生成随机字符串(Nonce)
     *
     * @return 16位随机字符串
     */
    public static String generateNonce() {
        return StrUtil.uuid().substring(0, 16);
    }

    /**
     * 生成时间戳(毫秒)
     *
     * @return 当前时间戳
     */
    public static String generateTimestamp() {
        return String.valueOf(System.currentTimeMillis());
    }

    /**
     * 计算签名
     * 签名算法：Base64(HMAC-SHA256(AppSecret, Data))
     * 其中 Data = AppKey + Timestamp + Nonce + Body
     *
     * @param appKey    应用Key
     * @param appSecret 应用密钥
     * @param timestamp 时间戳
     * @param nonce     随机字符串
     * @param body      请求体原始JSON字符串
     * @return Base64编码的签名
     */
    public static String sign(String appKey, String appSecret, String timestamp, String nonce, String body) {
        // 按文档要求拼接数据：AppKey + Timestamp + Nonce + Body
        String data = appKey + timestamp + nonce + body;
        
        // 使用HMAC-SHA256计算签名
        byte[] signatureBytes = SecureUtil.hmacSha256(appSecret.getBytes()).digest(data.getBytes());
        
        // Base64编码
        return cn.hutool.core.codec.Base64.encode(signatureBytes);
    }
}
