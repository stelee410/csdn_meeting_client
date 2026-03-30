package com.csdn.meeting.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * CSDN扫码二维码DTO
 */
@Data
@Schema(description = "CSDN扫码二维码信息")
public class CsdnQrCodeDTO {

    @Schema(description = "二维码唯一标识", example = "qr_abc123")
    private String qrId;

    @Schema(description = "二维码Base64图片数据", example = "data:image/png;base64,iVBORw0KGgo...")
    private String qrImageBase64;

    @Schema(description = "CSDN授权URL（可直接生成二维码）", example = "https://passport.csdn.net/oauth2/authorize?...")
    private String authUrl;

    @Schema(description = "二维码过期时间（秒）", example = "300")
    private Integer expireSeconds;

    @Schema(description = "二维码创建时间戳", example = "1711699200000")
    private Long createTime;
}
