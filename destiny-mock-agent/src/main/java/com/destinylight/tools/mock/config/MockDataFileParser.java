package com.destinylight.tools.mock.config;

import com.destinylight.tools.mock.utils.MockConstants;
import com.destinylight.tools.mock.utils.MockUtils;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * Mock数据配置文件解析
 * </p>
 *
 * @author 郑靖华 (11821967@qq.com)
 * @date 2025/3/11
 */
public class MockDataFileParser {
    private static final Logger log = LoggerFactory.getLogger(MockDataFileParser.class);

    /**
     * 解析固定目录[${user.dir}/mock]下的所有后缀名为".mock.json"的文件。
     *
     * @return Mock方法拦截目标列表
     */
    public List<MockTarget> parseAll() {
        Path path = MockUtils.configPath();
        File root = path.toFile();
        if (!root.exists()) {
            log.info("{} Mock数据配置文件所在的目录[{}]不存在", MockConstants.COMPONENT_NAME, root.getAbsolutePath());
            return Collections.EMPTY_LIST;
        } else {
            File[] files = root.listFiles();
            if (files == null || files.length < 1) {
                log.info("{} Mock数据配置文件所在的目录[{}]为空", MockConstants.COMPONENT_NAME, root.getAbsolutePath());
                return Collections.EMPTY_LIST;
            }
        }

        /*
         *递归遍历目录下所有后缀名为".mock.json"的文件
         */
        List<MockTarget> targets = new ArrayList<>();
        try {
            Files.walk(path)
                    .filter(p -> p.toFile().getAbsolutePath().endsWith(MockConstants.MOCK_DATA_FILENAME_SUFFIX))
                    .filter(p -> p.toFile().isFile())
                    .forEach(p -> {
                        MockTarget target = parse(p.toFile());
                        if (target != null) {
                            targets.add(target);
                        }
                    });
        } catch (Exception e) {
            log.error("{} 遍历Mock数据配置文件失败[{}]", MockConstants.COMPONENT_NAME, e.getMessage(), e);
            return Collections.EMPTY_LIST;
        }

        return targets;
    }

    /**
     * 解析指定名称的Mock数据配置文件。
     * <pre>
     * 1.如果文件中包含<code>target</code>元素，则不需要关心其文件名。
     * 2.如果文件中未包含<code>target</code>元素，则文件名即为拦截目标。
     * 3.如果文件中未包含<code>target</code>元素，并且文件名也不是拦截目标的名称，则返回<code>null</code>。
     * </pre>
     *
     * @return Mock方法拦截目标
     */
    public MockTarget parse(File file) {
        log.info("{} 解析Mock数据配置文件[{}]", MockConstants.COMPONENT_NAME, file.getAbsolutePath());
        MockTarget target = new MockTarget();
        target.setFile(file);
        target.setFilename(file.getAbsolutePath());
        target.setFileUpdatedTime(file.lastModified());

        try {
            JSONObject json = MockUtils.parseJsonFile(file);
            String targetStr = (json == null) ? null : json.getString("target");
            if (targetStr == null) {
                // 如果文件中未包含<code>target</code>元素，则文件名即为拦截目标。
                String filename = file.getName();
                targetStr = filename.substring(0, filename.length() - MockConstants.MOCK_DATA_FILENAME_SUFFIX.length());
            }
            target.setTargetStr(targetStr);

            // 类名.方法名
            String classMethodName;
            // 目标名，必须形如：
            // 1. className.methodName
            // 2. className.methodName(...)
            int parenthesisLoc = targetStr.indexOf("(");
            classMethodName = (parenthesisLoc > 0) ? targetStr.substring(0, parenthesisLoc) : targetStr;
            // 在类名.方法名中，最后一个小数点的位置
            int lastPotLoc = classMethodName.lastIndexOf(".");
            if (lastPotLoc <= 0) {
                throw new IllegalArgumentException(String.format("[类.方法]的全限定名[%s]不正确", classMethodName));
            }

            String className = classMethodName.substring(0, lastPotLoc);
            String methodName = classMethodName.substring(lastPotLoc + 1);
            target.setTargetTypeName(className);
            target.setTargetMethodName(methodName);

            // 不能在这里调用：onFirstCallMethod(target);

            // 是否禁用对该目标的拦截(默认为启用)
            target.setEnable(MockUtils.nullAs((json == null) ? null : json.getBoolean("enable"), true));

            // 是否允许从Mock服务器(元素设计平台)获得mock数据。默认值为true。
            target.setShouldFromMockServer(
                    MockUtils.nullAs((json == null) ? null : json.getBoolean("fromMockServer"), true));

            // 是否每一次调用时，都使用最新的数据，而不是上一次已经生成的数据。默认值为true。
            target.setRefreshEveryTime(
                    MockUtils.nullAs((json == null) ? null : json.getBoolean("refreshEveryTime"), true));

            target.setHeader(MockHttpReqHeader.parse(json.get("header")));

            log.info("{} 解析Mock数据配置文件[{}]成功。拦截目标[{}]",
                    MockConstants.COMPONENT_NAME, file.getAbsolutePath(), targetStr);
        } catch (Exception e) {
            log.error("{} 解析Mock数据配置文件[{}]失败[{}]",
                    MockConstants.COMPONENT_NAME, file.getAbsolutePath(), e.getMessage(), e);
            return null;
        }

        return target;
    }

