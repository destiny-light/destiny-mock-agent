package com.destinylight.tools.mock.utils;

import java.util.regex.Pattern;

/**
 * <p>
 * Mock拦截器组件所需要的常量
 * </p>
 *
 * @author 郑靖华 (11821967@qq.com)
 * @date 2025/3/11
 */
public class MockConstants {
    /**
     * 是否调试Mock拦截器组件本身
     */
    public static boolean DEBUG = false;
    /**
     * Mock拦截器组件配置文件主目录
     */
    public static String MIC_HOME = null;
    /**
     * Mock数据配置文件名称的后缀
     */
    public static final String MOCK_DATA_FILENAME_SUFFIX = ".mock.json";
    /**
     * Mock拦截器组件配置文件名称
     */
    public static final String MOCK_AGENT_CONFIG_FILENAME = "mock-agent-config.json";
    /**
     * 组件名称
     */
    public static final String COMPONENT_NAME = "【Mock拦截器组件】";
    /**
     * Spring为FeignClient接口类生成的代理类的名称的正则表达式字符串
     */
    public static final String FEIGN_PROXY_NAME_PATTERN_STR = ".*\\$Proxy\\d+|.*\\$\\$EnhancerBySpringCGLIB\\$.*";
    /**
     * Spring为FeignClient接口类生成的代理类的名称的正则表达式
     */
    public static final Pattern FEIGN_PROXY_NAME_PATTERN = Pattern.compile(FEIGN_PROXY_NAME_PATTERN_STR);
    /**
     * 在调用Mock服务器(元素设计平台)时，需要在请求报文的HTTP HEADER里添加参数，指出微服务/应用名称。
     */
    public static final String MOCK_SERVER_REQ_HEAD_SVC_NAME = "serviceName";
    /**
     * 在调用Mock服务器(元素设计平台)后，在响应报文的HTTP HEADER里的参数，指出是否真实的被Mock服务处理过了
     */
    public static final String MOCK_SERVER_RESP_HEAD_MOCK = "metaMock";
    /**
     * 在调用Mock服务器(元素设计平台)后，在响应报文的HTTP HEADER里的参数，指出生成mock数据时是否发生了错误。
     */
    public static final String MOCK_SERVER_RESP_HEAD_DATA = "mockData";
    /**
     * Mock数据生成算法 - 极简规则
     */
    public static final String MOCK_ALG_MIN_RULE = "min-rule";
    /**
     * Mock数据生成算法 - 来自于之前生成的mock数据的缓存
     */
    public static final String MOCK_ALG_FROM_CACHE = "last-cache";
    /**
     * Mock数据生成算法 - 来自于本地Mock数据配置文件
     */
    public static final String MOCK_ALG_FROM_LOCAL_FILE = "local-file";
    /**
     * Mock数据生成算法 - 来自于Mock服务器(元素设计平台)
     */
    public static final String MOCK_ALG_FROM_MOCK_SERVER = "mock-server";

    public static void setDebug(boolean debug) {
        MockConstants.DEBUG = debug;
    }

    public static void setMicHome(String micHome) {
        MIC_HOME = micHome;
    }
}
