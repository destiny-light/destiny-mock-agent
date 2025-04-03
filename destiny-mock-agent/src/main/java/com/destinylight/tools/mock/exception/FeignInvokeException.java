package com.destinylight.tools.mock.exception;

import org.springframework.web.bind.annotation.RequestMethod;

/**
 * <p>
 * 拦截FeignClient时的异常
 * </p>
 *
 * @author 郑靖华 (11821967@qq.com)
 * @date 2025/3/11
 */
public class FeignInvokeException extends MockException {
    /**
     * @param message the detail message (which is saved tor later retrieval by the {@link #getMessage()} method).
     */
    public FeignInvokeException(String message) {
        super(message);
    }

    /**
     * @param message the detail message (which is saved tor later retrieval by the {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the {@link #getCause()} method).
     *                (A {@code null} value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public FeignInvokeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message       the detail message (which is saved tor later retrieval by the {@link #getMessage()} method).
     * @param url           访问的URL
     * @param requestMethod HTTP REQUEST METHOD
     */
    public FeignInvokeException(String message, String url, RequestMethod requestMethod) {
        super(String.format("Call[%s %s], %s", requestMethod.name(), url, message));
    }

    /**
     * @param message         the detail message (which is saved tor later retrieval by the {@link #getMessage()} method).
     * @param clz             FeignClient的类
     * @param feignMethodName FeignClient的类中的方法名
     * @param url             访问的URL
     * @param requestMethod   HTTP REQUEST METHOD
     */
    public FeignInvokeException(String message, Class<?> clz, String feignMethodName, String url, RequestMethod requestMethod) {
        super(String.format("Invoke[%s.%s()] Call[%s %s], %s", clz.getName(), feignMethodName, requestMethod.name(), url, message));
    }

    /**
     * @param message         the detail message (which is saved tor later retrieval by the {@link #getMessage()} method).
     * @param clz             FeignClient的类
     * @param feignMethodName FeignClient的类中的方法名
     */
    public FeignInvokeException(String message, Class<?> clz, String feignMethodName) {
        super(String.format("Invoke[%s.%s()], %s", clz.getName(), feignMethodName, message));
    }

    /**
     * @param message       the detail message (which is saved tor later retrieval by the {@link #getMessage()} method).
     * @param url           访问的URL
     * @param requestMethod HTTP REQUEST METHOD
     * @param cause         the cause (which is saved for later retrieval by the {@link #getCause()} method).
     *                      (A {@code null} value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public FeignInvokeException(String message, String url, RequestMethod requestMethod, Throwable cause) {
        super(String.format("Call[%s %s], %s", requestMethod.name(), url, message), cause);
    }

    /**
     * @param message         the detail message (which is saved tor later retrieval by the {@link #getMessage()} method).
     * @param clz             FeignClient的类
     * @param feignMethodName FeignClient的类中的方法名
     * @param url             访问的URL
     * @param requestMethod   HTTP REQUEST METHOD
     * @param cause           the cause (which is saved for later retrieval by the {@link #getCause()} method).
     *                        (A {@code null} value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public FeignInvokeException(String message, Class<?> clz, String feignMethodName, String url,
                                RequestMethod requestMethod, Throwable cause) {
        super(String.format("Invoke[%s.%s()] Call[%s %s], %s",
                        clz.getName(), feignMethodName, requestMethod.name(), url, message),
                cause);
    }

    /**
     * @param message         the detail message (which is saved tor later retrieval by the {@link #getMessage()} method).
     * @param clz             FeignClient的类
     * @param feignMethodName FeignClient的类中的方法名
     * @param cause           the cause (which is saved for later retrieval by the {@link #getCause()} method).
     *                        (A {@code null} value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public FeignInvokeException(String message, Class<?> clz, String feignMethodName, Throwable cause) {
        super(String.format("Invoke[%s.%s()], %s", clz.getName(), feignMethodName, message), cause);
    }
}
