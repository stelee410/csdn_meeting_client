package com.csdn.meeting.interfaces.controller;

import com.csdn.meeting.application.dto.*;
import com.csdn.meeting.application.service.UserAuthAppService;
import com.csdn.meeting.application.service.VerificationCodeAppService;
import com.csdn.meeting.interfaces.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 用户认证控制器
 * 处理注册、登录、验证码发送等接口
 */
@Slf4j
@Tag(name = "用户认证接口")
@RestController
@RequestMapping("/api/auth")
public class UserAuthController {

    private final UserAuthAppService userAuthAppService;
    private final VerificationCodeAppService verificationCodeAppService;

    public UserAuthController(UserAuthAppService userAuthAppService,
                              VerificationCodeAppService verificationCodeAppService) {
        this.userAuthAppService = userAuthAppService;
        this.verificationCodeAppService = verificationCodeAppService;
    }

    @Operation(summary = "发送短信验证码", description = "发送短信验证码，用于注册或登录")
    @PostMapping("/verification-code/sms")
    public ResponseEntity<ApiResponse<Void>> sendSmsCode(@Valid @RequestBody VerificationCodeSendCommand command) {
        // 强制设置类型为SMS
        command.setType("SMS");
        verificationCodeAppService.sendVerificationCode(command);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "发送邮箱验证码", description = "发送邮箱验证码，用于注册时邮箱验证")
    @PostMapping("/verification-code/email")
    public ResponseEntity<ApiResponse<Void>> sendEmailCode(@Valid @RequestBody VerificationCodeSendCommand command) {
        // 强制设置类型为EMAIL
        command.setType("EMAIL");
        verificationCodeAppService.sendVerificationCode(command);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "用户注册", description = "表单注册，需手机号+验证码+密码+协议同意")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<LoginResultDTO>> register(@Valid @RequestBody UserRegisterCommand command) {
        LoginResultDTO result = userAuthAppService.register(command);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @Operation(summary = "密码登录", description = "使用手机号+密码登录")
    @PostMapping("/login/password")
    public ResponseEntity<ApiResponse<LoginResultDTO>> loginByPassword(@Valid @RequestBody UserLoginPasswordCommand command) {
        LoginResultDTO result = userAuthAppService.loginByPassword(command);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @Operation(summary = "短信验证码登录", description = "使用手机号+短信验证码登录")
    @PostMapping("/login/sms")
    public ResponseEntity<ApiResponse<LoginResultDTO>> loginBySms(@Valid @RequestBody UserLoginSmsCommand command) {
        LoginResultDTO result = userAuthAppService.loginBySms(command);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @Operation(summary = "CSDN扫码回调", description = "CSDN App扫码授权后的回调处理，未注册则自动创建账号")
    @PostMapping("/csdn/callback")
    public ResponseEntity<ApiResponse<LoginResultDTO>> csdnAuthCallback(@Valid @RequestBody CsdnAuthCallbackCommand command) {
        LoginResultDTO result = userAuthAppService.handleCsdnAuthCallback(command);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @Operation(summary = "退出登录", description = "退出登录（前端清除token即可，服务端记录可选）")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        // 可选：将token加入黑名单（如果需要实现token失效机制）
        log.info("用户退出登录");
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
