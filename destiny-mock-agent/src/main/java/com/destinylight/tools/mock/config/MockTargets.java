package com.destinylight.tools.mock.config;

import com.destinylight.tools.mock.main.MockPremain;
import com.destinylight.tools.mock.utils.MockConstants;
import com.destinylight.tools.mock.utils.MockUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * Mock拦截的目标。包括以下类型：
 * 1、拦截FeignClient请求。
 * 2、拦截普通的非静态方法。
 * </p>
 *
 * @author 郑靖华 (11821967@qq.com)
 * @date 2025/3/11
 */
public class MockTargets {
    private static final Logger log = LoggerFactory.getLogger(MockTargets.class);

    /**
     * Mock拦截器组件的全局配置
     */
    private MockConfig config;
    /**
     * 普通方法和FeignClient方法的拦截目标(List)
     */
    private List<MockTarget> targets = new ArrayList<>();
    /**
     * 普通方法和FeignClient方法的拦截目标(便于检索的Map)
     */
    private Map<MockTarget.Key, MockTarget> targetMap = new HashMap<>();
    /**
     * 已经明确不在拦截范围的类+方法
     */
    private Map<MockTarget.Key, MockTarget.Key> exclusions = new HashMap<>();

    public MockConfig getConfig() {
        return config;
    }

    public void setConfig(MockConfig config) {
        this.config = config;
    }

    public List<MockTarget> getTargets() {
        return targets;
    }

    public void setTargets(List<MockTarget> targets) {
        this.targets = targets;
        targetMap = this.targets.stream().collect(Collectors.toMap(MockTarget::getKey, p -> p));
    }

    /**
     * 将一个拦截目标加入到列表中
     *
     * @param target 拦截目标
     */
    public void putTarget(MockTarget target) {
        targets.add(target);
        targetMap.put(target.getKey(), target);
    }

    /**
     * 根据传入的关键信息，取得拦截目标
     *
     * @param className    拦截目标的类的名称
     * @param methodName   拦截目标的方法名称
     * @param methodParams 拦截目标的方法的参数类型
     * @return 拦截目标。<code>null</code>表示未找到。
     */
    public MockTarget getTarget(String className, String methodName, String methodParams) {
        MockTarget.Key key = new MockTarget.Key()
                .setTargetTypeName(className)
                .setTargetMethodName(methodName)
                .setTargetMethodParams(methodParams);

        return targetMap.get(key);
    }

    /**
     * 将指定类+方法加入到拦截目标的排除项里
     *
     * @param className    被排除的类的名称
     * @param methodName   被排除的方法名称
     * @param methodParams 被排除的方法的参数类型
     */
    public void exclude(String className, String methodName, String methodParams) {
        MockTarget.Key key = new MockTarget.Key()
                .setTargetTypeName(className)
                .setTargetMethodName(methodName)
                .setTargetMethodParams(methodParams);
        this.exclusions.put(key, key);
    }

    /**
     * @param className    待判定的类的名称
     * @param methodName   待判定的方法名称
     * @param methodParams 待判定的方法的参数类型。必须精确匹配。因为该值是之前的运行过程中从Advice里得到的准确的值。
     * @return 指定类+方法是否已经在Mock拦截器的排除项里了
     */
    public boolean isExcluded(String className, String methodName, String methodParams) {
        MockTarget.Key key = new MockTarget.Key()
                .setTargetTypeName(className)
                .setTargetMethodName(methodName)
                .setTargetMethodParams(methodParams);
        return this.exclusions.containsKey(key);
    }

