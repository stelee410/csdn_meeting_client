package com.csdn.meeting.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 通用API响应包装类
 * @param <T> 响应数据类型
 */
@Schema(description = "通用API响应包装类")
public class ApiResponse<T> {

    @Schema(description = "响应状态码，200表示成功", example = "200")
    private int code;

    @Schema(description = "响应消息", example = "success")
    private String msg;

    @Schema(description = "响应数据")
    private T data;

    @Schema(description = "错误字段（仅在验证错误时返回）", example = "email")
    private String field;

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(200);
        response.setMsg("success");
        response.setData(data);
        return response;
    }

    public static <T> ApiResponse<T> error(int code, String msg) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setMsg(msg);
        return response;
    }

    public static <T> ApiResponse<T> error(int code, String msg, String field) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setMsg(msg);
        response.setField(field);
        return response;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
}
