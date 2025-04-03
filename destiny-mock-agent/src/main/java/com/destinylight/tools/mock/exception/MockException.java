package com.destinylight.tools.mock.exception;

/**
 * <p>
 * Mock拦截器组件的异常
 * </p>
 *
 * @author 郑靖华 (11821967@qq.com)
 * @date 2025/3/11
 */
public class MockException extends RuntimeException {
    /**
     * @param message the detail message (which is saved tor later retrieval by the {@link #getMessage()} method).
     */
    public MockException(String message) {
        super(message);
    }

    /**
     * @param message the detail message (which is saved tor later retrieval by the {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the {@link #getCause()} method).
     *                (A {@code null} value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public MockException(String message, Throwable cause) {
        super(message, cause);
    }
}
