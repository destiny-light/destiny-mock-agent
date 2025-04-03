package com.destinylight.tools.mock.demo.controller;

import com.destinylight.tools.mock.demo.pojo.commons.module.adapter.web.rest.ResultDto;
import com.destinylight.tools.mock.demo.pojo.commons.module.adapter.web.trans.CstBseInfDtlQryDto;
import com.destinylight.tools.mock.demo.pojo.commons.module.adapter.web.trans.CstBseInfDto;
import com.destinylight.tools.mock.demo.feigns.crm.cif.TasteFeignClient;
import com.destinylight.tools.mock.demo.pojo.crm.vo.DataOfDifferentFieldType;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 尝试FeignClient的各种情况
 * </p>
 *
 * @author 郑靖华 (11821967@qq.com)
 * @date 2025/3/20
 */
@RestController
@RequestMapping("/mic")
public class TasteFeignController {
    private static final Logger log = LoggerFactory.getLogger(TasteFeignController.class);

    @Autowired
    private TasteFeignClient tasteFeignClient;

    private final String id = "38919";
    private final String name = "郑靖";
    private final String head = "HEAD_FROM_MAIN_APP";

    /**
     * 返回404。服务方没有提供这个服务接口。
     * 异常信息如下所示：
     * <p>
     * feign.FeignException$NotFound: [404] during [GET] to [http://localhost:11201/crm/cif/order/nonExist404?id=38919] [TasteFeignClient#nonExist404(String)]: [{"timestamp":"2025-03-20T07:09:07.863+00:00","status":404,"error":"Not Found","path":"/crm/cif/order/nonExist404"}]
     * </p>
     */
    @GetMapping("/nonExist404")
    String nonExist404() {
        try {
            String resp = tasteFeignClient.nonExist404(id);
            log.error("调用方法[nonExist404]成功，返回值[{}]", resp);
        } catch (Exception e) {
            log.error("调用方法[nonExist404]失败，错误信息[{}]", e.getMessage(), e);
        }
        return "SUCCESS";
    }

    /**
     * HTTP METHOD不正确。服务方提供的是GET，但我们尝试用POST去访问。
     * 异常信息如下所示：
     * <p>
     * feign.FeignException$MethodNotAllowed: [405] during [POST] to [http://localhost:11201/crm/cif/order/badHttpMethod?id=38919] [TasteFeignClient#badHttpMethod(String)]: [{"timestamp":"2025-03-20T07:19:06.685+00:00","status":405,"error":"Method Not Allowed","path":"/crm/cif/order/badHttpMethod"}]
     * </p>
     */
    @GetMapping("/badHttpMethod")
    String badHttpMethod() {
        try {
            String resp = tasteFeignClient.badHttpMethod(id);
            log.error("调用方法[badHttpMethod]成功，返回值[{}]", resp);
        } catch (Exception e) {
            log.error("调用方法[badHttpMethod]失败，错误信息[{}]", e.getMessage(), e);
        }
        return "SUCCESS";
    }

    /**
     * 服务方提供了这个服务，但抛出了异常。
     * 异常信息如下所示：
     * <p>
     * feign.FeignException$InternalServerError: [500] during [GET] to [http://localhost:11201/crm/cif/order/internalError?id=38919] [TasteFeignClient#internalError(String)]: [{"timestamp":"2025-03-20T07:22:20.137+00:00","status":500,"error":"Internal Server Error","path":"/crm/cif/order/internalError"}]
     * </p>
     */
    @GetMapping("/internalError")
    String internalError() {
        try {
            String resp = tasteFeignClient.internalError(id);
            log.error("调用方法[internalError]成功，返回值[{}]", resp);
        } catch (Exception e) {
            log.error("调用方法[internalError]失败，错误信息[{}]", e.getMessage(), e);
        }
        return "SUCCESS";
    }

    /**
     * 客户端设置了某个HEADER，验证该HEADER是否可以传递到第三方。
     */
    @GetMapping("/withHeader")
    String withHeader(@RequestHeader(value = "Hello", required = false) String hello) {
        try {
            log.info("客户端传入的HEADER里的参数[Hello]的值是[{}]", hello);
            String resp = tasteFeignClient.withHeader(id);
            log.error("调用方法[withHeader]成功，返回值[{}]", resp);
        } catch (Exception e) {
            log.error("调用方法[withHeader]失败，错误信息[{}]", e.getMessage(), e);
        }
        return "SUCCESS";
    }


