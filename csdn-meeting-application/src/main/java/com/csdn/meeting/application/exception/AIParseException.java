package com.csdn.meeting.application.exception;

/**
 * AI 解析异常：超时、服务不可用等，返回 422 Unprocessable Entity。
 */
public class AIParseException extends BusinessException {

    public static final int HTTP_STATUS = 422;

    public AIParseException(String message) {
        super(HTTP_STATUS, message);
    }

    public AIParseException(String message, Throwable cause) {
        super(HTTP_STATUS, message, cause);
    }
}
