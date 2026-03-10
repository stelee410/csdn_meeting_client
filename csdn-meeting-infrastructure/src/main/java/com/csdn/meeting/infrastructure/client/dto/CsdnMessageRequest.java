package com.csdn.meeting.infrastructure.client.dto;

import java.util.List;
import java.util.Map;

/**
 * CSDN消息推送请求DTO
 * 对应接口文档的请求参数
 */
public class CsdnMessageRequest {

    /**
     * 模板编码
     */
    private String templateCode;

    /**
     * 接收用户的CSDN ID列表
     */
    private List<String> toUsers;

    /**
     * 模板变量，用于替换模板中的占位符
     */
    private Map<String, String> params;

    public CsdnMessageRequest() {
    }

    public CsdnMessageRequest(String templateCode, List<String> toUsers, Map<String, String> params) {
        this.templateCode = templateCode;
        this.toUsers = toUsers;
        this.params = params;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public List<String> getToUsers() {
        return toUsers;
    }

    public void setToUsers(List<String> toUsers) {
        this.toUsers = toUsers;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }
}
