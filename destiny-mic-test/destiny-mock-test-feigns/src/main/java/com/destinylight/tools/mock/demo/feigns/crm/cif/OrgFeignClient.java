package com.destinylight.tools.mock.demo.feigns.crm.cif;

import com.destinylight.tools.mock.demo.feigns.Const;
import com.destinylight.tools.mock.demo.pojo.crm.vo.PersonInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * <p>
 * 本接口类的方法，与{@link PersonFeignClient}中的方法完全相同，但本接口类的方法没有被配置成Mock拦截目标
 * </p>
 *
 * @author 郑靖华 (11821967@qq.com)
 * @date 2025/3/13
 */
@FeignClient(name = "app-server-crm-simulator1", url = Const.SIMULATOR_URL)
public interface OrgFeignClient {
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
