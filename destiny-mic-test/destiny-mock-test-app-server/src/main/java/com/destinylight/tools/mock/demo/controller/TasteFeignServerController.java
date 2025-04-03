package com.destinylight.tools.mock.demo.controller;

import com.destinylight.tools.mock.demo.pojo.commons.module.adapter.web.rest.ResultDto;
import com.destinylight.tools.mock.demo.pojo.commons.module.adapter.web.trans.CstBseInfDtlQryDto;
import com.destinylight.tools.mock.demo.pojo.commons.module.adapter.web.trans.CstBseInfDto;
import com.alibaba.fastjson.JSON;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
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
 * 尝试FeignClient各种情况的服务端
 * </p>
 *
 * @author 郑靖华 (11821967@qq.com)
 * @date 2025/3/20
 */
@RequestMapping("/crm/cif/order")
@RestController
public class TasteFeignServerController {
    private static final Logger log = LoggerFactory.getLogger(TasteFeignServerController.class);

    // HTTP METHOD不正确。服务方提供的是GET，但我们尝试用POST去访问。
    @GetMapping("/badHttpMethod")
    String badHttpMethod(@RequestParam("id") String id) {
        log.info("进入方法[badHttpMethod], id[{}]", id);
        return "来自于第三方服务端[" + id + "]";
    }

    @GetMapping("/internalError")
    String internalError(@RequestParam("id") String id) {
        log.info("进入方法[internalError], id[{}]", id);
        throw new IllegalStateException("来自于第三方服务端的异常");
    }

    // 客户端设置了某个HEADER，验证该HEADER是否可以传递到第三方。
    @GetMapping("/withHeader")
    String withHeader(@RequestHeader(value = "Hello", required = false) String hello,
                      @RequestParam("id") String id) {
        log.info("进入方法[withHeader], id[{}]", id);
        return hello == null ? "第三方应用没有收到FeignClient转发的header" : hello;
    }

    // 请求参数没有给RequestBody注解，这种情况是正确的，被视作给了RequestBody注解
    @PostMapping("/postWithoutAnnotation")
    String postWithoutAnnotation(String id) {
        log.info("进入方法[postWithoutAnnotation], id[{}]", id);
        return "第三方应用收到请求体没有给注解";
    }

    // 请求参数有给RequestBody注解
    @PostMapping("/postWithAnnotation")
    String postWithAnnotation(@RequestBody String id) {
        log.info("进入方法[postWithAnnotation], id[{}]", id);
        return "第三方应用收到请求体有给RequestBody注解";
    }

    // 请求参数有给多个RequestBody注解，这种情况在FeignClient里是不被允许的
    @PostMapping("/postWithMultiAnnotation")
    String postWithMultiAnnotation(@RequestBody String id, @RequestBody String name) {
        log.info("进入方法[postWithMultiAnnotation], id[{}], name[{}]", id, name);
        return "第三方应用收到请求体有给多个RequestBody注解";
    }

    // 请求参数有多个有RequestBody注解
    @PostMapping("/postWithoutMultiAnnotation")
    String postWithoutMultiAnnotation(String id, String name) {
        log.info("进入方法[postWithoutMultiAnnotation], id[{}], name[{}]", id, name);
        return "第三方应用收到请求参数有多个有RequestBody注解";
    }

    // 请求参数有给1个RequestBody注解，同时也有至少一个没有RequestBody注解
    @PostMapping("/postWithAndWithoutAnnotation")
    String postWithAndWithoutAnnotation(@RequestBody String id, String name) {
        log.info("进入方法[postWithAndWithoutAnnotation], id[{}], name[{}]", id, name);
        return "第三方应用收到请求体有给1个RequestBody注解以及多个没有RequestBody注解";
    }

    // 客户端和服务端都有RequestHeader注解
    @GetMapping("/haveHeadBoth")
    String haveHeadBoth(@RequestHeader(name = "CustHead", required = false, defaultValue = "服务端的默认值") String head,
                        @RequestParam("id") String id) {
        log.info("进入方法[haveHeadBoth], head[{}], id[{}]", head, id);
        return "第三方应用收到客户端和服务端都有RequestHeader注解";
    }

    // 仅客户端有RequestHeader注解
    @GetMapping("/hasHeadClient")
    String hasHeadClient(@RequestParam("id") String id) {
        log.info("进入方法[hasHeadClient], id[{}]", id);
        return "第三方应用收到仅客户端有RequestHeader注解";
    }

    // 仅服务端有RequestHeader注解
    @GetMapping("/hasHeadServer")
    String hasHeadServer(@RequestHeader(name = "CustHead", required = false, defaultValue = "服务端的默认值") String head,
                         @RequestParam("id") String id) {
        log.info("进入方法[haveHeadBoth], head[{}], id[{}]", head, id);
        return "第三方应用收到仅服务端有RequestHeader注解";
    }

    // 服务端返回的报文体中的某个字段，与客户端使用的实体类的字段类型不同
    @GetMapping(value = "/differentDataType",
            consumes = "application/json;charset=UTF-8",
            produces = "application/json;charset=UTF-8")
    Data1 differentDataType() {
        log.info("进入方法[differentDataType]");
        Data1 data1 = new Data1();
        data1.fld1 = "客户端和服务端都是\"String\"";
        data1.fld2 = "10";
        data1.fld3 = 4;
        return data1;
    }

    public static class Data1 {
        // 客户端和服务端都是"String"
        String fld1;
        // 客户端是"int"，服务端是"String"
        String fld2;
        // 客户端是"String"，服务端是"int"
        int fld3;

