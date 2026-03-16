package com.csdn.meeting.infrastructure.client;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

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

    public static String sign2(String appKey, String appSecret, String timestamp, String nonce, String body) throws NoSuchAlgorithmException, InvalidKeyException {

        // 2. 拼接数据
        String data = appKey + timestamp + nonce + body;

        // 3. 计算签名
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(appSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        String signature = Base64.getEncoder().encodeToString(sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8)));

        System.out.println("Signature: " + signature);
        System.out.println("Body to send: " + body);
        return signature;
    }

    public static String sign3(String appKey, String appSecret, String timestamp, String nonce, String body) {
        try {
            String data = appKey + timestamp + nonce + body;
            if(appSecret==null){
                appSecret="";
            }
            Mac sha256HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(appSecret.getBytes(), "HmacSHA256");
            sha256HMAC.init(secretKey);
            return Base64.getEncoder().encodeToString(sha256HMAC.doFinal(data.getBytes()));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Error generating signature", e);
        }
    }

    public static void main(String[] args) {
        String appKey = "Conference_New_Notice";
        String secret = "086e138b358948b8a30a8b29313bb45f";
        String timestamp = "1773579693493";
        String nonce = "6b0b0982-34b6-4e";

        String requestBody ="{\"templateCode\":\"New_Notice_IM\",\"toUsers\":[\"qq_42400267\"],\"params\":{\"tag\":\"测试ing\"}}";

        String dataToSign = appKey + timestamp + nonce + requestBody;
        String signature = sign(secret, dataToSign);
        System.out.println(signature);


    }
    static String HMAC_SHA256 = "HmacSHA256";
    public static String sign(String secret, String data) {
        try {
            if(secret==null){
                secret="";
            }
            Mac sha256HMAC = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), HMAC_SHA256);
            sha256HMAC.init(secretKey);
            return Base64.getEncoder().encodeToString(sha256HMAC.doFinal(data.getBytes()));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Error generating signature", e);
        }
    }
}
