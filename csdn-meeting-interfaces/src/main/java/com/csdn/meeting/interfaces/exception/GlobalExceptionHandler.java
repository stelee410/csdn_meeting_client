package com.csdn.meeting.interfaces.exception;

import com.csdn.meeting.application.exception.BusinessException;
import com.csdn.meeting.domain.exception.AgendaIntegrityException;
import com.csdn.meeting.interfaces.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 日程完整性校验失败 -> 400, 带 field 定位 */
    @ExceptionHandler(AgendaIntegrityException.class)
    public ResponseEntity<ApiErrorResponse> handleAgendaIntegrityException(AgendaIntegrityException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiErrorResponse(400, e.getMessage(), "agenda"));
    }

    /** 业务异常 -> 422 或 403 */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessException(BusinessException e) {
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(new ApiErrorResponse(e.getHttpStatus(), e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiErrorResponse(400, e.getMessage()));
    }

    /** 非法状态（如非草稿不可编辑） -> 422 */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalStateException(IllegalStateException e) {
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ApiErrorResponse(422, e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiErrorResponse(500, "服务器内部错误"));
    }
}
