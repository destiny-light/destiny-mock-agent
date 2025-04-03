package com.destinylight.tools.mock.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * 从命令行得到的参数
 * </p>
 *
 * @author 郑靖华 (11821967@qq.com)
 * @date 2025/3/18
 */
public class MockCommandLineArgs {
    private static final Logger log = LoggerFactory.getLogger(MockCommandLineArgs.class);

    /**
     * @param agentArgs 从命令"-javaagent:..[=[name=value]*]"传入的参数, 参数之间用&符号分隔
     */
    public static void parse(String agentArgs) {
        if (MockUtils.isBlank(agentArgs)) {
            return;
        }
        String[] args = agentArgs.split("&");
        if (args == null || args.length < 1) {
            return;
        }
        for (String arg : args) {
            try {
                String[] options = arg.split("=");
                String name = options[0];
                if ("debug".equals(name)) {
                    // 是否输出Mock拦截器组件的调试信息
                    log.info("{} 设置为调试模式", MockConstants.COMPONENT_NAME);
                    MockConstants.setDebug(true);
                }
            } catch (Exception e) {
                log.error("{} 解析参数[{}]失败[{}]", MockConstants.COMPONENT_NAME, arg, e.getMessage(), e);
            }
        }
    }


}
