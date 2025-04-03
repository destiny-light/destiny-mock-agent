package com.destinylight.tools.mock.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import feign.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * <p>
 * Mock拦截器组件的工具类
 * </p>
 *
 * @author 郑靖华 (11821967@qq.com)
 * @date 2025/3/11
 */
public class MockUtils {
    private static final Logger log = LoggerFactory.getLogger(MockUtils.class);

    /**
     * 取得代理类所代理的FeignClient接口类
     * 判断核心是：是否存在{@code @FeignClient}的注解.
     *
     * @param proxy 需要判断的对象，该类是由Spring生成的代理类。
     * @return 代理类所代理的FeignClient接口类. <code>null</code>表示该代理类并不是代理一个FeignClient接口类
     */
    public static Class<?> getFeignInterface(Class<?> proxy) {
        Class<?>[] proxyInterfaces = getInterfaces(proxy);
        if (MockConstants.DEBUG) {
            log.info("代理对象的接口列表：{}", Arrays.toString(proxyInterfaces));
        }
        if (proxyInterfaces.length > 0) {
            // 判断是否存在接口带有feign注解.
            for (Class<?> proxyInterface : proxyInterfaces) {
                if (proxyInterface.isAnnotationPresent(FeignClient.class)) {
                    //  存在FeignClient注解并且不存在Fallback
                    FeignClient feignClient = proxyInterface.getAnnotation(FeignClient.class);
                    Class<?> fallback = feignClient.fallback();
                    if (fallback != void.class && fallback.isAssignableFrom(proxy.getClass())) {
                        System.out.println("Fallback impl not proxy interceptor");
                        // 只要存在接口并且是fallback中类的实现类就不再进行代理.
                        return null;
                    }
                    return proxyInterface;
                }
            }
        }
        return null;
    }

    /**
     * 获取class的接口列表.
     *
     * @param proxy 代理类.
     * @return Class<?>[]
     */
    private static Class<?>[] getInterfaces(Class<?> proxy) {
        return proxy.getInterfaces();
    }

    /**
     * @return Mock配置文件的根目录
     */
    public static Path configPath() {
        return isBlank(MockConstants.MIC_HOME) ?
                Paths.get(System.getProperty("user.dir"), "mock") :
                Paths.get(MockConstants.MIC_HOME);
    }

    /**
     * @return Mock配置文件的根目录是否存在
     */
    public static boolean configPathExist() {
        File root = configPath().toFile();
        return root.exists() && root.isDirectory();
    }

    /**
     * @return Mock服务器的配置文件
     */
    public static File serverConfigFile() {
        return Paths.get(configPath().toFile().getAbsolutePath(), MockConstants.MOCK_AGENT_CONFIG_FILENAME).toFile();
    }

    /**
     * Check whether the current {@code CharSequence} is blank space
     *
     * @param cs {@link CharSequence} object
     * @return {@code boolean} true is current {@code cs} is blank space, false is not
     */
    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 从文件解析JSON对象
     *
     * @param file JSON文件
     * @return JSON对象
     * @throws IOException
     */
    public static JSONObject parseJsonFile(File file) throws IOException {
        String jsonStr = new String(Files.readAllBytes(file.toPath()));
        return JSON.parseObject(jsonStr);
    }

    /**
     * <pre>
     * 所谓简单数据类型，包括：
     * 1. {@code int} 及其包装类 {@link Integer}
     * 2. {@code short} 及其包装类 {@link Short}
     * 3. {@code long} 及其包装类 {@link Long}
     * 4. {@code float} 及其包装类 {@link Float}
     * 5. {@code double} 及其包装类 {@link Double}
     * 6. {@code boolean} 及其包装类 {@link Boolean}
     * 7. {@code byte} 及其包装类 {@link Byte}
     * 8. {@code char} 及其包装类 {@link Character}
     * 9. {@link String}
     * </pre>
     *
     * @param clz 类型
     * @return 是否是简单数据类型
     */
    public static boolean isSimpleType(Class<?> clz) {
        String typeName = clz.getTypeName();
        if ("int".equals(typeName) || "Integer".equals(typeName) || "java.lang.Integer".equals(typeName)
                || "short".equals(typeName) || "Short".equals(typeName) || "java.lang.Short".equals(typeName)
                || "long".equals(typeName) || "Long".equals(typeName) || "java.lang.Long".equals(typeName)
                || "float".equals(typeName) || "Float".equals(typeName) || "java.lang.Float".equals(typeName)
                || "double".equals(typeName) || "Double".equals(typeName) || "java.lang.Double".equals(typeName)
                || "boolean".equals(typeName) || "Boolean".equals(typeName) || "java.lang.Boolean".equals(typeName)
                || "byte".equals(typeName) || "Byte".equals(typeName) || "java.lang.Byte".equals(typeName)
                || "char".equals(typeName) || "Character".equals(typeName) || "java.lang.Character".equals(typeName)
                || "String".equals(typeName) || "java.lang.String".equals(typeName)) {
            return true;
        }
        return false;
    }

    /**
     * 如果传入的值为null，则返回默认值
     *
     * @param value 传入的值
     * @param dfl   默认值
     * @return 如果传入的值为null，则返回默认值。否则，返回传入的值.
     */
    public static boolean nullAs(Boolean value, boolean dfl) {
        return value == null ? dfl : value;
    }

    /**
     * @param values 待判断的字符串
     * @return 第一个非空的字符串。如果所有字符串都是空白字符串，则返回 <code>null</code> 。
     */
    public static String anyNonBlank(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (!isBlank(value)) {
                return value;
            }
        }
        return null;
    }

    /**
     * 根据传入的HTTP METHOD的名称，转换成 {@link feign.Request.HttpMethod} 枚举
     *
     * @param httpMethodName HTTP METHOD的名称，例如: <code>GET</code>, <code>POST</code>, 等等。
     * @return {@link feign.Request.HttpMethod}
     */
    public static Request.HttpMethod ofFeignHttpMethod(String httpMethodName) {
        if ("GET".equalsIgnoreCase(httpMethodName)) {
            return Request.HttpMethod.GET;
        }
        if ("HEAD".equalsIgnoreCase(httpMethodName)) {
            return Request.HttpMethod.HEAD;
        }
        if ("POST".equalsIgnoreCase(httpMethodName)) {
            return Request.HttpMethod.POST;
        }
        if ("PUT".equalsIgnoreCase(httpMethodName)) {
            return Request.HttpMethod.PUT;
        }
        if ("DELETE".equalsIgnoreCase(httpMethodName)) {
            return Request.HttpMethod.DELETE;
        }
        if ("CONNECT".equalsIgnoreCase(httpMethodName)) {
            return Request.HttpMethod.CONNECT;
        }
        if ("OPTIONS".equalsIgnoreCase(httpMethodName)) {
            return Request.HttpMethod.OPTIONS;
        }
        if ("TRACE".equalsIgnoreCase(httpMethodName)) {
            return Request.HttpMethod.TRACE;
        }
        if ("PATCH".equalsIgnoreCase(httpMethodName)) {
            return Request.HttpMethod.PATCH;
        }
        return Request.HttpMethod.GET;
    }
}
