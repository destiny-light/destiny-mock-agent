package com.destinylight.tools.mock.demo.controller;

import com.destinylight.tools.mock.demo.pojo.commons.module.adapter.web.rest.ResultDto;
import com.destinylight.tools.mock.demo.pojo.commons.module.adapter.web.trans.CstBseInfDtlQryDto;
import com.destinylight.tools.mock.demo.pojo.commons.module.adapter.web.trans.CstBseInfDto;
import com.destinylight.tools.mock.demo.pojo.crm.vo.PersonInfo;
import com.destinylight.tools.mock.demo.feigns.mockserver.TransferToMockServerFeignClient;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 测试拦截后转发到Mock服务器
 * </p>
 *
 * @author 郑靖华 (11821967@qq.com)
 * @date 2025/3/21
 */
@RestController
@RequestMapping("/mic/toMockServer")
public class TransferToMockServerController {
    private static final Logger log = LoggerFactory.getLogger(TransferToMockServerController.class);

    @Autowired
    private TransferToMockServerFeignClient transferToMockServerFeignClient;

    private final String id = "510227";
    private final String name = "郑靖";

    // 无参的GET
    @GetMapping("/oriTest01")
    public String test01() {
        log.info("调用方法[test01]");
        try {
            String r = transferToMockServerFeignClient.test01();
            log.info("调用方法[test01]成功，返回值[{}]", r);
            return String.format("调用方法[test01]成功，返回值[%s]", r);
        } catch (Exception e) {
            log.error("调用方法[test01]失败，错误信息[{}]", e.getMessage(), e);
            return String.format("调用方法[test01]失败，错误信息[%s]", e.getMessage());
        }
    }

    // 有参数的GET。
    @GetMapping("/oriTest02")
    public String test02() {
        log.info("调用方法[test02]");
        try {
            String r = transferToMockServerFeignClient.test02(id, name);
            log.info("调用方法[test02]成功，返回值[{}]", r);
            return String.format("调用方法[test02]成功，返回值[%s]", r);
        } catch (Exception e) {
            log.error("调用方法[test02]失败，错误信息[{}]", e.getMessage(), e);
            return String.format("调用方法[test02]失败，错误信息[%s]", e.getMessage());
        }
    }

    // 只有路径参数的GET。
    @GetMapping("/oriTest03")
    public String test03() {
        log.info("调用方法[test03]");
        try {
            String r = transferToMockServerFeignClient.test03(id);
            log.info("调用方法[test03]成功，返回值[{}]", r);
            return String.format("调用方法[test03]成功，返回值[%s]", r);
        } catch (Exception e) {
            log.error("调用方法[test03]失败，错误信息[{}]", e.getMessage(), e);
            return String.format("调用方法[test03]失败，错误信息[%s]", e.getMessage());
        }
    }

    // 只有请求参数的GET。
    @GetMapping("/oriTest04")
    public String test04() {
        log.info("调用方法[test04]");
        try {
            String r = transferToMockServerFeignClient.test04(name);
            log.info("调用方法[test04]成功，返回值[{}]", r);
            return String.format("调用方法[test04]成功，返回值[%s]", r);
        } catch (Exception e) {
            log.error("调用方法[test04]失败，错误信息[{}]", e.getMessage(), e);
            return String.format("调用方法[test04]失败，错误信息[%s]", e.getMessage());
        }
    }

    // 没有报文体的POST。
    @GetMapping("/oriTest05")
    public String test05() {
        log.info("调用方法[test05]");
        try {
            String r = transferToMockServerFeignClient.test05();
            log.info("调用方法[test05]成功，返回值[{}]", r);
            return String.format("调用方法[test05]成功，返回值[%s]", r);
        } catch (Exception e) {
            log.error("调用方法[test05]失败，错误信息[{}]", e.getMessage(), e);
            return String.format("调用方法[test05]失败，错误信息[%s]", e.getMessage());
        }
    }

    // 有报文体的POST-报文体是简单的String
    @GetMapping("/oriTest06")
    public String test06() {
        log.info("调用方法[test06]");
        try {
            String r = transferToMockServerFeignClient.test06(id);
            log.info("调用方法[test06]成功，返回值[{}]", r);
            return String.format("调用方法[test06]成功，返回值[%s]", r);
        } catch (Exception e) {
            log.error("调用方法[test06]失败，错误信息[{}]", e.getMessage(), e);
            return String.format("调用方法[test06]失败，错误信息[%s]", e.getMessage());
        }
    }

    // 有报文体的POST-报文体是自定义类型
    @GetMapping(value = "/oriTest07",
            consumes = "application/json;charset=UTF-8",
            produces = "application/json;charset=UTF-8")
    public String test07() {
        log.info("调用方法[test07]");
        try {
            PersonInfo personInfo = new PersonInfo().setAge(38).setId(id).setName(name);
            String r = transferToMockServerFeignClient.test07(personInfo);
            log.info("调用方法[test07]成功，返回值[{}]", r);
            return String.format("调用方法[test07]成功，返回值[%s]", r);
        } catch (Exception e) {
            log.error("调用方法[test07]失败，错误信息[{}]", e.getMessage(), e);
            return String.format("调用方法[test07]失败，错误信息[%s]", e.getMessage());
        }
    }

    // 有HTTP HEADER
    @GetMapping(value = "/oriTest08")
    public String test08() {
        log.info("调用方法[test08]");
        try {
            String r = transferToMockServerFeignClient.test08("HEAD_FROM_MAIN_APP");
            log.info("调用方法[test08]成功，返回值[{}]", r);
            return String.format("调用方法[test08]成功，返回值[%s]", r);
        } catch (Exception e) {
            log.error("调用方法[test08]失败，错误信息[{}]", e.getMessage(), e);
            return String.format("调用方法[test08]失败，错误信息[%s]", e.getMessage());
        }
    }

    // 服务端返回status的值是401
    @GetMapping("/status401")
    String status401() {
        log.info("调用方法[status401]");
        try {
            String r = transferToMockServerFeignClient.status401(id);
            log.info("调用方法[status401]成功，返回值[{}]", r);
            return String.format("调用方法[status401]成功，返回值[%s]", r);
        } catch (Exception e) {
            log.error("调用方法[status401]失败，错误信息[{}]", e.getMessage(), e);
            return String.format("调用方法[status401]失败，错误信息[%s]", e.getMessage());
        }
    }

    /**
     * 返回值为泛型
     */
    @GetMapping(value = "/test09")
    public String test09() {
        log.info("调用方法[test09]");
        try {
            CstBseInfDtlQryDto req = new CstBseInfDtlQryDto();
            req.setCstId("501227");
            req.setCstTyp("F");
            ResultDto<CstBseInfDto> r = transferToMockServerFeignClient.test09(req);
            log.info("调用方法[test09]成功，返回值[{}]", r);
            return String.format("调用方法[test09]成功，返回值[%s]", JSON.toJSONString(r));
        } catch (Exception e) {
            log.error("调用方法[test09]失败，错误信息[{}]", e.getMessage(), e);
            return String.format("调用方法[test09]失败，错误信息[%s]", e.getMessage());
        }
    }
}
