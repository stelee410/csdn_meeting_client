package com.csdn.meeting.interfaces.controller;

import com.csdn.meeting.application.dto.*;
import com.csdn.meeting.application.service.CsdnQrCodeUseCase;
import com.csdn.meeting.application.service.UserAuthAppService;
import com.csdn.meeting.application.service.VerificationCodeAppService;
import com.csdn.meeting.infrastructure.security.JwtTokenProvider;
import com.csdn.meeting.infrastructure.security.TokenRevocationStore;
import com.csdn.meeting.interfaces.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 用户认证控制器
 * 处理注册、登录、验证码发送、CSDN扫码等接口
 * @author 13786
 */
@Slf4j
@Tag(name = "用户认证接口")
@RestController
@RequestMapping("/api/auth")
public class UserAuthController {

    private final UserAuthAppService userAuthAppService;
    private final VerificationCodeAppService verificationCodeAppService;
    private final CsdnQrCodeUseCase csdnQrCodeUseCase;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenRevocationStore tokenRevocationStore;

    public UserAuthController(UserAuthAppService userAuthAppService,
                              VerificationCodeAppService verificationCodeAppService,
                              CsdnQrCodeUseCase csdnQrCodeUseCase,
                              JwtTokenProvider jwtTokenProvider,
                              TokenRevocationStore tokenRevocationStore) {
        this.userAuthAppService = userAuthAppService;
        this.verificationCodeAppService = verificationCodeAppService;
        this.csdnQrCodeUseCase = csdnQrCodeUseCase;
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenRevocationStore = tokenRevocationStore;
    }

    @Operation(summary = "发送短信验证码", description = "发送短信验证码，用于注册或登录")
    @PostMapping("/verification-code/sms")
    public ResponseEntity<ApiResponse<Object>> sendSmsCode(@Valid @RequestBody VerificationCodeSendCommand command) {
        // 强制设置类型为SMS
        command.setType("SMS");
        // todo 修改不返回验证码
        String verificationCode = verificationCodeAppService.sendVerificationCode(command);
        return ResponseEntity.ok(ApiResponse.success(verificationCode));
    }

    @Operation(summary = "发送邮箱验证码", description = "发送邮箱验证码，用于注册时邮箱验证")
    @PostMapping("/verification-code/email")
    public ResponseEntity<ApiResponse<Object>> sendEmailCode(@Valid @RequestBody VerificationCodeSendCommand command) {
        // 强制设置类型为EMAIL
        command.setType("EMAIL");
        // todo 修改不返回验证码
        String verificationCode = verificationCodeAppService.sendVerificationCode(command);
        return ResponseEntity.ok(ApiResponse.success(verificationCode));
    }

    @Operation(summary = "用户注册", description = "表单注册，需手机号+验证码+密码+协议同意\n" +
            "- 已注销账号的手机号可以重新注册")
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

    // ==================== CSDN App扫码登录/注册 ====================
    // TODO【需与CSDN对接】: 以下接口依赖CSDN OAuth服务，需在CSDN开放平台配置完成后才能正常使用

    /**
     * TODO【需与CSDN对接】: 生成CSDN扫码二维码
     * 需要在CSDN开放平台配置client-id后才能生成真实授权二维码
     * 未配置时返回模拟二维码（仅供开发测试）
     */
    @Operation(summary = "生成CSDN扫码二维码", 
               description = "生成CSDN App扫码登录的二维码，二维码有效期5分钟。\n" +
                       "TODO【需与CSDN对接】: 需先在CSDN开放平台申请应用并配置client-id")
    @GetMapping("/csdn/qr")
    public ResponseEntity<ApiResponse<CsdnQrCodeDTO>> generateCsdnQrCode() {
        CsdnQrCodeDTO qrCode = csdnQrCodeUseCase.generateQrCode();
        if (qrCode == null) {
            return ResponseEntity.ok(ApiResponse.error(500, "生成二维码失败，请检查CSDN OAuth配置"));
        }
        return ResponseEntity.ok(ApiResponse.success(qrCode));
    }

