package com.qqbot.common.exception;

import lombok.Getter;

/**
 * 业务异常
 *
 * <p>用于表示业务逻辑层面的异常，如 AI 调用失败、消息发送失败等。
 * 包含错误码和错误消息，由全局异常处理器统一处理。</p>
 *
 * @author QQbot Team
 * @since 2026-07-29
 */
@Getter
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 构造业务异常
     *
     * @param code    错误码
     * @param message 错误消息
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 构造业务异常（默认错误码 500）
     *
     * @param message 错误消息
     */
    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }

    /**
     * 构造业务异常（带原始异常）
     *
     * @param code    错误码
     * @param message 错误消息
     * @param cause   原始异常
     */
    public BusinessException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}