    // 服务端返回的报文体中的某个字段，与客户端使用的实体类的字段类型不同
    @GetMapping(value = "/differentDataType")
    public String differentDataType() {
        try {
            DataOfDifferentFieldType resp = tasteFeignClient.differentDataType();
            log.info("调用方法[differentDataType]成功，返回值[{}]", JSON.toJSONString(resp));
        } catch (Exception e) {
            log.error("调用方法[differentDataType]失败，错误信息[{}]", e.getMessage(), e);
        }
        return "SUCCESS";
    }

    /**
     * 请求体没有给注解
     */
    @GetMapping("/postWithoutAnnotation")
    String postWithoutAnnotation() {
        try {
            String resp = tasteFeignClient.postWithoutAnnotation(id);
            log.error("调用方法[postWithoutAnnotation]成功，返回值[{}]", resp);
        } catch (Exception e) {
            log.error("调用方法[postWithoutAnnotation]失败，错误信息[{}]", e.getMessage(), e);
        }
        return "SUCCESS";
    }

    /**
     * 请求体有给RequestBody注解
     */
    @GetMapping("/postWithAnnotation")
    String postWithAnnotation() {
        try {
            String resp = tasteFeignClient.postWithAnnotation(id);
            log.error("调用方法[postWithoutAnnotation]成功，返回值[{}]", resp);
        } catch (Exception e) {
            log.error("调用方法[postWithoutAnnotation]失败，错误信息[{}]", e.getMessage(), e);
        }
        return "SUCCESS";
    }

    /**
     * 请求体有给多个RequestBody注解
     */
    @GetMapping("/postWithMultiAnnotation")
    String postWithMultiAnnotation() {
        try {
            // 不能调用：String resp = tasteFeignClient.postWithMultiAnnotation(id, name);
            log.error("调用方法[postWithoutAnnotation]成功，实际上，这个方法根本不能存在，否则会导致启动失败");
        } catch (Exception e) {
            log.error("调用方法[postWithoutAnnotation]失败，错误信息[{}]", e.getMessage(), e);
        }
        return "SUCCESS";
    }

    /**
     * 请求参数有多个有RequestBody注解
     */
    @PostMapping("/postWithoutMultiAnnotation")
    String postWithoutMultiAnnotation() {
        try {
            // 不能调用：String resp = tasteFeignClient.postWithoutMultiAnnotation(id, name);
            log.error("调用方法[postWithoutMultiAnnotation]成功，实际上，这个方法根本不能存在，否则会导致启动失败");
        } catch (Exception e) {
            log.error("调用方法[postWithoutMultiAnnotation]失败，错误信息[{}]", e.getMessage(), e);
        }
        return "SUCCESS";
    }

    /**
     * 请求参数有给1个RequestBody注解，同时也有至少一个没有RequestBody注解
     */
    @PostMapping("/postWithAndWithoutAnnotation")
    String postWithAndWithoutAnnotation() {
        try {
            // 不能调用：String resp = tasteFeignClient.postWithAndWithoutAnnotation(id, name);
            log.error("调用方法[postWithAndWithoutAnnotation]成功，实际上，这个方法根本不能存在，否则会导致启动失败");
        } catch (Exception e) {
            log.error("调用方法[postWithAndWithoutAnnotation]失败，错误信息[{}]", e.getMessage(), e);
        }
        return "SUCCESS";
    }

    // 客户端和服务端都有RequestHeader注解
    @GetMapping("/haveHeadBoth")
    String haveHeadBoth() {
        try {
            String resp = tasteFeignClient.haveHeadBoth(head, id);
            log.error("调用方法[haveHeadBoth]成功，返回值[{}]", resp);
        } catch (Exception e) {
            log.error("调用方法[haveHeadBoth]失败，错误信息[{}]", e.getMessage(), e);
        }
        return "SUCCESS";
    }

    // 仅客户端有RequestHeader注解
    @GetMapping("/hasHeadClient")
    String hasHeadClient() {
        try {
            String resp = tasteFeignClient.hasHeadClient(head, id);
            log.error("调用方法[hasHeadClient]成功，返回值[{}]", resp);
        } catch (Exception e) {
            log.error("调用方法[hasHeadClient]失败，错误信息[{}]", e.getMessage(), e);
        }
        return "SUCCESS";
    }

