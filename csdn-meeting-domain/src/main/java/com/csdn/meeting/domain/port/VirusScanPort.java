package com.csdn.meeting.domain.port;

/**
 * 病毒扫描端口：对上传文件进行安全扫描。
 * 失败时抛出异常，由调用方转为 400 响应。
 */
public interface VirusScanPort {

    /**
     * 扫描文件，无病毒则正常返回。
     *
     * @param fileBytes 文件内容
     * @param fileName  文件名
     * @throws RuntimeException 扫描失败时（如发现病毒）
     */
    void scan(byte[] fileBytes, String fileName);
}
