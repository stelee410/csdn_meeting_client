package com.csdn.meeting.infrastructure.client.dto;

/**
 * CSDN消息推送响应DTO
 * 对应接口文档的响应参数
 */
public class CsdnMessageResponse {

    /**
     * 请求状态 (true: 成功, false: 失败)
     */
    private Boolean status;

    /**
     * 状态码 (200: 成功, 4xx: 客户端错误, 5xx: 服务端错误)
     */
    private String code;

    /**
     * 提示信息
     */
    private String message;

    /**
     * 响应数据（可选）
     */
    private Object data;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    /**
     * 判断响应是否成功
     */
    public boolean isSuccess() {
        return Boolean.TRUE.equals(status) && "200".equals(code);
    }
}