    // 仅服务端有RequestHeader注解
    @GetMapping("/hasHeadServer")
    String hasHeadServer() {
        try {
            String resp = tasteFeignClient.hasHeadServer(id);
            log.error("调用方法[hasHeadServer]成功，返回值[{}]", resp);
        } catch (Exception e) {
            log.error("调用方法[hasHeadServer]失败，错误信息[{}]", e.getMessage(), e);
        }
        return "SUCCESS";
    }

    /**
     * 服务端返回status的值是400
     * 异常信息如下:
     * <p>
     * feign.FeignException$BadRequest: [400] during [GET] to [http://localhost:11201/crm/cif/order/status400?id=38919] [TasteFeignClient#status400(String)]: [来自于第三方服务端[38919]]
     * </p>
     */
    @GetMapping("/tasteStatus400")
    String status400() {
        try {
            String resp = tasteFeignClient.status400(id);
            log.error("调用方法[status400]成功，返回值[{}]", resp);
        } catch (Exception e) {
            log.error("调用方法[status400]失败，错误信息[{}]", e.getMessage(), e);
        }
        return "SUCCESS";
    }

    /**
     * 服务端返回status的值是401
     * 异常信息如下:
     * <p>
     * feign.FeignException$Unauthorized: [401] during [GET] to [http://localhost:11201/crm/cif/order/status401?id=38919] [TasteFeignClient#status401(String)]: [来自于第三方服务端[38919]]
     * </p>
     */
    @GetMapping("/tasteStatus401")
    String status401() {
        try {
            String resp = tasteFeignClient.status401(id);
            log.error("调用方法[status401]成功，返回值[{}]", resp);
        } catch (Exception e) {
            log.error("调用方法[status401]失败，错误信息[{}]", e.getMessage(), e);
        }
        return "SUCCESS";
    }

    /**
     * 服务端返回status的值是403
     * 异常信息如下:
     * <p>
     * feign.FeignException$Forbidden: [403] during [GET] to [http://localhost:11201/crm/cif/order/status403?id=38919] [TasteFeignClient#status403(String)]: [来自于第三方服务端[38919]]
     * </p>
     */
    @GetMapping("/tasteStatus403")
    String status403() {
        try {
            String resp = tasteFeignClient.status403(id);
            log.error("调用方法[status403]成功，返回值[{}]", resp);
        } catch (Exception e) {
            log.error("调用方法[status403]失败，错误信息[{}]", e.getMessage(), e);
        }
        return "SUCCESS";
    }

    /**
     * 服务端返回status的值是404
     * 异常信息如下:
     * <p>
     * feign.FeignException$NotFound: [404] during [GET] to [http://localhost:11201/crm/cif/order/status404?id=38919] [TasteFeignClient#status404(String)]: [来自于第三方服务端[38919]]
     * </p>
     */
    @GetMapping("/tasteStatus404")
    String status404() {
        try {
            String resp = tasteFeignClient.status404(id);
            log.error("调用方法[status404]成功，返回值[{}]", resp);
        } catch (Exception e) {
            log.error("调用方法[status404]失败，错误信息[{}]", e.getMessage(), e);
        }
        return "SUCCESS";
    }

    /**
     * 服务端返回status的值是405
     * 异常信息如下:
     * <p>
     * feign.FeignException$MethodNotAllowed: [405] during [GET] to [http://localhost:11201/crm/cif/order/status405?id=38919] [TasteFeignClient#status405(String)]: [来自于第三方服务端[38919]]
     * </p>
     */
    @GetMapping("/tasteStatus405")
    String status405() {
        try {
            String resp = tasteFeignClient.status405(id);
            log.error("调用方法[status405]成功，返回值[{}]", resp);
        } catch (Exception e) {
            log.error("调用方法[status405]失败，错误信息[{}]", e.getMessage(), e);
        }
        return "SUCCESS";
    }

    /**
     * 服务端返回status的值是406
     * 异常信息如下:
     * <p>
     * feign.FeignException$NotAcceptable: [406] during [GET] to [http://localhost:11201/crm/cif/order/status406?id=38919] [TasteFeignClient#status406(String)]: [来自于第三方服务端[38919]]
     * </p>
     */
    @GetMapping("/tasteStatus406")
    String status406() {
        try {
            String resp = tasteFeignClient.status406(id);
            log.error("调用方法[status406]成功，返回值[{}]", resp);
        } catch (Exception e) {
            log.error("调用方法[status406]失败，错误信息[{}]", e.getMessage(), e);
        }
        return "SUCCESS";
    }

