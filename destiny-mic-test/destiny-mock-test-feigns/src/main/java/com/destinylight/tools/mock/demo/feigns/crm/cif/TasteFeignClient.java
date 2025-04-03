package com.destinylight.tools.mock.demo.feigns.crm.cif;

import com.destinylight.tools.mock.demo.feigns.Const;
import com.destinylight.tools.mock.demo.pojo.commons.module.adapter.web.rest.ResultDto;
import com.destinylight.tools.mock.demo.pojo.commons.module.adapter.web.trans.CstBseInfDtlQryDto;
import com.destinylight.tools.mock.demo.pojo.commons.module.adapter.web.trans.CstBseInfDto;
import com.destinylight.tools.mock.demo.pojo.crm.vo.DataOfDifferentFieldType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * <p>
 * 测试FeignClient可能遇到的部分情况
 * </p>
 * <pre>
 * 需要尝试的情况包括:
 * 1. HTTP METHOD不正确。
 * 2. 404。
 * 3. 对方返回500。
 * 4. 客户端设置了某个HEADER，验证该HEADER是否可以传递到第三方。
 * 400: BadRequest
 * 401: Unauthorized
 * 403: Forbidden
 * 404: NotFound
 * 405: MethodNotAllowed
 * 406: NotAcceptable
 * 409: Conflict
 * 410: Gone
 * 415: UnsupportedMediaType
 * 429: TooManyRequests
 * 422: UnprocessableEntity
 * 其他4XX: FeignClientException
 * 500: InternalServerError
 * 501: NotImplemented
 * 502: BadGateway
 * 503: ServiceUnavailable
 * 504: GatewayTimeout
 * 其他5XX: FeignServerException
 * </pre>
 *
 * @author 郑靖华 (11821967@qq.com)
 * @date 2025/3/20
 */
@FeignClient(name = "app-server-crm-simulator2", url = Const.SIMULATOR_URL, path = "/crm/cif/order")
public interface TasteFeignClient {
    // 返回404。服务方没有提供这个服务接口。
    @GetMapping("/nonExist404")
    String nonExist404(@RequestParam("id") String id);

    // HTTP METHOD不正确。服务方提供的是GET，但我们尝试用POST去访问。
    @PostMapping("/badHttpMethod")
    String badHttpMethod(@RequestParam("id") String id);

    // 服务方提供了这个服务，但抛出了异常。
    @GetMapping("/internalError")
    String internalError(@RequestParam("id") String id);

    // 客户端设置了某个HEADER，验证该HEADER是否可以传递到第三方。
    @GetMapping("/withHeader")
    String withHeader(@RequestParam("id") String id);

    // 请求体没有给注解
    @PostMapping("/postWithoutAnnotation")
    String postWithoutAnnotation(String id);

    // 请求体有给RequestBody注解
    @PostMapping("/postWithAnnotation")
    String postWithAnnotation(@RequestBody String id);

    /**
     * 请求体有给多个RequestBody注解
     * <pre>
     * 在FeignClient里，不允许一个方法的2个及以上参数的使用 {@link RequestBody} 注解。否则，对于以下代码：
     *     <code>@PostMapping("/postWithMultiAnnotation")</code>
     *     <code>String postWithMultiAnnotation(@RequestBody String id, @RequestBody String name);</code>
     *
     * 会在启动阶段抛出以下异常:
     *     org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'tasteFeignController': Unsatisfied dependency expressed through field 'tasteFeignClient'; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'com.destinylight.tools.mock.demo.feigns.crm.cif.TasteFeignClient': Unexpected exception during bean creation; nested exception is java.lang.IllegalStateException: Method has too many Body parameters: public abstract java.lang.String com.destinylight.tools.mock.demo.feigns.crm.cif.TasteFeignClient.postWithMultiAnnotation(java.lang.String,java.lang.String)
     * </pre>
     */
    String placeHolder00001 = "";

    /**
     * 请求体有多个参数没有RequestBody注解。参数上没有注解，表示默认为RequestBody注解。
     * <pre>
     * 在FeignClient里，不允许一个方法的2个及以上参数的使用 {@link RequestBody} 注解。否则，对于以下代码：
     *     <code>@PostMapping("/postWithoutMultiAnnotation")</code>
     *     <code>String postWithoutMultiAnnotation(String id, String name);</code>
     *
     * 会在启动阶段抛出以下异常:
     *     org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'tasteFeignController': Unsatisfied dependency expressed through field 'tasteFeignClient'; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'com.destinylight.tools.mock.demo.feigns.crm.cif.TasteFeignClient': Unexpected exception during bean creation; nested exception is java.lang.IllegalStateException: Method has too many Body parameters: public abstract java.lang.String com.destinylight.tools.mock.demo.feigns.crm.cif.TasteFeignClient.postWithoutMultiAnnotation(java.lang.String,java.lang.String)
     * </pre>
     */
    String placeHolder00002 = "";

