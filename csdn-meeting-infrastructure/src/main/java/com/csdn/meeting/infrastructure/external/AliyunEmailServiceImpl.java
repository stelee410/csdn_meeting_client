package com.csdn.meeting.infrastructure.external;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dm.model.v20151123.SingleSendMailRequest;
import com.aliyuncs.dm.model.v20151123.SingleSendMailResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.csdn.meeting.infrastructure.config.AliyunDmProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * 阿里云邮件推送服务实现
 * 支持配置不完整时自动降级到Mock模式，带重试机制，使用HTML模板
 */
@Slf4j
@Service
@Primary
public class AliyunEmailServiceImpl implements EmailService {

    @Autowired
    private AliyunDmProperties properties;

    private IAcsClient client;

    @PostConstruct
    public void init() {
        if (isConfigValid()) {
            try {
                IClientProfile profile = DefaultProfile.getProfile(
                        properties.getRegionId(),
                        properties.getAccessKeyId(),
                        properties.getAccessKeySecret());
                client = new DefaultAcsClient(profile);
                log.info("阿里云邮件服务初始化成功，区域: {}，发件人: {}",
                        properties.getRegionId(), properties.getFromAddress());
            } catch (Exception e) {
                log.error("阿里云邮件服务初始化失败: {}", e.getMessage());
                client = null;
            }
        } else {
            log.warn("阿里云邮件配置不完整（缺少accessKeyId、accessKeySecret或fromAddress），将使用模拟模式");
        }
    }

    /**
     * 检查配置是否有效
     */
    private boolean isConfigValid() {
        return properties.getAccessKeyId() != null && !properties.getAccessKeyId().isEmpty()
                && properties.getAccessKeySecret() != null && !properties.getAccessKeySecret().isEmpty()
                && properties.getFromAddress() != null && !properties.getFromAddress().isEmpty();
    }

    /**
     * 检查邮件客户端是否可用
     */
    private boolean isClientAvailable() {
        return client != null;
    }

    @Override
    public boolean sendVerificationCode(String email, String code, String scene) {
        if (!isConfigValid() || !isClientAvailable()) {
            log.info("[Email Mock] 向邮箱 {} 发送验证码: {}, 场景: {}", maskEmail(email), code, scene);
            return true;
        }

        String subject = buildSubject(scene);
        String htmlBody = buildEmailBody(code, scene);

        int maxAttempts = properties.getRetry().getMaxAttempts();
        long delayMs = properties.getRetry().getDelayMs();

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                SingleSendMailRequest request = new SingleSendMailRequest();
                request.setAccountName(properties.getFromAddress());
                request.setFromAlias(properties.getFromAlias());
                request.setAddressType(1);
                request.setTagName(properties.getTagName());
                request.setReplyToAddress(true);
                request.setToAddress(email);
                request.setSubject(subject);
                request.setHtmlBody(htmlBody);

                SingleSendMailResponse response = client.getAcsResponse(request);

                if (response.getEnvId() != null && !response.getEnvId().isEmpty()) {
                    log.info("邮件发送成功，邮箱: {}，场景: {}，EnvId: {}",
                            maskEmail(email), scene, response.getEnvId());
                    return true;
                } else {
                    log.error("邮件发送失败 (尝试 {}/{}): 未获取到EnvId", attempt, maxAttempts);
                }

            } catch (Exception e) {
                log.error("邮件发送异常 (尝试 {}/{}): {}", attempt, maxAttempts, e.getMessage(), e);
            }

            if (attempt < maxAttempts) {
                try {
                    Thread.sleep(delayMs * attempt);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    log.warn("邮件重试等待被中断");
                    break;
                }
            }
        }

        return false;
    }

    /**
     * 构建邮件主题
     */
    private String buildSubject(String scene) {
        String prefix = "CSDN会议";
        if (scene == null) {
            return prefix + " - 验证码";
        }

        String sceneLower = scene.toLowerCase();
        if (sceneLower.contains("register") || sceneLower.contains("注册")) {
            return prefix + " - 注册验证码";
        } else if (sceneLower.contains("login") || sceneLower.contains("登录")) {
            return prefix + " - 登录验证码";
        } else if (sceneLower.contains("reset") || sceneLower.contains("重置")) {
            return prefix + " - 密码重置验证码";
        } else if (sceneLower.contains("bind") || sceneLower.contains("绑定")) {
            return prefix + " - 邮箱绑定验证码";
        } else {
            return prefix + " - 验证码";
        }
    }

    /**
     * 构建邮件HTML内容
     */
    private String buildEmailBody(String code, String scene) {
        String operation = "操作";
        if (scene != null) {
            String sceneLower = scene.toLowerCase();
            if (sceneLower.contains("register") || sceneLower.contains("注册")) {
                operation = "注册";
            } else if (sceneLower.contains("login") || sceneLower.contains("登录")) {
                operation = "登录";
            } else if (sceneLower.contains("reset") || sceneLower.contains("重置")) {
                operation = "重置密码";
            } else if (sceneLower.contains("bind") || sceneLower.contains("绑定")) {
                operation = "邮箱绑定";
            }
        }

        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<style>" +
                "body { font-family: 'Microsoft YaHei', Arial, sans-serif; line-height: 1.6; color: #333; }" +
                ".container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                ".header { background: #007bff; color: white; padding: 20px; text-align: center; " +
                "          border-radius: 5px 5px 0 0; }" +
                ".content { background: #f9f9f9; padding: 30px; border: 1px solid #e0e0e0; }" +
                ".code { font-size: 32px; font-weight: bold; color: #007bff; letter-spacing: 5px; " +
                "       text-align: center; padding: 20px; margin: 20px 0; background: white; " +
                "       border-radius: 5px; }" +
                ".footer { color: #999; font-size: 12px; text-align: center; padding: 20px; }" +
                ".warning { color: #dc3545; font-size: 14px; margin-top: 20px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "  <div class='header'>" +
                "    <h2>CSDN会议系统</h2>" +
                "  </div>" +
                "  <div class='content'>" +
                "    <p>您好，</p>" +
                "    <p>您正在进行<strong>" + operation + "</strong>操作，验证码如下：</p>" +
                "    <div class='code'>" + code + "</div>" +
                "    <p>验证码有效期为<strong>5分钟</strong>，请尽快完成验证。</p>" +
                "    <p class='warning'>如非本人操作，请忽略此邮件。</p>" +
                "  </div>" +
                "  <div class='footer'>" +
                "    <p>此邮件由系统自动发送，请勿回复。</p>" +
                "    <p>&copy; 2026 CSDN会议系统</p>" +
                "  </div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    /**
     * 邮箱地址脱敏显示
     */
    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        String[] parts = email.split("@");
        String localPart = parts[0];
        String domain = parts[1];

        if (localPart.length() <= 2) {
            return "***@" + domain;
        }
        return localPart.substring(0, 2) + "***@" + domain;
    }
}