    /**
     * 服务端返回status的值是409
     * 异常信息如下:
     * <p>
     * feign.FeignException$Conflict: [409] during [GET] to [http://localhost:11201/crm/cif/order/status409?id=38919] [TasteFeignClient#status409(String)]: [来自于第三方服务端[38919]]
     * </p>
     */
    @GetMapping("/tasteStatus409")
    String status409() {
        try {
            String resp = tasteFeignClient.status409(id);
            log.error("调用方法[status409]成功，返回值[{}]", resp);
        } catch (Exception e) {
            log.error("调用方法[status409]失败，错误信息[{}]", e.getMessage(), e);
        }
        return "SUCCESS";
    }

    /**
     * 服务端返回status的值是410
     * 异常信息如下:
     * <p>
     * feign.FeignException$Gone: [410] during [GET] to [http://localhost:11201/crm/cif/order/status410?id=38919] [TasteFeignClient#status410(String)]: [来自于第三方服务端[38919]]
     * </p>
     */
    @GetMapping("/tasteStatus410")
    String status410() {
        try {
            String resp = tasteFeignClient.status410(id);
            log.error("调用方法[status410]成功，返回值[{}]", resp);
        } catch (Exception e) {
            log.error("调用方法[status410]失败，错误信息[{}]", e.getMessage(), e);
        }
        return "SUCCESS";
    }

    /**
     * 服务端返回status的值是415
     * 异常信息如下:
     * <p>
     * feign.FeignException$UnsupportedMediaType: [415] during [GET] to [http://localhost:11201/crm/cif/order/status415?id=38919] [TasteFeignClient#status415(String)]: [来自于第三方服务端[38919]]
     * </p>
     */
    @GetMapping("/tasteStatus415")
    String status415() {
        try {
            String resp = tasteFeignClient.status415(id);
            log.error("调用方法[status415]成功，返回值[{}]", resp);
        } catch (Exception e) {
            log.error("调用方法[status415]失败，错误信息[{}]", e.getMessage(), e);
        }
        return "SUCCESS";
    }

    /**
     * 服务端返回status的值是429
     * 异常信息如下:
     * <p>
     * feign.FeignException$TooManyRequests: [429] during [GET] to [http://localhost:11201/crm/cif/order/status429?id=38919] [TasteFeignClient#status429(String)]: [来自于第三方服务端[38919]]
     * </p>
     */
    @GetMapping("/tasteStatus429")
    String status429() {
        try {
            String resp = tasteFeignClient.status429(id);
            log.error("调用方法[status429]成功，返回值[{}]", resp);
        } catch (Exception e) {
            log.error("调用方法[status429]失败，错误信息[{}]", e.getMessage(), e);
        }
        return "SUCCESS";
    }

    /**
     * 服务端返回status的值是422
     * 异常信息如下:
     * <p>
     * feign.FeignException$UnprocessableEntity: [422] during [GET] to [http://localhost:11201/crm/cif/order/status422?id=38919] [TasteFeignClient#status422(String)]: [来自于第三方服务端[38919]]
     * </p>
     */
    @GetMapping("/tasteStatus422")
    String status422() {
        try {
            String resp = tasteFeignClient.status422(id);
            log.error("调用方法[status422]成功，返回值[{}]", resp);
        } catch (Exception e) {
            log.error("调用方法[status422]失败，错误信息[{}]", e.getMessage(), e);
        }
        return "SUCCESS";
    }

    /**
     * 服务端返回status的值是4XX
     * 异常信息如下:
     * <p>
     * feign.FeignException$FeignClientException: [486] during [GET] to [http://localhost:11201/crm/cif/order/status4XX?id=38919] [TasteFeignClient#status4XX(String)]: [来自于第三方服务端[38919]]
     * </p>
     */
    @GetMapping("/tasteStatus4XX")
    String status4XX() {
        try {
            String resp = tasteFeignClient.status4XX(id);
            log.error("调用方法[status4XX]成功，返回值[{}]", resp);
        } catch (Exception e) {
            log.error("调用方法[status4XX]失败，错误信息[{}]", e.getMessage(), e);
        }
        return "SUCCESS";
    }

