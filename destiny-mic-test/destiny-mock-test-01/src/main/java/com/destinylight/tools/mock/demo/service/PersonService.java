package com.destinylight.tools.mock.demo.service;

import com.destinylight.tools.mock.demo.feigns.crm.cif.OrgFeignClient;
import com.destinylight.tools.mock.demo.feigns.crm.cif.PersonFeignClient;
import com.destinylight.tools.mock.demo.pojo.crm.vo.PersonInfo;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 个人信息服务
 * </p>
 *
 * @author 郑靖华 (11821967@qq.com)
 * @date 2025/3/11
 */
@Service
public class PersonService {
    private static final Logger log = LoggerFactory.getLogger(PersonService.class);

    @Autowired
    private PersonFeignClient personFeignclient;
    @Autowired
    private OrgFeignClient orgFeignClient;

    /**
     * 大测试【个人信息服务】
     */
    public void personInfoTest() {
        String id = "5102271";

        log.info("----  调用将要被Mock的FeignClient的服务  ----");
        log.info("调用FeignClient的服务，取得[{}]", "个人信息");
        PersonInfo detail = personFeignclient.detail(id);
        log.info("调用FeignClient的服务，取得[{}]的值[{}]", "个人信息", JSON.toJSONString(detail));

        log.info("调用FeignClient的服务，取得[{}]", "姓名");
        String name = personFeignclient.name(id);
        log.info("调用FeignClient的服务，取得[{}]的值[{}]", "姓名", name);

        log.info("调用FeignClient的服务，取得[{}]", "年龄");
        Integer age = personFeignclient.age(id);
        log.info("调用FeignClient的服务，取得[{}]的值[{}]", "年龄", age);

        log.info("调用FeignClient的服务，取得[{}]", "email");
        String email = personFeignclient.email(id);
        log.info("调用FeignClient的服务，取得[{}]的值[{}]", "email", email);

        log.info("----  调用不被Mock的FeignClient的服务  ----");
        id = "610000";
        log.info("调用FeignClient的服务，取得[{}]", "个人信息");
        detail = orgFeignClient.detail(id);
        log.info("调用FeignClient的服务，取得[{}]的值[{}]", "个人信息", JSON.toJSONString(detail));

        log.info("调用FeignClient的服务，取得[{}]", "姓名");
        name = orgFeignClient.name(id);
        log.info("调用FeignClient的服务，取得[{}]的值[{}]", "姓名", name);

        log.info("调用FeignClient的服务，取得[{}]", "年龄");
        age = orgFeignClient.age(id);
        log.info("调用FeignClient的服务，取得[{}]的值[{}]", "年龄", age);

        log.info("调用FeignClient的服务，取得[{}]", "email");
        email = orgFeignClient.email(id);
        log.info("调用FeignClient的服务，取得[{}]的值[{}]", "email", email);

        log.info("调用方法");
        String aa = abc(1, 2, 99090, "3S");
        log.info("调用方法，取得的值[{}]", aa);
        abc1(1, 2, 99090, "3S");
    }

    public String abc(int a, Integer b, int c, String d) {
        log.info("在原始方法[abc()]中, a[{}], b[{}], c[{}], d[{}],[{}]", a, b, c, d, this.getClass().getName());
        return "来自于原生";
    }

    public void abc1(int a, Integer b, int c, String d) {
        log.info("在原始方法[abc1()]中, a[{}], b[{}], c[{}], d[{}],[{}]", a, b, c, d, this.getClass().getName());
    }
}