    /**
     * 当第一次调用目标方法时，取得Mock数据。
     * 本方法，不能在Java Agent阶段中执行，而是应该在Spring启动完成并运行过程中执行。
     * 否则，<code>Class.forName()</code>会将该类加载到JVM，而不能够被{@code bytebuddy}织入字节码。
     *
     * @param target 拦截目标
     */
    public static void onFirstCallMethod(MockTarget target) throws Exception {
        String className = target.getTargetTypeName();
        String methodName = target.getTargetMethodName();

        // 验证类名是否存在
        Class<?> clz;
        try {
            clz = Class.forName(className);
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("类[%s]不存在", className));
        }

        // 是否FeignClient接口类，并且不是Fallback接口类
        // 因为FeignClient的Fallback回调类是一个普通的类，所以，我们可以将其认为是一个普通的类。
        // 而且，通过具体的Fallback的类名称，也可以剔除掉该类，不需要在Mock拦截器中单独处理。
        if (clz.isInterface() && clz.getAnnotation(FeignClient.class) != null) {
            target.setFeignClient(true);
            // 解析FeignClient相关的信息
            parseFeignClient(target, clz);
        }

        // 取得方法
        Method method = getMethod(clz, className, methodName);

        target.setTargetType(clz);
        target.setMethod(method);

