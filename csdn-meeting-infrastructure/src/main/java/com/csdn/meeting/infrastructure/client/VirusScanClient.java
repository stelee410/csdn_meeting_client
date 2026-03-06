package com.csdn.meeting.infrastructure.client;

import com.csdn.meeting.domain.port.VirusScanPort;
import org.springframework.stereotype.Component;

/**
 * 文件病毒扫描客户端。
 * Stub：测试时对所有文件返回 OK；真实实现可调用外部扫描服务。
 * 失败时抛出异常，由调用方转为 400 响应。
 */
@Component
public class VirusScanClient implements VirusScanPort {

    @Override
    public void scan(byte[] fileBytes, String fileName) {
        // Stub: 测试时全部通过
    }
}
