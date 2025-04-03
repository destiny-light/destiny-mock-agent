package com.destinylight.tools.mock.demo.feigns.mockserver;

import com.destinylight.tools.mock.demo.feigns.Const;
import com.destinylight.tools.mock.demo.pojo.commons.module.adapter.web.rest.ResultDto;
import com.destinylight.tools.mock.demo.pojo.commons.module.adapter.web.trans.CstBseInfDtlQryDto;
import com.destinylight.tools.mock.demo.pojo.commons.module.adapter.web.trans.CstBseInfDto;
import com.destinylight.tools.mock.demo.pojo.crm.vo.PersonInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * <p>
 * 这些FeignClient调用，将会被转发到元素设计平台模拟服务器上
 * </p>
 *
 * @author 郑靖华 (11821967@qq.com)
 * @date 2025/3/21
 */
@FeignClient(name = "mock-server-simulator1", url = Const.SIMULATOR_URL, path = "/simulator")
public interface TransferToMockServerFeignClient {
    // 无参的GET。
    @GetMapping("/test01")
    String test01();

    // 有参数的GET。
    @GetMapping("/{id}/test02")
    String test02(@PathVariable("id") String id,
                  @RequestParam(name = "name", required = false) String name);

    // 只有路径参数的GET。
    @GetMapping("/{id}/test03")
    String test03(@PathVariable("id") String id);

    // 只有请求参数的GET。
    @GetMapping("/test04")
    String test04(@RequestParam(name = "name", required = false) String name);

    // 没有报文体的POST
    @PostMapping("/test05")
    String test05();

    // 有报文体的POST
    @PostMapping("/test06")
    String test06(@RequestBody String id);

    // 有报文体的POST
    @PostMapping(value = "/test07")
    String test07(@RequestBody PersonInfo personInfo);

    /**
     * 有HTTP HEADER
     *
     * @param head 主应用加上的HEAD，也是本FeignClient要求的参数之一
     * @return 响应信息
     */
    @GetMapping(value = "/test08")
    String test08(@RequestHeader(name = "CustHead") String head);

    // 服务端返回status的值是401
    @GetMapping("/status401")
    String status401(@RequestParam("id") String id);

    /**
     * 返回值为泛型
     *
     * @param req 请求报文
     * @return 响应信息
     */
    @PostMapping(value = "/test09",
            consumes = "application/json;charset=UTF-8",
            produces = "application/json;charset=UTF-8")
    ResultDto<CstBseInfDto> test09(CstBseInfDtlQryDto req);
}
