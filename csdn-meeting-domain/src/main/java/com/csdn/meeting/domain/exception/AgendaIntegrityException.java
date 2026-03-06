package com.csdn.meeting.domain.exception;

/**
 * 四级日程完整性校验失败时抛出
 * 供 application/interfaces 层捕获并转换为 400 响应
 */
public class AgendaIntegrityException extends RuntimeException {

    public AgendaIntegrityException(String message) {
        super(message);
    }

    public AgendaIntegrityException(String message, Throwable cause) {
        super(message, cause);
    }
}
