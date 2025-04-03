package com.destinylight.tools.mock.data;

import com.destinylight.tools.mock.config.MockConfig;
import com.destinylight.tools.mock.config.MockTarget;
import com.destinylight.tools.mock.main.MockPremain;
import com.destinylight.tools.mock.utils.MockConstants;
import com.destinylight.tools.mock.utils.MockUtils;
import com.alibaba.fastjson.JSON;
import feign.FeignException;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.destinylight.tools.mock.utils.MockConstants.MOCK_ALG_FROM_MOCK_SERVER;
import static com.destinylight.tools.mock.utils.MockConstants.MOCK_SERVER_REQ_HEAD_SVC_NAME;
import static com.destinylight.tools.mock.utils.MockConstants.MOCK_SERVER_RESP_HEAD_DATA;
import static com.destinylight.tools.mock.utils.MockConstants.MOCK_SERVER_RESP_HEAD_MOCK;

/**
 * <p>
 * 从Mock服务器(元素设计平台)获得mock数据
 * </p>
 *
 * @author 郑靖华 (11821967@qq.com)
 * @date 2025/3/16
 */
public class MockDataFromMockServer implements IMockData {
    private static final Logger log = LoggerFactory.getLogger(MockDataFromMockServer.class);

    private RequestTemplate requestTemplate = null;

    @Override
    public boolean execute(MockTarget target, Object[] args) {
        if (!target.isFeignClient()) {
            log.info("{} 拦截目标[{}]不是FeignClient，不需要尝试使用Mock服务器(元素设计平台)生成mock数据",
                    MockConstants.COMPONENT_NAME, target.getTargetStr());
            return false;
        }
        if (!target.isShouldFromMockServer()) {
            log.info("{} 拦截目标[{}]被配置为不需要尝试使用Mock服务器(元素设计平台)生成mock数据",
                    MockConstants.COMPONENT_NAME, target.getTargetStr());
            return false;
        }
        if (MockConstants.DEBUG) {
            log.info("{} 拦截目标[{}]尝试使用Mock服务器(元素设计平台)生成mock数据",
                    MockConstants.COMPONENT_NAME, target.getTargetStr());
        }
        try {
            String url = joinUrl(target);
            if (MockConstants.DEBUG) {
                log.info("{} 拦截目标[{}]访问Mock服务器(元素设计平台)的原始url是[{}]",
                        MockConstants.COMPONENT_NAME, target.getTargetStr(), url);
            }
            url = expandUrl(url, target.getParams(), args);
            if (MockConstants.DEBUG) {
                log.info("{} 拦截目标[{}]访问Mock服务器(元素设计平台)的扩展后的url是[{}]",
                        MockConstants.COMPONENT_NAME, target.getTargetStr(), url);
            }
            String httpMethodName = target.getHttpMethod().name();
            String respJson = call(target, url, httpMethodName, target.getMicroServiceName(), args);
            Object data = JSON.parseObject(respJson, target.getDataGenericType());
            target.setData(data);
            return true;
        } catch (Exception e) {
            log.error("{} 拦截目标[{}]使用Mock服务器(元素设计平台)生成mock数据时出错[{}]",
                    MockConstants.COMPONENT_NAME, target.getTargetStr(), e.getMessage(), e);
            // 如果是FeignException，则还需要抛出异常。
            if (e instanceof FeignException) {
                FeignException e1 = (FeignException) e;
                throw e1;
            }
            return false;
        }
    }

    @Override
    public String name() {
        return MOCK_ALG_FROM_MOCK_SERVER;
    }

