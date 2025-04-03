package com.destinylight.tools.mock.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * 将被动态代理的类
 * </p>
 *
 * @author 郑靖华 (11821967@qq.com)
 * @date 2025/3/11
 */
public class SomethingForProxy {
    private static final Logger log = LoggerFactory.getLogger(SomethingForProxy.class);

    public void shouldProxy() {
        log.info("需要代理的方法，原始方法中的内容");
    }

    public void withoutProxy() {
        log.info("不需要代理的方法的内容");
    }
}
