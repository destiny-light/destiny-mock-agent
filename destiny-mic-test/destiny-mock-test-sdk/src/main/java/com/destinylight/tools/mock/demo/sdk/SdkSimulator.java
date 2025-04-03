package com.destinylight.tools.mock.demo.sdk;

import com.destinylight.tools.mock.demo.pojo.crm.vo.PersonInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * 模拟一个SDK
 * </p>
 *
 * @author 郑靖华 (11821967@qq.com)
 * @date 2025/3/11
 */
public class SdkSimulator {
    private static final Logger log = LoggerFactory.getLogger(SdkSimulator.class);

    public String call1() {
        log.info("调用方法[call1()]");
        return "调用方法[call1()]";
    }

    public static String call2() {
        log.info("调用方法[call2()]");
        return "调用方法[call2()]";
    }

    public static PersonInfo call3() {
        log.info("调用方法[call3()]");
        return new PersonInfo().setName("调用方法[cal13()").setId("138481ABC").setAge(99);
    }
}
