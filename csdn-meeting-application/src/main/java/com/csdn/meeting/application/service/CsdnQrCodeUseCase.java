package com.csdn.meeting.application.service;

import com.csdn.meeting.application.dto.CsdnQrCodeDTO;
import com.csdn.meeting.infrastructure.external.CsdnAuthClient;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CSDN扫码二维码用例
 * 生成和管理CSDN授权二维码，处理扫码状态流转
 */
@Slf4j
@Service
public class CsdnQrCodeUseCase {

    private final CsdnAuthClient csdnAuthClient;

    // 二维码有效期（5分钟）
    private static final int QR_CODE_EXPIRE_SECONDS = 300;

    // 二维码宽度
    private static final int QR_CODE_WIDTH = 300;

    // 二维码高度
    private static final int QR_CODE_HEIGHT = 300;

    // 二维码状态缓存（生产环境建议使用Redis）
    private final Map<String, QrCodeStatus> qrCodeStatusMap = new ConcurrentHashMap<>();

    public CsdnQrCodeUseCase(CsdnAuthClient csdnAuthClient) {
        this.csdnAuthClient = csdnAuthClient;
    }

    /**
     * 生成CSDN扫码二维码
     *
     * @return 二维码信息DTO
     */
    public CsdnQrCodeDTO generateQrCode() {
        try {
            // 生成二维码唯一标识
            String qrId = generateQrId();

            // 生成随机state（防CSRF）
            String state = UUID.randomUUID().toString().replace("-", "");

            // 获取CSDN授权URL
            String authUrl = csdnAuthClient.generateAuthUrl(state);

            if (authUrl == null) {
                log.error("生成CSDN授权URL失败");
                return null;
            }

            // 生成二维码图片
            String qrImageBase64 = generateQrCodeImage(authUrl);

            // 初始化二维码状态
            QrCodeStatus status = new QrCodeStatus();
            status.setQrId(qrId);
            status.setState(state);
            status.setAuthUrl(authUrl);
            status.setStatus(QrStatus.PENDING);
            status.setCreateTime(System.currentTimeMillis());
            status.setExpireTime(System.currentTimeMillis() + QR_CODE_EXPIRE_SECONDS * 1000);

            qrCodeStatusMap.put(qrId, status);

            // 构建返回DTO
            CsdnQrCodeDTO dto = new CsdnQrCodeDTO();
            dto.setQrId(qrId);
            dto.setQrImageBase64(qrImageBase64);
            dto.setAuthUrl(authUrl);
            dto.setExpireSeconds(QR_CODE_EXPIRE_SECONDS);
            dto.setCreateTime(System.currentTimeMillis());

            log.info("生成CSDN扫码二维码成功: qrId={}, state={}", qrId, state);
            return dto;

        } catch (Exception e) {
            log.error("生成CSDN扫码二维码失败", e);
            return null;
        }
    }

    /**
     * 查询二维码状态
     * 前端轮询调用此接口检查扫码状态
     *
     * @param qrId 二维码ID
     * @return 二维码状态信息
     */
    public QrCodeStatusResult checkQrCodeStatus(String qrId) {
        QrCodeStatus status = qrCodeStatusMap.get(qrId);

        if (status == null) {
            return QrCodeStatusResult.fail("二维码不存在");
        }

        // 检查是否过期
        if (System.currentTimeMillis() > status.getExpireTime()) {
            status.setStatus(QrStatus.EXPIRED);
            return QrCodeStatusResult.expired();
        }

        QrCodeStatusResult result = new QrCodeStatusResult();
        result.setSuccess(true);
        result.setQrId(qrId);
        result.setStatus(status.getStatus().name());

        // 如果已扫描或已登录，返回authCode
        if (status.getStatus() == QrStatus.SCANNED || status.getStatus() == QrStatus.LOGGED_IN) {
            result.setAuthCode(status.getAuthCode());
        }

        // 如果已登录，返回登录结果
        if (status.getStatus() == QrStatus.LOGGED_IN && status.getLoginResult() != null) {
            result.setLoginResult(status.getLoginResult());
        }

        return result;
    }

