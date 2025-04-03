package com.destinylight.tools.mock.demo.service;

import com.destinylight.tools.mock.demo.feigns.crm.cif.TasteFeignClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 测试FeignClient可能遇到的部分异常。
 * 本类并不是用于Mock拦截的，而是为了适配Feign对这些异常情况的处理。
 * <pre>
 * FeignClient可能遇到的异常，包括：
 * 1. HTTP METHOD不正确。
 * 2. 404。
 * 3. 对方返回500。
 * </pre>
 *
 * @author 郑靖华 (11821967@qq.com)
 * @date 2025/3/20
 */
@Service
public class FeignExceptionService {
    private static final Logger log = LoggerFactory.getLogger(FeignExceptionService.class);

    @Autowired
    private TasteFeignClient tasteFeignClient;

    private final String id = "38919";

    public void execute() {
        test1();
    }

    private void test1() {
        try {
            String str = tasteFeignClient.badHttpMethod(id);
            log.info("调用方法[badHttpMethod()]成功，返回值[{}]", str);
        } catch (Exception e) {
            log.error("调用方法[badHttpMethod()]失败，错误信息[{}]", e.getMessage(), e);
        }
    }
}
