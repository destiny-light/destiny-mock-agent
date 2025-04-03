package com.destinylight.tools.mock.demo.feigns.crm.cif;

import com.destinylight.tools.mock.demo.feigns.Const;
import com.destinylight.tools.mock.demo.pojo.crm.vo.PersonInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 用于测试的FeignClient服务接口：
 * <pre>
 * 1.返回值分别是：
 * 1.2.void
 * 1.3.String
 * 1.4.Integer
 * 1.5.Double
 * 1.6.Boolean
 * 1.7.List&lt;string&gt;
 * 1.8.Map&lt;string, POJO&gt;
 * 1.9.自定义POJO
 * 2.方法名上有Mapping注解.
 * 2.1.无路径参数、无请求参数的GET.
 * 2.2.有路径参数、无请求参数的GET.
 * 2.3.无路径参数、有请求参数的GET.
 * 2.4.有路径参数、有请求参数的GET.
 * 2.5.POST
 * 2.6.DELETE
 * 2.7.有2个相同名称的方法
 * </pre>
 *
 * @author 郑靖华 (11821967@qq.com)
 * @date 2025/3/11
 */
@FeignClient(name = "app-server-crm-simulator", url = Const.SIMULATOR_URL)
// @RequestMapping annotation not allowed on @FeignClient interfaces
// 不能使用：@RequestMapping("/crm/cif")
public interface PersonFeignClient {
    @GetMapping("/crm/cif/order/detail")
    PersonInfo detail(@RequestParam("id") String id);

    /**
     * 查询用户姓名
     *
     * @param id 用户ID
     * @return 姓名
     */
    @GetMapping("/crm/cif/order/{id}/name")
    String name(@PathVariable("id") String id);

    /**
     * 查询用户email
     *
     * @param id 用户ID
     * @return email
     */
    @GetMapping("/crm/cif/order/{id}/email")
    String email(@PathVariable("id") String id);

    /**
     * 查询用户年龄
     *
     * @param id 用户ID
     * @return 年龄
     */
    @GetMapping("/crm/cif/order/{id}/age")
    Integer age(@PathVariable("id") String id);
}