    /**
     * 服务端返回status的值是500
     * 异常信息如下:
     * <p>
     * feign.FeignException$InternalServerError: [500] during [GET] to [http://localhost:11201/crm/cif/order/status500?id=38919] [TasteFeignClient#status500(String)]: [来自于第三方服务端[38919]]
     * </p>
     */
    @GetMapping("/tasteStatus500")
    String status500() {
        try {
            String resp = tasteFeignClient.status500(id);
            log.error("调用方法[status500]成功，返回值[{}]", resp);
        } catch (Exception e) {
            log.error("调用方法[status500]失败，错误信息[{}]", e.getMessage(), e);
        }
        return "SUCCESS";
    }

    /**
     * 服务端返回status的值是501
     * 异常信息如下:
     * <p>
     * feign.FeignException$NotImplemented: [501] during [GET] to [http://localhost:11201/crm/cif/order/status501?id=38919] [TasteFeignClient#status501(String)]: [来自于第三方服务端[38919]]
     * </p>
     */
    @GetMapping("/tasteStatus501")
    String status501() {
        try {
            String resp = tasteFeignClient.status501(id);
            log.error("调用方法[status501]成功，返回值[{}]", resp);
        } catch (Exception e) {
            log.error("调用方法[status501]失败，错误信息[{}]", e.getMessage(), e);
        }
        return "SUCCESS";
    }

    /**
     * 服务端返回status的值是502
     * 异常信息如下:
     * <p>
     * feign.FeignException$BadGateway: [502] during [GET] to [http://localhost:11201/crm/cif/order/status502?id=38919] [TasteFeignClient#status502(String)]: [来自于第三方服务端[38919]]
     * </p>
     */
    @GetMapping("/tasteStatus502")
    String status502() {
        try {
            String resp = tasteFeignClient.status502(id);
            log.error("调用方法[status502]成功，返回值[{}]", resp);
        } catch (Exception e) {
            log.error("调用方法[status502]失败，错误信息[{}]", e.getMessage(), e);
        }
        return "SUCCESS";
    }

    /**
     * 服务端返回status的值是503
     * 异常信息如下:
     * <p>
     * feign.FeignException$ServiceUnavailable: [503] during [GET] to [http://localhost:11201/crm/cif/order/status503?id=38919] [TasteFeignClient#status503(String)]: [来自于第三方服务端[38919]]
     * </p>
     */
    @GetMapping("/tasteStatus503")
    String status503() {
        try {
            String resp = tasteFeignClient.status503(id);
            log.error("调用方法[status503]成功，返回值[{}]", resp);
        } catch (Exception e) {
            log.error("调用方法[status503]失败，错误信息[{}]", e.getMessage(), e);
        }
        return "SUCCESS";
    }

    /**
     * 服务端返回status的值是504
     * 异常信息如下:
     * <p>
     * feign.FeignException$GatewayTimeout: [504] during [GET] to [http://localhost:11201/crm/cif/order/status504?id=38919] [TasteFeignClient#status504(String)]: [来自于第三方服务端[38919]]
     * </p>
     */
    @GetMapping("/tasteStatus504")
    String status504() {
        try {
            String resp = tasteFeignClient.status504(id);
            log.error("调用方法[status504]成功，返回值[{}]", resp);
        } catch (Exception e) {
            log.error("调用方法[status504]失败，错误信息[{}]", e.getMessage(), e);
        }
        return "SUCCESS";
    }

    /**
     * 服务端返回status的值是5XX
     * 异常信息如下:
     * <p>
     * feign.FeignException$FeignServerException: [586] during [GET] to [http://localhost:11201/crm/cif/order/status5XX?id=38919] [TasteFeignClient#status5XX(String)]: [来自于第三方服务端[38919]]
     * </p>
     */
    @GetMapping("/tasteStatus5XX")
    String status5XX() {
        try {
            String resp = tasteFeignClient.status5XX(id);
            log.error("调用方法[status5XX]成功，返回值[{}]", resp);
        } catch (Exception e) {
            log.error("调用方法[status5XX]失败，错误信息[{}]", e.getMessage(), e);
        }
        return "SUCCESS";
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
            ResultDto<CstBseInfDto> r = tasteFeignClient.test09(req);
            log.info("调用方法[test09]成功，返回值[{}]", r);
            return String.format("调用方法[test09]成功，返回值[%s]", r);
        } catch (Exception e) {
            log.error("调用方法[test09]失败，错误信息[{}]", e.getMessage(), e);
            return String.format("调用方法[test09]失败，错误信息[%s]", e.getMessage());
        }
    }
}