    /**
     * 查询CSDN扫码状态
     * 前端轮询调用此接口检查扫码状态
     */
    @Operation(summary = "查询CSDN扫码状态", 
               description = "前端轮询查询二维码状态：PENDING-待扫描, SCANNED-已扫描, LOGGED_IN-已登录, EXPIRED-已过期")
    @GetMapping("/csdn/qr/{qrId}/status")
    public ResponseEntity<ApiResponse<CsdnQrCodeUseCase.QrCodeStatusResult>> checkCsdnQrStatus(
            @Parameter(description = "二维码ID", required = true, example = "qr_abc123")
            @PathVariable String qrId) {
        CsdnQrCodeUseCase.QrCodeStatusResult result = csdnQrCodeUseCase.checkQrCodeStatus(qrId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * TODO【需与CSDN对接】: CSDN扫码回调处理
     * 此接口处理CSDN授权后的回调，需要与CSDN确认回调方式和参数格式
     */
    @Operation(summary = "CSDN扫码回调", 
               description = "CSDN App扫码授权后的回调处理。\n" +
                       "TODO【需与CSDN对接】: 确认回调方式和参数格式\n" +
                       "两种调用方式：\n" +
                       "1. CSDN主动回调（携带state和authCode参数）\n" +
                       "2. 前端代理回调（前端获取authCode后调用）\n" +
                       "未注册用户需要补充密码和协议同意后才能完成注册")
    @PostMapping("/csdn/callback")
    public ResponseEntity<ApiResponse<LoginResultDTO>> csdnAuthCallback(@Valid @RequestBody CsdnAuthCallbackCommand command) {
        LoginResultDTO result = userAuthAppService.handleCsdnAuthCallback(command);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * TODO【需与CSDN对接】: CSDN OAuth浏览器跳转回调
     * 如果CSDN使用浏览器跳转方式回调，通过此接口处理
     * 需要与CSDN确认实际的回调方式（是跳转还是前端代理）
     */
    @Operation(summary = "CSDN OAuth浏览器回调", 
               description = "CSDN浏览器跳转回调接口，处理state和code参数\n" +
                       "TODO【需与CSDN对接】: 确认CSDN实际使用的回调方式")
    @GetMapping("/csdn/oauth-callback")
    public ResponseEntity<ApiResponse<String>> csdnOAuthCallback(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String error) {
        
        if (error != null && !error.isEmpty()) {
            log.error("CSDN授权失败: error={}", error);
            return ResponseEntity.ok(ApiResponse.error(400, "CSDN授权失败: " + error));
        }

        if (code == null || code.isEmpty() || state == null || state.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.error(400, "授权码或状态码缺失"));
        }

        // 更新二维码状态为已扫描
        boolean updated = csdnQrCodeUseCase.updateQrCodeScanned(state, code);
        if (updated) {
            log.info("CSDN浏览器回调成功: state={}, code={}", state, code);
            return ResponseEntity.ok(ApiResponse.success("授权成功，请在页面上确认登录"));
        } else {
            log.warn("未找到对应的二维码状态: state={}", state);
            return ResponseEntity.ok(ApiResponse.error(400, "二维码已过期或不存在"));
        }
    }

    @Operation(summary = "开发环境快速登录", description = "仅用于本地开发测试，直接颁发JWT，无需验证码。生产环境应禁用。")
    @PostMapping("/dev-login")
    public ResponseEntity<ApiResponse<LoginResultDTO>> devLogin() {
        LoginResultDTO result = userAuthAppService.devLogin();
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @Operation(summary = "退出登录", description = "退出登录并将当前 Token 加入黑名单，使其立即失效")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        // 从请求头中提取 Authorization
        String authHeader = request.getHeader("Authorization");

        // 如果携带了有效的 Bearer Token，将其加入黑名单
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (!token.isEmpty() && jwtTokenProvider.validateToken(token)) {
                tokenRevocationStore.revokeToken(token);
                log.info("用户退出登录，Token 已加入黑名单");
            } else {
                log.warn("退出登录请求携带无效 Token");
            }
        } else {
            log.info("用户退出登录（未携带 Token，仅前端清理）");
        }

        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
