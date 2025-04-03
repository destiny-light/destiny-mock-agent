package com.destinylight.tools.mock.demo.controller;

import com.destinylight.tools.mock.demo.pojo.crm.vo.PersonInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 模拟的应用服务器上提供的客户信息服务接口
 * </p>
 *
 * @author 郑靖华 (11821967@qq.com)
 * @date 2025/3/11
 */
@RequestMapping("/crm/cif")
@RestController
public class PersonController {
    private static final Logger log = LoggerFactory.getLogger(PersonController.class);

    @GetMapping("/order/detail")
    public PersonInfo detail(@RequestParam("id") String id) {
        log.info("调用方法[detail], id[{}]", id);
        return new PersonInfo().setAge(20).setId("6199347").setName("小平同志");
    }

    /**
     * 查询用户姓名
     *
     * @param id 用户ID
     * @return 姓名
     */
    @GetMapping("/order/{id}/name")
    public String name(@PathVariable("id") String id) {
        log.info("调用方法[name], id[{}]", id);
        return "毛主席";
    }

    /**
     * 查询用户年龄
     *
     * @param id 用户ID
     * @return 年龄
     */
    @GetMapping("/order/{id}/age")
    public Integer age(@PathVariable("id") String id) {
        log.info("调用方法[age], id[{}]", id);
        return 18;
    }

    /**
     * 查询用户email
     *
     * @param id 用户ID
     * @return email
     */
    @GetMapping("/order/{id}/email")
    public String email(@PathVariable("id") String id) {
        log.info("调用方法[email], id[{}]", id);
        return "1182@qq.com";
    }
}
