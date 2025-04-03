package com.destinylight.tools.mock.demo.sdk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * 提供用于MIC测试的静态方法:<br>
 * 1. 无返回的方法，多个方法同名，但参数不同。<br>
 * 2. 返回List的方法，多个方法同名，但参数不同。<br>
 * 3. 返回Map的方法，多个方法同名，但参数不同。<br>
 * 4. 返回String的方法，多个方法同名，但参数不同。<br>
 * 5. 返回pojo的方法，多个方法同名，但参数不同。<br>
 * </p>
 *
 * @author 郑靖华 (11821967@qq.com)
 * @date 2025/3/3
 */
public class CallStaticSdk {
    private static final Logger log = LoggerFactory.getLogger(CallStaticSdk.class);

    /**
     * 无返回值的静态方法
     *
     * @param arg1 参数
     */
    public static void call(String arg1) {
        log.info("进入方法[{}], 参数1[类型[{}], 值[{}]]", "call",
                arg1.getClass().getName(), arg1);
    }

    /**
     * 无返回值的静态方法
     *
     * @param arg1 参数
     * @param arg2 参数
     */
    public static void call(String arg1, Integer arg2) {
        log.info("进入方法[{}], 参数1[类型[{}], 值[{}]], 参数2[类型[{}], 值[{}]]", "call",
                arg1.getClass().getName(), arg1,
                arg2.getClass().getName(), arg2);
    }

    /**
     * 无返回值的静态方法
     *
     * @param arg1 参数
     */
    public static void call(Integer arg1) {
        log.info("进入方法[{}], 参数1[类型[{}], 值[{}]]", "call",
                arg1.getClass().getName(), arg1);
    }
}