    /**
     * @return 当前已经注册的拦截目标的方法名(去重)
     */
    public List<String> targetMethodNames() {
        if (this.targetMap.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        Map<String, String> names = new HashMap<>();
        for (MockTarget.Key key : this.targetMap.keySet()) {
            names.put(key.getTargetMethodName(), key.getTargetMethodName());
        }
        return names.keySet().stream().collect(Collectors.toList());
    }

    /**
     * 从固定目录下导入Mock配置参数：
     * <pre>
     * 1. mock/mock-server.json: 配置元素设计平台的Mock地址
     * 2. *.mock.json: Mock数据配置文件
     * </pre>
     *
     * @return Mock配置参数。<code>null</code>表示没有配置文件，禁用Mock。
     */
    public static MockTargets load() {
        if (!MockUtils.configPathExist()) {
            // Mock数据配置目录不存在，禁用Mock拦截器
            log.error("Mock数据配置目录[{}]不存在，禁用Mock拦截器", MockUtils.configPath().toFile().getAbsolutePath());
            return null;
        }

        MockTargets mockTargets = new MockTargets();
        mockTargets.setConfig(new MockServerFileParser().parse());
        if (!mockTargets.getConfig().isDisable()) {
            List<MockTarget> targets = new MockDataFileParser().parseAll();
            if (CollectionUtils.isEmpty(targets)) {
                // 如果没有拦截目标，禁用Mock拦截器
                log.error("Mock数据配置目录[{}]下没有可用的Mock数据配置文件(拦截目标)，禁用Mock拦截器",
                        MockUtils.configPath().toFile().getAbsolutePath());
                return null;
            }

            mockTargets.setTargets(targets);
        } else {
            log.info("{} 在全局参数配置文件[{}]中的参数[disable]被设置为[true]，不再继续解析Mock数据配置文件",
                    MockConstants.COMPONENT_NAME, MockConstants.MOCK_SERVER_FILENAME);
        }
        return mockTargets;
    }

    /**
     * 查找满足条件的拦截目标
     *
     * @param className  拦截目标的类名
     * @param methodName 拦截目标的方法名
     * @param paramTypes 拦截目标的方法参数
     * @return 满足条件的拦截目标。<code>null</code>表示当前方法不是我们期望拦截的目标。
     */
    public static MockTarget search(String className, String methodName, String paramTypes) {
        // 如果已经明确被排除了，则返回null
        if (MockPremain.targets.isExcluded(className, methodName, paramTypes)) {
            if (MockConstants.DEBUG) {
                log.info("{} 这是一个已经明确不被拦截的方法[{}.{}{}]",
                        MockConstants.COMPONENT_NAME, className, methodName, paramTypes);
            }
            return null;
        }

        // 如果被拦截的类及方法，在目标中直接查找到了，表示应该是一个普通方法。
        MockTarget target = MockPremain.targets.getTarget(className, methodName, paramTypes);
        // 再尝试使用忽略参数的KEY查询一次
        if (target == null) {
            target = MockPremain.targets.getTarget(className, methodName, null);
        }
        if (target != null) {
            if (MockConstants.DEBUG) {
                log.info("{} 这是一个期望被拦截的目标[{}]", MockConstants.COMPONENT_NAME, target.getTargetStr());
            }
            onFirstCallMethod(target);
            return target;
        }

        // 如果没有找到，则看其是否是某个接口类的实现类，并且，查看该接口类是否有FeignClient注解。
        // 先判断类名称是否可能是FeignClient的代理类
        if (!MockConstants.FEIGN_PROXY_NAME_PATTERN.matcher(className).matches()) {
            // 类名不是一个Spring为FeignClient生成的代理类的名称，将其加入到排除项里
            if (MockConstants.DEBUG) {
                log.info("{} 既不是普通方法，也不是Spring生成的代理类的方法，可以明确这是不被拦截的方法[{}.{}{}]",
                        MockConstants.COMPONENT_NAME, className, methodName, paramTypes);
            }
            MockPremain.targets.exclude(className, methodName, paramTypes);
            return null;
        }

        // 该类是否实现了某个FeignClient接口类
        Class<?> clz;
        try {
            clz = Class.forName(className);
        } catch (Exception e) {
            log.error("{} 类[{}]不存在[{}]", MockConstants.COMPONENT_NAME, className, e.getMessage());
            MockPremain.targets.exclude(className, methodName, paramTypes);
            return null;
        }
        // 如果FeignClient接口类本身又是继承自另一个FeignClient接口类，还需要进一步处理。暂时先没做处理。
        Class<?> feign = MockUtils.getFeignInterface(clz);
        if (feign == null) {
            // 该类不是任何FeignClient的代理类，确认不是拦截目标，将其加入到排除项里
            if (MockConstants.DEBUG) {
                log.info("{} 虽然是Spring生成的代理类的方法，但其代理的接口类不是FeignClient接口类。"
                                + "可以明确这是不被拦截的方法[{}.{}{}]",
                        MockConstants.COMPONENT_NAME, className, methodName, paramTypes);
            }
            MockPremain.targets.exclude(className, methodName, paramTypes);
            return null;
        }
        target = MockPremain.targets.getTarget(feign.getName(), methodName, paramTypes);
        // 再尝试使用忽略参数的KEY查询一次
        if (target == null) {
            target = MockPremain.targets.getTarget(feign.getName(), methodName, null);
        }
        // 该代理类所实现的FeignClient不在拦截目标中
        if (target == null) {
            if (MockConstants.DEBUG) {
                log.info("{} 虽然是Spring为FeignClient接口类生成的代理类的方法，但该FeignClient接口类不在拦截目标中。"
                                + "可以明确这是不被拦截的方法[{}.{}{}]",
                        MockConstants.COMPONENT_NAME, className, methodName, paramTypes);
            }
            MockPremain.targets.exclude(className, methodName, paramTypes);
            return null;
        }
        onFirstCallMethod(target);
        target.setFeignClient(true);

        // 解析FeignClient相关的信息

        if (MockConstants.DEBUG) {
            log.info("{} 可以明确这是Spring为要被拦截的FeignClient接口类而生成的代理类的方法[{}.{}{}]",
                    MockConstants.COMPONENT_NAME, className, methodName, paramTypes);
        }
        // 到这里，已经可以确定该代理类+方法是拦截目标，将该代理类+方法加入到目标，便于下次可以直接命中。
        MockTarget targetProxy = new MockTarget();
        targetProxy.setTargetTypeName(className);
        targetProxy.setTargetMethodName(methodName);
        targetProxy.setTargetMethodParams(paramTypes);
        targetProxy.setTargetStr(target.getTargetStr());
        targetProxy.setFilename(target.getFilename());
        targetProxy.setFile(target.getFile());
        targetProxy.setFileUpdatedTime(target.getFileUpdatedTime());
        targetProxy.setRefreshEveryTime(target.isRefreshEveryTime());
        targetProxy.setShouldFromMockServer(target.isShouldFromMockServer());
        targetProxy.setParams(target.getParams());
        targetProxy.setHeader(target.getHeader());

        // 解析FeignClient相关的信息
        MockDataFileParser.parseFeignClient(targetProxy, feign);
        targetProxy.setFeignClient(true);
        try {
            MockDataFileParser.onFirstCallMethod(targetProxy);
            // Spring为FeignClient生成的代理类，很可能丢失了返回值类型的泛型信息。
            // 所以，需要从接口类里获得返回值类型等信息。
            targetProxy.setDataGenericType(target.getDataGenericType());
            targetProxy.setDataType(target.getDataType());
            targetProxy.setDataTypeName(target.getDataTypeName());
            targetProxy.setFirstInvoke(false);
            MockPremain.targets.putTarget(targetProxy);
            return targetProxy;
        } catch (Exception e) {
            log.error("{} 类[{}]Mock返回值失败[{}]", MockConstants.COMPONENT_NAME, className, e.getMessage(), e);
            MockPremain.targets.exclude(className, methodName, paramTypes);
            return null;
        }
    }

    /**
     * 如果第一次调用本方法，则解析该方法的信息
     *
     * @param target 拦截目标
     */
    private static void onFirstCallMethod(MockTarget target) {
        if (target.isFirstInvoke()) {
            // 该拦截目标是第一次被调用，需要取得拦截对象的类实例、方法实例等信息
            if (MockConstants.DEBUG) {
                log.info("{} 第一次拦截这个目标[{}]", MockConstants.COMPONENT_NAME, target.getTargetStr());
            }
            try {
                MockDataFileParser.onFirstCallMethod(target);
            } catch (Exception e) {
                log.error("{} 解析拦截对象的返回值出错[{}]", MockConstants.COMPONENT_NAME, e.getMessage(), e);
            }
            target.setFirstInvoke(false);
        } else {
            if (MockConstants.DEBUG) {
                log.info("{} 不是第一次搜索这个目标[{}]", MockConstants.COMPONENT_NAME, target.getTargetStr());
            }
        }
    }
}