    /**
     * 请求参数有给1个RequestBody注解，同时也有至少一个没有RequestBody注解。参数上没有注解，表示默认为RequestBody注解。
     * <pre>
     * 在FeignClient里，不允许一个方法的2个及以上参数的使用 {@link RequestBody} 注解。否则，对于以下代码：
     *     <code>@PostMapping("/postWithAndWithoutAnnotation")</code>
     *     <code>String postWithAndWithoutAnnotation(@RequestBody String id, String name);</code>
     *
     * 会在启动阶段抛出以下异常:
     *     org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'tasteFeignController': Unsatisfied dependency expressed through field 'tasteFeignClient'; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'com.destinylight.tools.mock.demo.feigns.crm.cif.TasteFeignClient': Unexpected exception during bean creation; nested exception is java.lang.IllegalStateException: Method has too many Body parameters: public abstract java.lang.String com.destinylight.tools.mock.demo.feigns.crm.cif.TasteFeignClient.postWithAndWithoutAnnotation(java.lang.String,java.lang.String)
     * </pre>
     */
    String placeHolder00003 = "";

    // 客户端和服务端都有RequestHeader注解
    @GetMapping("/haveHeadBoth")
    String haveHeadBoth(@RequestHeader(name = "CustHead", required = false) String head,
                        @RequestParam("id") String id);

    // 仅客户端有RequestHeader注解
    @GetMapping("/hasHeadClient")
    String hasHeadClient(@RequestHeader(name = "CustHead", required = false) String head,
                         @RequestParam("id") String id);

    // 仅服务端有RequestHeader注解
    @GetMapping("/hasHeadServer")
    String hasHeadServer(@RequestParam("id") String id);

    // 服务端返回的报文体中的某个字段，与客户端使用的实体类的字段类型不同
    @GetMapping(value = "/differentDataType",
            consumes = "application/json;charset=UTF-8",
            produces = "application/json;charset=UTF-8")
    DataOfDifferentFieldType differentDataType();

    // 服务端返回status的值是400
    @GetMapping("/status400")
    String status400(@RequestParam("id") String id);

    // 服务端返回status的值是401
    @GetMapping("/status401")
    String status401(@RequestParam("id") String id);

    // 服务端返回status的值是403
    @GetMapping("/status403")
    String status403(@RequestParam("id") String id);

    // 服务端返回status的值是404
    @GetMapping("/status404")
    String status404(@RequestParam("id") String id);

    // 服务端返回status的值是405
    @GetMapping("/status405")
    String status405(@RequestParam("id") String id);

    // 服务端返回status的值是406
    @GetMapping("/status406")
    String status406(@RequestParam("id") String id);

    // 服务端返回status的值是409
    @GetMapping("/status409")
    String status409(@RequestParam("id") String id);

    // 服务端返回status的值是410
    @GetMapping("/status410")
    String status410(@RequestParam("id") String id);

    // 服务端返回status的值是415
    @GetMapping("/status415")
    String status415(@RequestParam("id") String id);

    // 服务端返回status的值是429
    @GetMapping("/status429")
    String status429(@RequestParam("id") String id);

    // 服务端返回status的值是422
    @GetMapping("/status422")
    String status422(@RequestParam("id") String id);

    // 服务端返回status的值是4XX
    @GetMapping("/status4XX")
    String status4XX(@RequestParam("id") String id);

    // 服务端返回status的值是500
    @GetMapping("/status500")
    String status500(@RequestParam("id") String id);

    // 服务端返回status的值是501
    @GetMapping("/status501")
    String status501(@RequestParam("id") String id);

    // 服务端返回status的值是502
    @GetMapping("/status502")
    String status502(@RequestParam("id") String id);

    // 服务端返回status的值是503
    @GetMapping("/status503")
    String status503(@RequestParam("id") String id);

    // 服务端返回status的值是504
    @GetMapping("/status504")
    String status504(@RequestParam("id") String id);

    // 服务端返回status的值是5XX
    @GetMapping("/status5XX")
    String status5XX(@RequestParam("id") String id);

    @PostMapping(value = "/test09")
    ResultDto<CstBseInfDto> test09(CstBseInfDtlQryDto req);
}
