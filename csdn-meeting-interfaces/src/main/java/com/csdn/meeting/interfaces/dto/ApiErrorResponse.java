package com.csdn.meeting.interfaces.dto;

/**
 * 错误响应体：errorCode, message, (optional) field
 */
public class ApiErrorResponse {

    private int errorCode;
    private String message;
    private String field;

    public ApiErrorResponse() {
    }

    public ApiErrorResponse(int errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public ApiErrorResponse(int errorCode, String message, String field) {
        this.errorCode = errorCode;
        this.message = message;
        this.field = field;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
}