    /**
     * CSDN回调时更新二维码状态
     * 用户扫码并授权后，CSDN回调我们的接口，我们更新二维码状态
     *
     * @param state    随机状态码（用于关联二维码）
     * @param authCode CSDN返回的授权码
     * @return 是否成功找到并更新二维码状态
     */
    public boolean updateQrCodeScanned(String state, String authCode) {
        for (QrCodeStatus status : qrCodeStatusMap.values()) {
            if (state.equals(status.getState())) {
                status.setStatus(QrStatus.SCANNED);
                status.setAuthCode(authCode);
                status.setScanTime(System.currentTimeMillis());

                log.info("更新二维码状态为已扫描: qrId={}, state={}", status.getQrId(), state);
                return true;
            }
        }

        log.warn("未找到对应的二维码状态: state={}", state);
        return false;
    }

    /**
     * 更新二维码状态为已登录
     * 用户完成登录/注册后调用
     *
     * @param qrId        二维码ID
     * @param loginResult 登录结果
     */
    public void updateQrCodeLoggedIn(String qrId, Object loginResult) {
        QrCodeStatus status = qrCodeStatusMap.get(qrId);
        if (status != null) {
            status.setStatus(QrStatus.LOGGED_IN);
            status.setLoginResult(loginResult);
            status.setLoginTime(System.currentTimeMillis());

            log.info("更新二维码状态为已登录: qrId={}", qrId);
        }
    }

    /**
     * 清理过期的二维码状态
     * 建议定期调用（如每分钟）
     */
    public void cleanExpiredQrCodes() {
        long now = System.currentTimeMillis();
        qrCodeStatusMap.entrySet().removeIf(entry -> {
            QrCodeStatus status = entry.getValue();
            boolean expired = now > status.getExpireTime();
            if (expired) {
                log.debug("清理过期二维码: qrId={}", entry.getKey());
            }
            return expired;
        });
    }

    /**
     * 生成二维码唯一ID
     */
    private String generateQrId() {
        return "qr_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    /**
     * 生成二维码图片（Base64格式）
     */
    private String generateQrCodeImage(String content) throws Exception {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.MARGIN, 2);

        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, QR_CODE_WIDTH, QR_CODE_HEIGHT, hints);

        BufferedImage image = new BufferedImage(QR_CODE_WIDTH, QR_CODE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < QR_CODE_WIDTH; x++) {
            for (int y = 0; y < QR_CODE_HEIGHT; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", outputStream);

        byte[] imageBytes = outputStream.toByteArray();
        String base64 = Base64.getEncoder().encodeToString(imageBytes);

        return "data:image/png;base64," + base64;
    }

    /**
     * 二维码状态枚举
     */
    public enum QrStatus {
        PENDING,    // 待扫描
        SCANNED,    // 已扫描（用户已扫码，等待确认登录）
        LOGGED_IN,  // 已登录
        EXPIRED     // 已过期
    }

    /**
     * 二维码状态内部类
     */
    @lombok.Data
    public static class QrCodeStatus {
        private String qrId;
        private String state;
        private String authUrl;
        private QrStatus status;
        private String authCode;
        private Object loginResult;
        private long createTime;
        private long expireTime;
        private long scanTime;
        private long loginTime;
    }

    /**
     * 二维码状态查询结果
     */
    @lombok.Data
    public static class QrCodeStatusResult {
        private boolean success;
        private String errorMessage;
        private String qrId;
        private String status;      // PENDING, SCANNED, LOGGED_IN, EXPIRED
        private String authCode;    // 扫码后返回的授权码
        private Object loginResult; // 登录成功后的结果

        public static QrCodeStatusResult fail(String message) {
            QrCodeStatusResult result = new QrCodeStatusResult();
            result.setSuccess(false);
            result.setErrorMessage(message);
            return result;
        }

        public static QrCodeStatusResult expired() {
            QrCodeStatusResult result = new QrCodeStatusResult();
            result.setSuccess(true);
            result.setStatus(QrStatus.EXPIRED.name());
            return result;
        }
    }
}