    /**
     * 根据参数展开URL。
     * <pre>
     * 1. 替换路径参数 {@link PathVariable}
     * 2. 追加请求参数 {@link RequestParam}
     * </pre>
     *
     * @param url   可能带有占位符的原始URL
     * @param metas 方法参数的元数据
     * @param args  本次调用本方法时传入的参数的值
     * @return 展开后的URL
     */
    private String expandUrl(String url, List<MockTarget.Param> metas, Object[] args) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);

        // 追加请求参数
        MultiValueMap<String, String> queries = new LinkedMultiValueMap<>();
        int loc = 0;
        for (MockTarget.Param meta : metas) {
            if (MockTarget.ParamType.QUERY.equals(meta.getType())) {
                if (MockUtils.isSimpleType(meta.getClz())) {
                    String value = "" + args[loc];
                    queries.add(meta.getName(), value);
                }
            }
            loc++;
        }
        // 增加请求参数
        if (!queries.isEmpty()) {
            builder.queryParams(queries);
        }

        // 替换占位符
        Map<String, Object> params = new HashMap<>();
        loc = 0;
        for (MockTarget.Param meta : metas) {
            if (MockTarget.ParamType.PATH.equals(meta.getType())) {
                params.put(meta.getName(), args[loc]);
            }
            loc++;
        }
        if (!params.isEmpty()) {
            return builder.buildAndExpand(params).toUriString();
        }

        return builder.toUriString();
    }

    /**
     * 根据拦截目标(FeignClient)中定义的HTTP相关的信息，返回完整的URL
     *
     * @param target 拦截目标
     * @return 拦截目标转向访问Mock服务器后的URL
     */
    private String joinUrl(MockTarget target) {
        StringBuilder sb = new StringBuilder();
        String server = MockPremain.targets.getConfig().getServer();
        if (MockUtils.isBlank(server)) {
            throw new IllegalArgumentException("没有配置Mock服务器(元素设计平台)的URL地址");
        }
        sb.append(server.endsWith("/") ? server.subSequence(0, server.length() - 1) : server);
        String context = target.getContextPath();
        if (!MockUtils.isBlank(context)) {
            if (!context.startsWith("/")) {
                sb.append('/');
            }
            sb.append(context.endsWith("/") ? context.subSequence(0, server.length() - 1) : context);
        }
        String path = target.getHttpUrl();
        if (!MockUtils.isBlank(path)) {
            if (!path.startsWith("/")) {
                sb.append('/');
            }
            sb.append(path);
        }
        return sb.toString();
    }

    /**
     * 调用Mock服务器(元素设计平台)的mock服务接口，取得mock数据。
     * <pre>
     * -----  请求报文  -----
     * 1. 请求报文中，HTTP HEADER里，使用 {@link MockConstants#MOCK_SERVER_REQ_HEAD_SVC_NAME} 保存原始<code>FeignClient(name="..")</code>中定义的微服务/应用名称。
     * 2. 以请求URL<code>http://metaweb.test.zgb.inyusys.com/mock/1/api/w/demom/corp/uscmrtgastinf/query</code>为例，由以下部分组成:
     * 2.1 <code>http://metaweb.test.zgb.inyusys.com/mock/1</code>是保存在Mock配置文件"mock-server.json"中的固定的URL地址。该地址又是由2个部分组成的：
     * 2.1.1 <code>http://metaweb.test.zgb.inyusys.com/mock</code>: 这是元素设计平台对外提供的统一的mock数据服务接口。
     * 2.1.2 <code>/1</code>: 这是当前应用在元素设计平台的租户编号。
     * 2.2 <code>/api/w/demom/corp/uscmrtgastinf/query</code>: 这是原始FeignClient中类及方法注解中相关内容拼接而成的，第三方应用对外提供的服务接口。
     * -----  响应报文  -----
     * 1. 如果响应报文的HTTP HEADER里，既没有{@link MockConstants#MOCK_SERVER_RESP_HEAD_MOCK}，又没有{@link MockConstants#MOCK_SERVER_RESP_HEAD_DATA}，
     * 并且<code>status</code>不是2XX，则表示调用元素设计平台本身出错了，应该抛出异常。
     * 2. 如果响应报文的HTTP HEADER里，虽然既没有{@link  MockConstants#MOCK_SERVER_RESP_HEAD_MOCK}，又没有{@link MockConstants#MOCK_SERVER_RESP_HEAD_DATA}，
     * 但是<code>status</code>是2XX，则表示调用元素设计平台成功了，视同有数据成功返回。
     * 3. 如果响应报文的HTTP HEADER里，有{@link MockConstants#MOCK_SERVER_RESP_HEAD_MOCK}，或者有{@link MockConstants#MOCK_SERVER_RESP_HEAD_DATA}，
     * 并且<code>status</code>不是200，则表示调用元素设计平台本身成功，并且由元素设计平台模拟了非200的<code>status</code>值。
     * 对于Mock拦截器而言，目前还没有处理这种情况，所以只需要记录信息，抛出异常，并由下一个mock数据生成算法继续执行。
     * 4. 如果响应报文的HTTP HEADER里，有{@link MockConstants#MOCK_SERVER_RESP_HEAD_MOCK}(其值为true或者其他任何值，我们不做区分)，
     * 表示成功生成了mock数据，我们需要将返回的mock数据反序列化到对象里。
     * 5. 如果响应报文的HTTP HEADER里，有{@link MockConstants#MOCK_SERVER_RESP_HEAD_DATA}(其值为<code>failed</code>)，
     * 表示调用元素设计平台成功，但元素设计平台生成mock数据失败，应该抛出异常。
     * </pre>
     *
     * <pre>
     * 对于元素设计平台模拟的status：
     * 2XX: 交易成功，返回响应报文。
     * 3XX: 暂时还不知道怎么处理。
     * 4XX: 客户端错误，抛出异常就可以了。
     * 5XX: 服务端错误，抛出异常就可以了。
     * </pre>
     *
     * @param target         拦截目标
     * @param url            访问Mock服务器(元素设计平台)的URL
     * @param httpMethodName 访问Mock服务器(元素设计平台)的HTTP METHOD。
     *                       不过，我们并不需要传递真正的报文体给Mock服务器(元素设计平台)
     * @param svcName        原始的第三方应用的微服务/应用名称
     * @param args           本次调用本方法时传入的参数的值
     * @return 元素设计平台返回的mock数据
     */
    private String call(MockTarget target, String url, String httpMethodName, String svcName, Object[] args) throws Exception {
        // 创建一个HttpClient实例
        OkHttpClient httpClient = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json;charset=UTF-8");
        // 构造请求报文体的内容
        String reqBodyStr = null;
        RequestBody reqBody = null;
        if (needReqBody(httpMethodName)) {
            reqBodyStr = reqBodyStr(target.getParams(), args);
            reqBody = RequestBody.create(reqBodyStr, mediaType);
            if (MockConstants.DEBUG) {
                log.info("{} 拦截目标[{}], 报文体内容[{}]",
                        MockConstants.COMPONENT_NAME, target.getTargetStr(), reqBodyStr);
            }
        }

        // 创建一个HTTP请求 指定URI
        Request httpRequest = new Request.Builder()
                .headers(headers(target, svcName, args))
                .url(HttpUrl.get(URI.create(url)))
                .method(httpMethodName, reqBody)
                .build();

        // 发送 HTTP 请求并获取响应
        Response response = httpClient.newCall(httpRequest).execute();
        int status = response.code();
        String metaMock = response.header(MOCK_SERVER_RESP_HEAD_MOCK);
        String mockData = response.header(MOCK_SERVER_RESP_HEAD_DATA);
        String respBodyStr = response.isSuccessful() ? response.body().string() : null;
        if (MockConstants.DEBUG) {
            log.info("{} 拦截目标[{}]访问Mock服务器(元素设计平台), status[{}], metaMock[{}], mockData[{}], data[{}]",
                    MockConstants.COMPONENT_NAME, target.getTargetStr(), status, metaMock, mockData, respBodyStr);
        }

        // 没有特定的HTTP RESPONSE HEADER，表示请求完全没有到达元素设计平台，或者没有被元素设计平台识别为一个Mock数据生成请求。
        if (MockUtils.isBlank(metaMock) && MockUtils.isBlank(mockData)) {
            if (status >= 400 && status <= 599) {
                throw new IllegalStateException(String.format("调用[%s %s]失败, 返回的[status]是[%d], 响应报文体[%s]",
                        httpMethodName, url, status, respBodyStr));
            }
            return respBodyStr;
        }

        if (!MockUtils.isBlank(metaMock) || !MockUtils.isBlank(mockData)) {
            if (status != 200) {
                // 这是元素设计平台模拟出来的表示异常的status。需要构造FeignException，并抛出该异常。
                log.info("调用[{} {}]成功, 元素设计平台返回的[status]是[{}], [{}]是[{}], [{}]是[{}], 响应报文体[{}]",
                        httpMethodName, url, status,
                        MOCK_SERVER_RESP_HEAD_MOCK, metaMock, MOCK_SERVER_RESP_HEAD_DATA, mockData, respBodyStr);

                statusHandlerAsFeign(url, httpMethodName, Collections.EMPTY_MAP, reqBodyStr, respBodyStr, status);
            }
        }

        if ("failed".equals(mockData)) {
            throw new IllegalStateException(
                    String.format("调用[%s %s]成功, 元素设计平台返回的[status]是[%d], [%s]是[%s], 服务端错误信息[%s]",
                            httpMethodName, url, status, MOCK_SERVER_RESP_HEAD_DATA, mockData, respBodyStr));
        }

        // 元素设计平台返回正常的mock数据
        return respBodyStr;
    }

    /**
     * 拼装的HTTP REQUEST HEADER。
     * <pre>
     * 转发给元素设计平台的HTTP REQUEST HEADER，由以下部分组成:
     * 1. 如果 <code>copyFromClient</code> 被设置为 <code>true</code> (Mock数据配置文件的优先级高于全局配置文件)，
     *    则将客户端的HTTP HEAD全部复制过来。
     * 2. 将Mock数据配置文件和全局配置文件中配置的"add"元素中的HEAD，去重后加入。
     * 3. 使用Mock数据配置文件和全局配置文件中配置的"update"元素中HEAD，修改head中的值。
     * 4. 使用Mock数据配置文件和全局配置文件中配置的"exclude"元素中HEAD，删除head中的值。
     * 5. 根据被拦截目标文件中的注解@RequestHeader，加入该HEADER。
     * 6. 加入元素设计平台要求的固定的HEADER，名为 {@link MockConstants#MOCK_SERVER_REQ_HEAD_SVC_NAME} 。
     * X. 排除几个RFC中几个特殊的HEAD，这几个HEAD，应该由Http的底层实现自动填充。
     * </pre>
     * 2025/3/23: 我们先实现上述的1、5、6、X这几个部分。
     *
     * @param target  拦截目标
     * @param svcName 原始的第三方应用的微服务/应用名称
     * @param args    本次调用本方法时传入的参数的值
     * @return 传递给元素设计平台的HTTP REQUEST HEADER
     */
    private Headers headers(MockTarget target, String svcName, Object[] args) {
        MockConfig config = MockPremain.targets.getConfig();
        Headers.Builder builder = new Headers.Builder();

        // 1. 获取当前请求的客户端传入的HEADER
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = sra.getRequest();
        Enumeration<String> names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            if (MockConstants.DEBUG) {
                log.info("{} 拦截目标[{}]访问Mock服务器(元素设计平台), 从客户端获得的HTTP HEADER名称[{}]",
                        MockConstants.COMPONENT_NAME, target.getTargetStr(), name);
            }
            if (!isIgnoredRfcHead(name)) {
                Enumeration<String> values = request.getHeaders(name);
                while (values.hasMoreElements()) {
                    String value = values.nextElement();
                    builder.add(name, value);
                }
            }
        }

        // 5. 根据被拦截目标文件中的注解@RequestHeader，加入该HEADER。
        int loc = 0;
        for (MockTarget.Param meta : target.getParams()) {
            if (MockTarget.ParamType.HEAD.equals(meta.getType())) {
                String value = "" + args[loc];
                builder.add(meta.getName(), value);
            }
            loc++;
        }

        // 6. 加入元素设计平台要求的固定的HEADER
        builder.add(MOCK_SERVER_REQ_HEAD_SVC_NAME, svcName);

        return builder.build();
    }

    /**
     * 是否是RFC中规定的标准HTTP REQUEST HEADER，这些HEADER不应该根据客户端的值转发给Mock服务器，而是应该由主应用的HTTP util自动赋值。
     *
     * @param name 等判断的HTTP REQUEST HEADER的名称
     * @return 是否是不需要转发给Mock服务器的HEADER
     * @see <a href="https://www.rfc-editor.org/rfc/rfc2616.html#section-14">Hypertext Transfer Protocol -- HTTP/1.1: 14 Header Field Definitions</a>
     */
    private boolean isIgnoredRfcHead(String name) {
        return "connection".equalsIgnoreCase(name) || "content-type".equalsIgnoreCase(name)
                || "host".equalsIgnoreCase(name) || "user-agent".equalsIgnoreCase(name);
    }

    /**
     * 找到注解为@RequestBody的参数，将其参数值序列化为JSON字符串
     *
     * @param metas 方法参数的元数据
     * @param args  本次调用本方法时传入的参数的值
     * @return 请求参数报文体
     */
    private String reqBodyStr(List<MockTarget.Param> metas, Object[] args) {
        String bodyStr = "{}";
        // 找到第一个种类为BODY的参数
        int loc = 0;
        for (MockTarget.Param meta : metas) {
            if (MockTarget.ParamType.BODY.equals(meta.getType())) {
                if (MockUtils.isSimpleType(args[loc].getClass())) {
                    bodyStr = "" + args[loc];
                } else {
                    bodyStr = JSON.toJSONString(args[loc]);
                }
                break;
            }
            loc++;
        }
        return bodyStr;
    }

    /**
     * @param httpMethodName 访问Mock服务器(元素设计平台)的HTTP METHOD。
     * @return 是否需要填充请求报文体
     */
    private boolean needReqBody(String httpMethodName) {
        return "POST".equalsIgnoreCase(httpMethodName);
    }

    /**
     * 象FeignClient一样处理返回码
     *
     * @param url            URL地址
     * @param httpMethodName HTTP METHOD
     * @param headers        请求报文的HTTP HEADER
     * @param reqBodyStr     请求报文体的内容，可为空
     * @param respBodyStr    响应报文体的内容，可为空
     * @param status         元素设计平台返回的HTTP的status
     */
    private void statusHandlerAsFeign(String url, String httpMethodName,
                                      Map<String, Collection<String>> headers,
                                      String reqBodyStr,
                                      String respBodyStr,
                                      int status) {
        if (!FeignSimulator.isClientError(status) && !FeignSimulator.isClientError(status)) {
            // 既不4XX，也不是5XX，我现在还不知道应该如何处理
            return;
        }

        // 构造请求报文体
        feign.Request.Body reqBody = MockUtils.isBlank(reqBodyStr) ?
                feign.Request.Body.empty() :
                feign.Request.Body.create(reqBodyStr);
        // 构造请求报文
        feign.Request request = feign.Request.create(
                MockUtils.ofFeignHttpMethod(httpMethodName),
                url, headers, reqBody, requestTemplate);

        // 构造响应报文
        feign.Response.Builder respBuilder = feign.Response.builder();
        respBuilder.status(status);
        respBuilder.request(request);
        respBuilder.body(MockUtils.isBlank(respBodyStr) ? null : respBodyStr.getBytes());
        respBuilder.requestTemplate(requestTemplate);
        feign.Response response = respBuilder.build();

        // 现在不知道"methodKey"是做什么用的
        String methodKey = httpMethodName;
        throw FeignException.errorStatus(methodKey, response);
    }

    /**
     * 从"io.github.openfeign:feign-core:11.10"中复制来的源代码，用于模拟Feign的相关操作
     */
    private static abstract class FeignSimulator {
        /**
         * @param status HTTP响应报文头中的 <code>status</code>
         * @return 是否是客户端原因造成的错误
         */
        private static boolean isClientError(int status) {
            return status >= 400 && status < 500;
        }

        /**
         * @param status  HTTP响应报文头中的 <code>status</code>
         * @param message 根据一定规则拼装成的信息，参考 {@link FeignException.FeignExceptionMessageBuilder#build}
         * @param request feign构建的请求报文
         * @param body    响应报文体
         * @param headers HTTP HEADER
         * @return 与 <code>status</code> 对应的异常类
         * @see {@link FeignException#clientErrorStatus}
         */
        abstract FeignException.FeignClientException
        clientErrorStatus(int status,
                          String message,
                          feign.Request request,
                          byte[] body,
                          Map<String, Collection<String>> headers);

        /**
         * @param status HTTP响应报文头中的 <code>status</code>
         * @return 是否是服务器端原因造成的错误
         */
        private static boolean isServerError(int status) {
            return status >= 500 && status <= 599;
        }

        /**
         * @param status  HTTP响应报文头中的 <code>status</code>
         * @param message 根据一定规则拼装成的信息，参考 {@link FeignException.FeignExceptionMessageBuilder#build}
         * @param request feign构建的请求报文
         * @param body    响应报文体
         * @param headers HTTP HEADER
         * @return 与 <code>status</code> 对应的异常类
         * @see {@link FeignException#serverErrorStatus}
         */
        abstract FeignException.FeignServerException
        serverErrorStatus(int status,
                          String message,
                          feign.Request request,
                          byte[] body,
                          Map<String, Collection<String>> headers);
    }
}