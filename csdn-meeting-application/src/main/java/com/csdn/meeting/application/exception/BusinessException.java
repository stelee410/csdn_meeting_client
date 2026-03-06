package com.csdn.meeting.application.exception;

/**
 * 业务异常，用于 422 Unprocessable Entity、403 Forbidden 等场景
 */
public class BusinessException extends RuntimeException {

    private final int httpStatus;

    public BusinessException(int httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public BusinessException(int httpStatus, String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