        public String getFld1() {
            return fld1;
        }

        public void setFld1(String fld1) {
            this.fld1 = fld1;
        }

        public String getFld2() {
            return fld2;
        }

        public void setFld2(String fld2) {
            this.fld2 = fld2;
        }

        public int getFld3() {
            return fld3;
        }

        public void setFld3(int fld3) {
            this.fld3 = fld3;
        }
    }

    // 服务端返回status的值是400
    @GetMapping("/status400")
    String status400(HttpServletResponse response, @RequestParam("id") String id) {
        log.info("进入方法[status400], id[{}]", id);
        response.setStatus(400);
        return "来自于第三方服务端[" + id + "]";
    }

    // 服务端返回status的值是401
    @GetMapping("/status401")
    String status401(HttpServletResponse response, @RequestParam("id") String id) {
        log.info("进入方法[status401], id[{}]", id);
        response.setStatus(401);
        return "来自于第三方服务端[" + id + "]";
    }

    // 服务端返回status的值是403
    @GetMapping("/status403")
    String status403(HttpServletResponse response, @RequestParam("id") String id) {
        log.info("进入方法[status403], id[{}]", id);
        response.setStatus(403);
        return "来自于第三方服务端[" + id + "]";
    }

    // 服务端返回status的值是404
    @GetMapping("/status404")
    String status404(HttpServletResponse response, @RequestParam("id") String id) {
        log.info("进入方法[status404], id[{}]", id);
        response.setStatus(404);
        return "来自于第三方服务端[" + id + "]";
    }

    // 服务端返回status的值是405
    @GetMapping("/status405")
    String status405(HttpServletResponse response, @RequestParam("id") String id) {
        log.info("进入方法[status405], id[{}]", id);
        response.setStatus(405);
        return "来自于第三方服务端[" + id + "]";
    }

    // 服务端返回status的值是406
    @GetMapping("/status406")
    String status406(HttpServletResponse response, @RequestParam("id") String id) {
        log.info("进入方法[status406], id[{}]", id);
        response.setStatus(406);
        return "来自于第三方服务端[" + id + "]";
    }

    // 服务端返回status的值是409
    @GetMapping("/status409")
    String status409(HttpServletResponse response, @RequestParam("id") String id) {
        log.info("进入方法[status409], id[{}]", id);
        response.setStatus(409);
        return "来自于第三方服务端[" + id + "]";
    }

    // 服务端返回status的值是410
    @GetMapping("/status410")
    String status410(HttpServletResponse response, @RequestParam("id") String id) {
        log.info("进入方法[status410], id[{}]", id);
        response.setStatus(410);
        return "来自于第三方服务端[" + id + "]";
    }

    // 服务端返回status的值是415
    @GetMapping("/status415")
    String status415(HttpServletResponse response, @RequestParam("id") String id) {
        log.info("进入方法[status415], id[{}]", id);
        response.setStatus(415);
        return "来自于第三方服务端[" + id + "]";
    }

    // 服务端返回status的值是429
    @GetMapping("/status429")
    String status429(HttpServletResponse response, @RequestParam("id") String id) {
        log.info("进入方法[status429], id[{}]", id);
        response.setStatus(429);
        return "来自于第三方服务端[" + id + "]";
    }

    // 服务端返回status的值是422
    @GetMapping("/status422")
    String status422(HttpServletResponse response, @RequestParam("id") String id) {
        log.info("进入方法[status422], id[{}]", id);
        response.setStatus(422);
        return "来自于第三方服务端[" + id + "]";
    }

    // 服务端返回status的值是4XX
    @GetMapping("/status4XX")
    String status4XX(HttpServletResponse response, @RequestParam("id") String id) {
        log.info("进入方法[status4XX], id[{}]", id);
        response.setStatus(486);
        return "来自于第三方服务端[" + id + "]";
    }

    // 服务端返回status的值是500
    @GetMapping("/status500")
    String status500(HttpServletResponse response, @RequestParam("id") String id) {
        log.info("进入方法[status500], id[{}]", id);
        response.setStatus(500);
        return "来自于第三方服务端[" + id + "]";
    }

    // 服务端返回status的值是501
    @GetMapping("/status501")
    String status501(HttpServletResponse response, @RequestParam("id") String id) {
        log.info("进入方法[status501], id[{}]", id);
        response.setStatus(501);
        return "来自于第三方服务端[" + id + "]";
    }

    // 服务端返回status的值是502
    @GetMapping("/status502")
    String status502(HttpServletResponse response, @RequestParam("id") String id) {
        log.info("进入方法[status502], id[{}]", id);
        response.setStatus(502);
        return "来自于第三方服务端[" + id + "]";
    }

    // 服务端返回status的值是503
    @GetMapping("/status503")
    String status503(HttpServletResponse response, @RequestParam("id") String id) {
        log.info("进入方法[status503], id[{}]", id);
        response.setStatus(503);
        return "来自于第三方服务端[" + id + "]";
    }

    // 服务端返回status的值是504
    @GetMapping("/status504")
    String status504(HttpServletResponse response, @RequestParam("id") String id) {
        log.info("进入方法[status504], id[{}]", id);
        response.setStatus(504);
        return "来自于第三方服务端[" + id + "]";
    }

    // 服务端返回status的值是5XX
    @GetMapping("/status5XX")
    String status5XX(HttpServletResponse response, @RequestParam("id") String id) {
        log.info("进入方法[status5XX], id[{}]", id);
        response.setStatus(586);
        return "来自于第三方服务端[" + id + "]";
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
