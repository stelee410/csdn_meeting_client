package com.csdn.meeting.interfaces.controller;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.csdn.meeting.infrastructure.client.CsdnMessagePushClient;
import com.csdn.meeting.infrastructure.client.dto.CsdnMessageResponse;
import com.csdn.meeting.interfaces.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

/**
 * 消息推送测试控制器
 * 用于测试CSDN消息推送接口（IM站内信和APP推送）
 */
@Slf4j
@Tag(name = "消息推送测试接口")
@RestController
@RequestMapping("/api/test/message-push")
public class MessagePushTestController {

    private final CsdnMessagePushClient messagePushClient;

    public MessagePushTestController(CsdnMessagePushClient messagePushClient) {
        this.messagePushClient = messagePushClient;
    }

    /**
     * 测试IM站内信推送
     */
    @Operation(
            summary = "测试IM站内信推送",
            description = "发送IM站内信到指定用户列表，用于测试消息模板和接口连通性"
    )
    @PostMapping("/im")
    public ResponseEntity<ApiResponse<PushTestResult>> sendImMessage(
            @Valid @RequestBody PushTestRequest request) {

        log.info("[测试IM推送] 开始发送: bizId={}, users={}, template={}",
                request.getBizId(), request.getUserIds().size(), request.getTemplateCode());

        long startTime = System.currentTimeMillis();
        CsdnMessageResponse response = messagePushClient.sendImMessage(
                request.getBizId(),
                request.getTemplateCode(),
                request.getUserIds(),
                request.getParams()
        );
        long costTime = System.currentTimeMillis() - startTime;

        PushTestResult result = new PushTestResult();
        result.setSuccess(response.isSuccess());
        result.setCode(response.getCode());
        result.setMessage(response.getMessage());
        result.setUserCount(request.getUserIds().size());
        result.setCostTimeMs(costTime);
        result.setChannel("IM");

        if (response.isSuccess()) {
            log.info("[测试IM推送] 发送成功: bizId={}, cost={}ms", request.getBizId(), costTime);
            return ResponseEntity.ok(ApiResponse.success(result));
        } else {
            log.warn("[测试IM推送] 发送失败: bizId={}, code={}, message={}",
                    request.getBizId(), response.getCode(), response.getMessage());
            return ResponseEntity.ok(ApiResponse.error(500, "IM推送失败: " + response.getMessage(), JSONUtil.toJsonStr(result)));
        }
    }

    /**
     * 测试APP Push推送
     */
    @Operation(
            summary = "测试APP Push推送",
            description = "发送APP Push到指定用户列表，用于测试消息模板和接口连通性"
    )
    @PostMapping("/push")
    public ResponseEntity<ApiResponse<PushTestResult>> sendPushNotification(
            @Valid @RequestBody PushTestRequest request) {

        log.info("[测试PUSH推送] 开始发送: bizId={}, users={}, template={}",
                request.getBizId(), request.getUserIds().size(), request.getTemplateCode());

        long startTime = System.currentTimeMillis();
        CsdnMessageResponse response = messagePushClient.sendPushNotification(
                request.getBizId(),
                request.getTemplateCode(),
                request.getUserIds(),
                request.getParams()
        );
        long costTime = System.currentTimeMillis() - startTime;

        PushTestResult result = new PushTestResult();
        result.setSuccess(response.isSuccess());
        result.setCode(response.getCode());
        result.setMessage(response.getMessage());
        result.setUserCount(request.getUserIds().size());
        result.setCostTimeMs(costTime);
        result.setChannel("PUSH");

        if (response.isSuccess()) {
            log.info("[测试PUSH推送] 发送成功: bizId={}, cost={}ms", request.getBizId(), costTime);
            return ResponseEntity.ok(ApiResponse.success(result));
        } else {
            log.warn("[测试PUSH推送] 发送失败: bizId={}, code={}, message={}",
                    request.getBizId(), response.getCode(), response.getMessage());
            return ResponseEntity.ok(ApiResponse.error(500, "PUSH推送失败: " + response.getMessage() , JSONUtil.toJsonStr(result)));
        }
    }

