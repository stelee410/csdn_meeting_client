package com.csdn.meeting.infrastructure.client;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.csdn.meeting.infrastructure.client.dto.CsdnMessageRequest;
import com.csdn.meeting.infrastructure.client.dto.CsdnMessageResponse;
import com.csdn.meeting.infrastructure.config.CsdnMessageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CSDN消息推送客户端
 * 支持IM站内信和APP推送两种消息类型
 * 含完整日志记录功能
 */
@Component
public class CsdnMessagePushClient {

    private static final Logger logger = LoggerFactory.getLogger(CsdnMessagePushClient.class);

    private static final String IM_SEND_PATH = "/im/open/v1/send";
    private static final String PUSH_SEND_PATH = "/push/open/v1/send";

    private final CsdnMessageProperties properties;

    public CsdnMessagePushClient(CsdnMessageProperties properties) {
        this.properties = properties;
    }

    /**
     * 发送IM站内信
     *
     * @param bizId        业务ID（如会议ID）
     * @param templateCode 模板编码
     * @param userIds      接收用户ID列表
     * @param params       模板变量
     * @return 发送结果
     */
    public CsdnMessageResponse sendImMessage(String bizId, String templateCode, List<String> userIds, Map<String, String> params) {
        CsdnMessageRequest request = new CsdnMessageRequest(templateCode, userIds, params);
        return doSend(bizId, "IM", IM_SEND_PATH, request);
    }

    /**
     * 发送APP推送
     *
     * @param bizId        业务ID（如会议ID）
     * @param templateCode 模板编码
     * @param userIds      接收用户ID列表
     * @param params       模板变量
     * @return 发送结果
     */
    public CsdnMessageResponse sendPushNotification(String bizId, String templateCode, List<String> userIds, Map<String, String> params) {
        CsdnMessageRequest request = new CsdnMessageRequest(templateCode, userIds, params);
        return doSend(bizId, "PUSH", PUSH_SEND_PATH, request);
    }

    /**
     * 使用会议发布模板发送IM站内信
     *
     * @param bizId        业务ID（会议ID）
     * @param userIds      接收用户ID列表
     * @param meetingTitle 会议标题
     * @param meetingId    会议ID
     * @param tagId        标签ID
     * @param tagName      标签名称
     * @return 发送结果
     */
    public CsdnMessageResponse sendMeetingPublishIm(String bizId, List<String> userIds, 
                                                       String meetingTitle, String meetingId,
                                                       Long tagId, String tagName) {
        String templateCode = properties.getTemplates().getMeetingPublishIm();
        Map<String, String> params = new HashMap<>();
        params.put("meetingTitle", meetingTitle);
        params.put("meetingId", meetingId);
        params.put("tag", tagName);
        params.put("tagId", String.valueOf(tagId));
        return sendImMessage(bizId, templateCode, userIds, params);
    }

    /**
     * 使用会议发布模板发送APP推送
     *
     * @param bizId        业务ID（会议ID）
     * @param userIds      接收用户ID列表
     * @param meetingTitle 会议标题
     * @param meetingId    会议ID
     * @param tagId        标签ID
     * @param tagName      标签名称
     * @return 发送结果
     */
    public CsdnMessageResponse sendMeetingPublishPush(String bizId, List<String> userIds, 
                                                       String meetingTitle, String meetingId,
                                                       Long tagId, String tagName) {
        String templateCode = properties.getTemplates().getMeetingPublishPush();
        Map<String, String> params = new HashMap<>();
        params.put("meetingTitle", meetingTitle);
        params.put("meetingId", meetingId);
        params.put("tag", tagName);
        params.put("tagId", String.valueOf(tagId));
        return sendPushNotification(bizId, templateCode, userIds, params);
    }

    /**
     * 执行发送
     *
     * @param bizId    业务ID
     * @param msgType  消息类型(IM/PUSH)
     * @param path     接口路径
     * @param request  请求对象
     * @return 响应结果
     */
    private CsdnMessageResponse doSend(String bizId, String msgType, String path, CsdnMessageRequest request) {
        String url = properties.getBaseUrl() + path;
        String jsonBody = JSONUtil.toJsonStr(request);
        int userCount = request.getToUsers() != null ? request.getToUsers().size() : 0;

        // 记录发送前日志
        logger.info("[CSDN推送] 发送中 - bizId={}, type={}, template={}, users={}, url={}",
                bizId, msgType, request.getTemplateCode(), userCount, url);
        logger.debug("[CSDN推送] 请求体 - bizId={}, body={}", bizId, jsonBody);

        try {
            // 生成签名参数
            String timestamp = CsdnMessageSigner.generateTimestamp();
            String nonce = CsdnMessageSigner.generateNonce();
            String signature = CsdnMessageSigner.sign(
                    properties.getAppKey(),
                    properties.getAppSecret(),
                    timestamp,
                    nonce,
                    jsonBody
            );

            // 发送HTTP请求
            HttpResponse response = HttpRequest.post(url)
                    .header("Content-Type", "application/json")
                    .header("X-App-Key", properties.getAppKey())
                    .header("X-Timestamp", timestamp)
                    .header("X-Nonce", nonce)
                    .header("X-Signature", signature)
                    .body(jsonBody)
                    .timeout(30000)
                    .execute();

            // 解析响应
            String responseBody = response.body();
            CsdnMessageResponse result = JSONUtil.toBean(responseBody, CsdnMessageResponse.class);

            // 记录响应日志
            if (result.isSuccess()) {
                logger.info("[CSDN推送] 发送成功 - bizId={}, type={}, status={}, code={}",
                        bizId, msgType, result.getStatus(), result.getCode());
            } else {
                logger.warn("[CSDN推送] 发送失败 - bizId={}, type={}, status={}, code={}, message={}",
                        bizId, msgType, result.getStatus(), result.getCode(), result.getMessage());
            }
            logger.debug("[CSDN推送] 响应体 - bizId={}, body={}", bizId, responseBody);

            return result;

        } catch (Exception e) {
            logger.error("[CSDN推送] 发送异常 - bizId={}, type={}, error={}", bizId, msgType, e.getMessage(), e);
            
            // 构造失败响应
            CsdnMessageResponse errorResponse = new CsdnMessageResponse();
            errorResponse.setStatus(false);
            errorResponse.setCode("500");
            errorResponse.setMessage("发送异常: " + e.getMessage());
            return errorResponse;
        }
    }
}
