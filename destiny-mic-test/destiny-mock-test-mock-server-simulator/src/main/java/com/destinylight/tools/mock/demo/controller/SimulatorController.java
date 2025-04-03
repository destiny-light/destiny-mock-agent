package com.destinylight.tools.mock.demo.controller;

import com.destinylight.tools.mock.demo.pojo.commons.module.adapter.web.rest.ResultDto;
import com.destinylight.tools.mock.demo.pojo.commons.module.adapter.web.trans.CstBseInfDtlQryDto;
import com.destinylight.tools.mock.demo.pojo.commons.module.adapter.web.trans.CstBseInfDto;
import com.destinylight.tools.mock.demo.pojo.crm.vo.PersonInfo;
import com.alibaba.fastjson.JSON;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 模拟元素设计平台，但这里的各个URL是固定的
 * </p>
 *
 * @author 郑靖华 (11821967@qq.com)
 * @date 2025/3/20
 */
@RestController
@RequestMapping("/mock/1/simulator")
public class SimulatorController {
    private static final Logger log = LoggerFactory.getLogger(SimulatorController.class);

    // 无参的GET。
    @GetMapping("/test01")
    public String test01() {
        log.info("调用方法[test01]成功");
        return JSON.toJSONString("来自于元素设计平台的模拟字符串");
    }

    // 有参数的GET。
    @GetMapping("/{id}/test02")
    public String test02(@PathVariable("id") String id,
                         @RequestParam(name = "name", required = false) String name) {
        log.info("调用方法[test02]成功, id[{}], name[{}]", id, name);
        return JSON.toJSONString("来自于元素设计平台的模拟字符串");
    }

    // 只有路径参数的GET。
    @GetMapping("/{id}/test03")
    public String test03(@PathVariable("id") String id) {
        log.info("调用方法[test03]成功, id[{}]", id);
        return JSON.toJSONString("来自于元素设计平台的模拟字符串");
    }

    // 只有请求参数的GET。
    @GetMapping("/test04")
    public String test04(@RequestParam(name = "name", required = false) String name) {
        log.info("调用方法[test04]成功, name[{}]", name);
        return JSON.toJSONString("来自于元素设计平台的模拟字符串");
    }

    // 没有报文体的POST
    @PostMapping("/test05")
    public String test05() {
        log.info("调用方法[test05]成功, 没有报文体");
        return JSON.toJSONString("来自于元素设计平台的模拟字符串");
    }

    // 有报文体的POST-报文体为简单的String
    @PostMapping(value = "/test06",
            consumes = "application/json;charset=UTF-8",
            produces = "application/json;charset=UTF-8")
    public String test06(@RequestBody String id) {
        log.info("调用方法[test06]成功, 有报文体, id[{}]", id);
        return JSON.toJSONString("来自于元素设计平台的模拟字符串");
    }

    // 有报文体的POST-报文体为自定义类型
    @PostMapping(value = "/test07",
            consumes = "application/json;charset=UTF-8",
            produces = "application/json;charset=UTF-8")
    public String test07(@RequestBody PersonInfo personInfo) {
        log.info("调用方法[test07]成功, 有报文体, personInfo[{}]", JSON.toJSONString(personInfo));
        return JSON.toJSONString("来自于元素设计平台的模拟字符串");
    }

    /**
     * 有HTTP HEADER
     *
     * @param head  主应用加上的HEAD，也是本FeignClient要求的参数之一
     * @param hello 从客户端传给主应用的HEAD，再由主应用转发给Mock服务器
     * @return 响应信息
     */
    @GetMapping(value = "/test08")
    public String test08(@RequestHeader(name = "CustHead", required = false, defaultValue = "HEAD_FROM_SERVER_1") String head,
                         @RequestHeader(name = "Hello", required = false, defaultValue = "HEAD_FROM_SERVER_2") String hello,
                         @RequestHeader(name = "serviceName", required = false, defaultValue = "HEAD_FROM_SERVER_3") String svcName) {
        log.info("调用方法[test08]成功, 有HTTP HEADER, CustHead[{}], hello[{}], svcName[{}]", head, hello, svcName);
        return JSON.toJSONString("来自于元素设计平台的模拟字符串");
    }

    // 服务端返回status的值是401
    @GetMapping("/status401")
    String status401(HttpServletResponse response, @RequestParam("id") String id) {
        log.info("调用方法[status401]成功, id[{}]", id);
        response.addHeader("metaMock", "true");
        response.setStatus(401);
        return JSON.toJSONString("服务端返回status的值是401");
    }

    /**
     * 返回值为泛型
     *
     * @param req 请求报文
     * @return 响应信息
     */
    @PostMapping(value = "/test09",
            consumes = "application/json;charset=UTF-8",
            produces = "application/json;charset=UTF-8")
    public ResultDto<CstBseInfDto> test09(CstBseInfDtlQryDto req) {
        log.info("调用方法[test09]成功, 返回值为泛型, 请求报文[{}]", JSON.toJSONString(req));

        CstBseInfDto respBody = new CstBseInfDto();
        respBody.setCstId("模拟值: 客户号");
        respBody.setCstTyp("模拟值: 客户类型");
        respBody.setCertNo("模拟值: 证件号码");
        respBody.setIdtcRskCtrlNo("模拟值: 同一风险控制号");
        respBody.setCstSrcChnl("模拟值: 客户来源渠道");
        respBody.setBscInfCplInd("模拟值: 基本信息完整标志");
        respBody.setLgpId("模拟值: 法人编号");
        respBody.setBelMngLgpId("模拟值: 归属管理法人编号");
        respBody.setDelInd("模拟值: 删除标识");
        respBody.setLastUpdPsn("模拟值: 最后更新人");
        respBody.setLastUpdOrg("模拟值: 最后更新机构");
        respBody.setLastUpdTm(LocalDateTime.parse("2025-03-25T14:16:30"));
        respBody.setBthDt(LocalDate.parse("1974-10-20"));
        respBody.setActiCtlerCstId("模拟值: 实际控制人客户号");
        respBody.setBtchNO("模拟值: 批次号");
        respBody.setSysRegPsn("模拟值: 系统登记人");
        respBody.setSysRegOrg("模拟值: 系统登记机构");
        respBody.setSysRegTm(LocalDateTime.parse("2025-02-16T12:16:30"));
        respBody.setTstSts(5);
        respBody.setCstLrgClss("模拟值: 客户大类");
        respBody.setCertTyp("模拟值: 证件类型");
        respBody.setCstNm("模拟值: 客户名称");
        respBody.setBnfrAddr("模拟值: 受益人地址");
        respBody.setTstTyp("模拟值: 测试类型");

        ResultDto<CstBseInfDto> resp = new ResultDto<>();
        resp.code("10").message("成功了").data(respBody);
        log.info("调用方法[test09]成功, 返回值为泛型, 响应报文[{}]", JSON.toJSONString(resp));
        return resp;
    }
}