    /**
     * 同时测试IM和PUSH推送
     */
    @Operation(
            summary = "同时测试IM和PUSH推送",
            description = "同时发送IM站内信和APP Push到指定用户列表，用于全面测试消息推送功能"
    )
    @PostMapping("/both")
    public ResponseEntity<ApiResponse<BothPushTestResult>> sendBoth(
            @Valid @RequestBody PushTestRequest request) {

        log.info("[测试双通道推送] 开始发送: bizId={}, users={}",
                request.getBizId(), request.getUserIds().size());

        BothPushTestResult result = new BothPushTestResult();
        result.setBizId(request.getBizId());
        result.setUserCount(request.getUserIds().size());

        // 发送IM
        long imStartTime = System.currentTimeMillis();
        CsdnMessageResponse imResponse = messagePushClient.sendImMessage(
                request.getBizId(),
                request.getTemplateCode(),
                request.getUserIds(),
                request.getParams()
        );
        result.setImCostTimeMs(System.currentTimeMillis() - imStartTime);
        result.setImSuccess(imResponse.isSuccess());
        result.setImCode(imResponse.getCode());
        result.setImMessage(imResponse.getMessage());

        // 发送PUSH
        long pushStartTime = System.currentTimeMillis();
        CsdnMessageResponse pushResponse = messagePushClient.sendPushNotification(
                request.getBizId(),
                request.getTemplateCode(),
                request.getUserIds(),
                request.getParams()
        );
        result.setPushCostTimeMs(System.currentTimeMillis() - pushStartTime);
        result.setPushSuccess(pushResponse.isSuccess());
        result.setPushCode(pushResponse.getCode());
        result.setPushMessage(pushResponse.getMessage());

        boolean allSuccess = imResponse.isSuccess() && pushResponse.isSuccess();

        log.info("[测试双通道推送] 发送完成: bizId={}, IM={}({}), PUSH={}({})",
                request.getBizId(),
                imResponse.isSuccess() ? "成功" : "失败", imResponse.getCode(),
                pushResponse.isSuccess() ? "成功" : "失败", pushResponse.getCode());

        if (allSuccess) {
            return ResponseEntity.ok(ApiResponse.success(result));
        } else {
            String errorMsg = "推送部分失败: " +
                    (imResponse.isSuccess() ? "" : "IM[" + imResponse.getMessage() + "] ") +
                    (pushResponse.isSuccess() ? "" : "PUSH[" + pushResponse.getMessage() + "]");
            return ResponseEntity.ok(ApiResponse.error(500, errorMsg.trim(), JSONUtil.toJsonStr(result)));
        }
    }

    /**
     * 使用会议发布模板测试推送（推荐）
     */
    @Operation(
            summary = "使用会议发布模板测试推送",
            description = "使用配置文件中配置的会议发布模板，同时发送IM和PUSH，模拟真实的会议发布场景"
    )
    @PostMapping("/meeting-publish")
    public ResponseEntity<ApiResponse<BothPushTestResult>> testMeetingPublishPush(
            @Valid @RequestBody MeetingPublishTestRequest request) {

        log.info("[测试会议发布推送] 开始发送: meetingId={}, users={}, tag={}",
                request.getMeetingId(), request.getUserIds().size(), request.getTagName());

        BothPushTestResult result = new BothPushTestResult();
        result.setBizId(request.getMeetingId());
        result.setUserCount(request.getUserIds().size());

        // 发送IM
        long imStartTime = System.currentTimeMillis();
        CsdnMessageResponse imResponse = messagePushClient.sendMeetingPublishIm(
                request.getMeetingId(),
                request.getUserIds(),
                request.getMeetingTitle(),
                request.getMeetingId(),
                request.getTagId(),
                request.getTagName()
        );
        result.setImCostTimeMs(System.currentTimeMillis() - imStartTime);
        result.setImSuccess(imResponse.isSuccess());
        result.setImCode(imResponse.getCode());
        result.setImMessage(imResponse.getMessage());

        // 发送PUSH
        long pushStartTime = System.currentTimeMillis();
        CsdnMessageResponse pushResponse = messagePushClient.sendMeetingPublishPush(
                request.getMeetingId(),
                request.getUserIds(),
                request.getMeetingTitle(),
                request.getMeetingId(),
                request.getTagId(),
                request.getTagName()
        );
        result.setPushCostTimeMs(System.currentTimeMillis() - pushStartTime);
        result.setPushSuccess(pushResponse.isSuccess());
        result.setPushCode(pushResponse.getCode());
        result.setPushMessage(pushResponse.getMessage());

        boolean allSuccess = imResponse.isSuccess() && pushResponse.isSuccess();

        log.info("[测试会议发布推送] 发送完成: meetingId={}, IM={}({}), PUSH={}({})",
                request.getMeetingId(),
                imResponse.isSuccess() ? "成功" : "失败", imResponse.getCode(),
                pushResponse.isSuccess() ? "成功" : "失败", pushResponse.getCode());

        if (allSuccess) {
            return ResponseEntity.ok(ApiResponse.success(result));
        } else {
            String errorMsg = "推送部分失败: " +
                    (imResponse.isSuccess() ? "" : "IM[" + imResponse.getMessage() + "] ") +
                    (pushResponse.isSuccess() ? "" : "PUSH[" + pushResponse.getMessage() + "]");
            return ResponseEntity.ok(ApiResponse.error(500, errorMsg.trim(), JSONUtil.toJsonStr(result)));
        }
    }

