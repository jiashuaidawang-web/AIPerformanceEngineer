package com.aipe.resource.api.dto;

import java.io.Serializable;

/**
 * 统一 API 响应包装
 *
 * <p>对齐 IM-006 Response Model：{code, message, requestId, timestamp, data}
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public class ApiResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码（0 = 成功）
     */
    private int code;

    /**
     * 消息
     */
    private String message;

    /**
     * 请求 ID（UUID）
     */
    private String requestId;

    /**
     * 时间戳
     */
    private long timestamp;

    /**
     * 业务数据
     */
    private T data;

    public ApiResponse() {
        this.code = 0;
        this.message = "success";
        this.requestId = java.util.UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
    }

    public ApiResponse(T data) {
        this();
        this.data = data;
    }

    public ApiResponse(int code, String message) {
        this.code = code;
        this.message = message;
        this.requestId = java.util.UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 成功响应
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(data);
    }

    /**
     * 失败响应
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message);
    }

    // ==================== Getter & Setter ====================

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