        // 返回值类型
        Class<?> returnType = method.getReturnType();
        if (void.class == returnType || Void.class == returnType) {
            target.setDataTypeVoid(true);
        } else {
            target.setDataType(returnType);
            Type genericReturnType = method.getGenericReturnType();
            if (MockConstants.DEBUG) {
                Type[] types = method.getGenericParameterTypes();
                log.info("{} 是否是ParameterizedType[{}], Type类型名称[{}], 泛型参数类型[{}]", MockConstants.COMPONENT_NAME,
                        (genericReturnType instanceof ParameterizedType), genericReturnType.getTypeName(),
                        ((types == null || types.length < 1) ? "无" : types));
                if (genericReturnType instanceof ParameterizedType) {
                    ParameterizedType pt = (ParameterizedType) genericReturnType;
                    log.info("{} ParameterizedType: RawType[{}], OwnerType[{}]", MockConstants.COMPONENT_NAME,
                            (pt.getRawType() == null ? "无" : pt.getRawType().getTypeName()),
                            (pt.getOwnerType() == null ? "无" : pt.getOwnerType().getTypeName()));
                }
            }
            target.setDataGenericType(genericReturnType);
            target.setDataTypeName(returnType.getTypeName());
        }
    }

    /**
     * 解析FeignClient相关的信息。包括:
     * <pre>
     * 1. contextPath
     * 2. HTTP METHOD
     * 3. HTTP URI
     * </pre>
     *
     * @param target   拦截目标
     * @param feignClz FeignClient接口类
     */
    public static void parseFeignClient(MockTarget target, Class<?> feignClz) {
        FeignClient annotation = feignClz.getAnnotation(FeignClient.class);
        target.setMicroServiceName(MockUtils.isBlank(annotation.value()) ? annotation.name() : annotation.value());
        target.setContextPath(annotation.path());

        // getAnnotations()返回该方法上所有的注解类(包括继承来的)
        // getDeclaredAnnotations()返回该方法上直接定义的所有的注解类(不包括继承来的)
        Method method = getMethod(feignClz, feignClz.getName(), target.getTargetMethodName());
        target.setHttpMethod(parseMethod(method.getAnnotations()));
        target.setHttpUrl(parseUrl(method.getAnnotations()));
        target.setParams(parseRequestMethodParam(method.getParameters(), method.getName()));
    }

    /**
     * 解析方法的参数
     *
     * @param parameters 反射得到的方法参数的列表
     * @param methodName 方法名称
     * @return Mock拦截器类型的方法参数列表
     */
    private static List<MockTarget.Param> parseRequestMethodParam(Parameter[] parameters, String methodName) {
        if (parameters != null && parameters.length > 0) {
            List<MockTarget.Param> params = new ArrayList<>(parameters.length);
            for (Parameter parameter : parameters) {
                MockTarget.Param param = new MockTarget.Param();
                if (MockConstants.DEBUG) {
                    log.info("{} 方法[{}]参数: isNamePresent[{}], 参数名称[{}], 参数类型[{}]", MockConstants.COMPONENT_NAME,
                            methodName, parameter.isNamePresent(), parameter.getName(),
                            parameter.getType().getName());
                }
                param.setOrigName(parameter.getName());
                param.setClz(parameter.getType());

                Annotation[] annotations = parameter.getAnnotations();
                // 是否已经找到FeignClient相关的注解？
                boolean hasValidAnnotation = false;
                // 对于FeignClient的方法参数，允许同一个参数有多个FeignClient的注解么？
                if (annotations != null && annotations.length > 0) {
                    /**
                     * 我们目前需要关注以下注解 {@link MockTarget.ParamType}
                     */
                    for (Annotation anno : annotations) {
                        if (anno instanceof PathVariable) {
                            PathVariable pva = (PathVariable) anno;
                            if (MockConstants.DEBUG) {
                                // 注解类本身可能被Spring做了代理
                                log.info("{} 方法[{}]的参数[{}]有注解类: [{}]@[{}]", MockConstants.COMPONENT_NAME,
                                        methodName, parameter.getName(),
                                        anno.getClass().getName(), PathVariable.class.getName());
                            }
                            param.setName(MockUtils.anyNonBlank(pva.name(), pva.value(), param.getOrigName()));
                            param.setRequired(pva.required());
                            param.setType(MockTarget.ParamType.PATH);
                            hasValidAnnotation = true;
                            break;
                        } else if (anno instanceof RequestParam) {
                            RequestParam rpa = (RequestParam) anno;
                            if (MockConstants.DEBUG) {
                                // 注解类本身可能被Spring做了代理
                                log.info("{} 方法[{}]的参数[{}]有注解类: [{}]@[{}]", MockConstants.COMPONENT_NAME,
                                        methodName, parameter.getName(),
                                        anno.getClass().getName(), RequestParam.class.getName());
                            }
                            param.setName(MockUtils.anyNonBlank(rpa.name(), rpa.value(), param.getOrigName()));
                            param.setRequired(rpa.required());
                            param.setType(MockTarget.ParamType.QUERY);
                            param.setDefaultValue(rpa.defaultValue());
                            hasValidAnnotation = true;
                            break;
                        } else if (anno instanceof RequestBody) {
                            RequestBody rba = (RequestBody) anno;
                            if (MockConstants.DEBUG) {
                                // 注解类本身可能被Spring做了代理
                                log.info("{} 方法[{}]的参数[{}]有注解类: [{}]@[{}]", MockConstants.COMPONENT_NAME,
                                        methodName, parameter.getName(),
                                        anno.getClass().getName(), RequestBody.class.getName());
                            }
                            param.setName(param.getOrigName());
                            param.setRequired(rba.required());
                            param.setType(MockTarget.ParamType.BODY);
                            hasValidAnnotation = true;
                            break;
                        } else if (anno instanceof RequestHeader) {
                            RequestHeader rha = (RequestHeader) anno;
                            if (MockConstants.DEBUG) {
                                // 注解类本身可能被Spring做了代理
                                log.info("{} 方法[{}]的参数[{}]有注解类: [{}]@[{}]", MockConstants.COMPONENT_NAME,
                                        methodName, parameter.getName(),
                                        anno.getClass().getName(), RequestHeader.class.getName());
                            }
                            param.setName(MockUtils.anyNonBlank(rha.name(), rha.value(), param.getOrigName()));
                            param.setRequired(rha.required());
                            param.setType(MockTarget.ParamType.HEAD);
                            param.setDefaultValue(rha.defaultValue());
                            hasValidAnnotation = true;
                            break;
                        } else if (anno instanceof RequestAttribute) {
                            RequestAttribute raa = (RequestAttribute) anno;
                            if (MockConstants.DEBUG) {
                                // 注解类本身可能被Spring做了代理
                                log.info("{} 方法[{}]的参数[{}]有注解类: [{}]@[{}]", MockConstants.COMPONENT_NAME,
                                        methodName, parameter.getName(),
                                        anno.getClass().getName(), RequestAttribute.class.getName());
                            }
                            param.setName(MockUtils.anyNonBlank(raa.name(), raa.value(), param.getOrigName()));
                            param.setRequired(raa.required());
                            param.setType(MockTarget.ParamType.ATTR);
                            hasValidAnnotation = true;
                            break;
                        } else {
                            if (MockConstants.DEBUG) {
                                log.info("{} 方法[{}]的参数[{}]有我们还未识别的注解类: [{}]", MockConstants.COMPONENT_NAME,
                                        methodName, parameter.getName(), anno.getClass().getName());
                            }
                        }
                    }
                }
                if (!hasValidAnnotation) {
                    // 默认为RequestBody
                    if (MockConstants.DEBUG) {
                        // 注解类本身可能被Spring做了代理
                        log.info("{} 方法[{}]的参数[{}]没有注解类，被视作使用注解类[{}]", MockConstants.COMPONENT_NAME,
                                methodName, parameter.getName(), RequestBody.class.getName());
                    }
                    param.setName(param.getOrigName());
                    param.setRequired(true);
                    param.setType(MockTarget.ParamType.BODY);
                }
                params.add(param);
            }
            return params;
        } else {
            if (MockConstants.DEBUG) {
                log.info("{} 方法[{}]没有参数", MockConstants.COMPONENT_NAME, methodName);
            }
            return Collections.EMPTY_LIST;
        }
    }

    /**
     * 根据注解类确定HTTP METHOD。
     * 如果该注解有多个HTTP METHOD，则返回第1个。
     *
     * @param annotations 方法上的注解类
     * @return HTTP METHOD
     */
    private static RequestMethod parseMethod(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof RequestMapping) {
                RequestMapping m = (RequestMapping) annotation;
                return (m.method() == null || m.method().length < 1) ? RequestMethod.GET : m.method()[0];
            }
            if (annotation instanceof GetMapping) {
                return RequestMethod.GET;
            }
            if (annotation instanceof PostMapping) {
                return RequestMethod.POST;
            }
            if (annotation instanceof PutMapping) {
                return RequestMethod.PUT;
            }
            if (annotation instanceof PatchMapping) {
                return RequestMethod.PATCH;
            }
            if (annotation instanceof DeleteMapping) {
                return RequestMethod.DELETE;
            }
            // RequestMethod枚举类里的HEAD, OPTIONS, TRACE，没有找到对应的Mapping
        }
        return null;
    }

    /**
     * 根据注解类确定HTTP URL。
     *
     * @param annotations 方法上的注解类
     * @return HTTP URL
     */
    private static String parseUrl(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof RequestMapping) {
                RequestMapping m = (RequestMapping) annotation;
                return firstUrl(m.path(), m.value());
            }
            if (annotation instanceof GetMapping) {
                GetMapping m = (GetMapping) annotation;
                return firstUrl(m.path(), m.value());
            }
            if (annotation instanceof PostMapping) {
                PostMapping m = (PostMapping) annotation;
                return firstUrl(m.path(), m.value());
            }
            if (annotation instanceof PutMapping) {
                PutMapping m = (PutMapping) annotation;
                return firstUrl(m.path(), m.value());
            }
            if (annotation instanceof PatchMapping) {
                PatchMapping m = (PatchMapping) annotation;
                return firstUrl(m.path(), m.value());
            }
            if (annotation instanceof DeleteMapping) {
                DeleteMapping m = (DeleteMapping) annotation;
                return firstUrl(m.path(), m.value());
            }
            // RequestMethod枚举类里的HEAD, OPTIONS, TRACE，没有找到对应的Mapping
        }
        return null;
    }

    /**
     * @param paths  方法上的Mapping注解里的HTTP URL(paths)
     * @param values 方法上的Mapping注解里的HTTP URL(默认值或者values)
     * @return 第1个URL
     */
    private static String firstUrl(String[] paths, String values[]) {
        return (values != null && values.length > 0)
                ? values[0]
                : ((paths != null && paths.length > 0) ? paths[0] : null);
    }

    /**
     * <pre>
     * getMethods()只能取得所有public的方法，包括其各级父类及接口类定义的方法。
     * getDeclaredMethods()只能取得该类自己定义的所有方法，但不包括其各级父类及接口类定义的方法。
     * getDeclaredMethods()更符合我们的场景。
     * 暂时还没有考虑多个同名方法有不同参数的情况。
     * </pre>
     *
     * @param clz        目标类
     * @param className  目标类的名称
     * @param methodName 目标方法的名称
     * @return 目标方法
     */
    private static Method getMethod(Class<?> clz, String className, String methodName) {
        for (Method m : clz.getDeclaredMethods()) {
            if (m.getName().equals(methodName)) {
                return m;
            }
        }
        throw new IllegalArgumentException(String.format("在类[%s]中未找到名为[%s]的方法", className, methodName));
    }
}