    // ==================== DTO ====================

    /**
     * 通用推送测试请求
     */
    @Data
    public static class PushTestRequest {

        @Parameter(description = "业务ID（如会议ID）", required = true, example = "meeting_123")
        @NotBlank(message = "业务ID不能为空")
        private String bizId;

        @Parameter(description = "模板编码", required = true, example = "New_Notice_IM")
        @NotBlank(message = "模板编码不能为空")
        private String templateCode;

        @Parameter(description = "接收用户ID列表（单次最多1000人）", required = true)
        @NotEmpty(message = "用户列表不能为空")
        private List<String> userIds;

        @Parameter(description = "模板变量参数，用于替换模板中的占位符", example = "{\"meetingTitle\":\"测试会议\",\"tag\":\"AI\"}")
        private Map<String, String> params;
    }

    /**
     * 会议发布测试请求
     */
    @Data
    public static class MeetingPublishTestRequest {

        @Parameter(description = "会议ID", required = true, example = "meeting_123")
        @NotBlank(message = "会议ID不能为空")
        private String meetingId;

        @Parameter(description = "会议标题", required = true, example = "2025 AI开发者大会")
        @NotBlank(message = "会议标题不能为空")
        private String meetingTitle;

        @Parameter(description = "标签ID", required = true, example = "1")
        private Long tagId;

        @Parameter(description = "标签名称", required = true, example = "人工智能")
        @NotBlank(message = "标签名称不能为空")
        private String tagName;

        @Parameter(description = "接收用户ID列表（单次最多1000人）", required = true)
        @NotEmpty(message = "用户列表不能为空")
        private List<String> userIds;
    }

    /**
     * 单通道推送测试结果
     */
    @Data
    public static class PushTestResult {

        @Parameter(description = "是否成功")
        private boolean success;

        @Parameter(description = "响应状态码")
        private String code;

        @Parameter(description = "响应消息")
        private String message;

        @Parameter(description = "发送用户数")
        private int userCount;

        @Parameter(description = "耗时（毫秒）")
        private long costTimeMs;

        @Parameter(description = "推送通道（IM/PUSH）")
        private String channel;
    }

    /**
     * 双通道推送测试结果
     */
    @Data
    public static class BothPushTestResult {

        @Parameter(description = "业务ID")
        private String bizId;

        @Parameter(description = "发送用户数")
        private int userCount;

        // IM结果
        @Parameter(description = "IM是否成功")
        private boolean imSuccess;

        @Parameter(description = "IM响应码")
        private String imCode;

        @Parameter(description = "IM响应消息")
        private String imMessage;

        @Parameter(description = "IM耗时（毫秒）")
        private long imCostTimeMs;

        // PUSH结果
        @Parameter(description = "PUSH是否成功")
        private boolean pushSuccess;

        @Parameter(description = "PUSH响应码")
        private String pushCode;

        @Parameter(description = "PUSH响应消息")
        private String pushMessage;

        @Parameter(description = "PUSH耗时（毫秒）")
        private long pushCostTimeMs;
    }
}
