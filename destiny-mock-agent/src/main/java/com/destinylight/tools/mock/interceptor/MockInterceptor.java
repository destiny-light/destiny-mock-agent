package com.destinylight.tools.mock.interceptor;

import com.destinylight.tools.mock.main.MockPremain;
import com.destinylight.tools.mock.utils.MockConstants;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.matcher.ElementMatchers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.Instrumentation;

/**
 * <p>
 * 拦截指定的类及方法，为其返回Mock数据
 * </p>
 *
 * @author 郑靖华 (11821967@qq.com)
 * @date 2025/3/11
 */
public class MockInterceptor {
    private static final Logger log = LoggerFactory.getLogger(MockInterceptor.class);

    /**
     * 注册对方法的拦截
     *
     * @param instrumentation Java Agent的参数
     * @param listener        对动态代理操作的侦听
     * @param advice          动态代理要做的增强
     * @param className       将被拦截的类的名称
     * @param methodName      将被拦截的方法的名称
     * @param paramTypeNames  如果方法名称在该类中有重复，则需要通过方法参数唯一确定.
     *                        2025/3/13: 方法参数的名称，应该与<code>bytebuddy</code>中的<code>@Advice.OnMethodEnter</code>
     *                        中的<code>@Advice.Origin("#s")</code>传入的字符串完全相同，但我目前不知道其完整的语法。
     */
    public static void intercept(Instrumentation instrumentation, AgentBuilder.Listener listener,
                                 Class<?> advice,
                                 String className, String methodName, String paramTypeNames) {
        log.info("{} 注册拦截目标, 类名[{}], 方法名[{}]", MockConstants.COMPONENT_NAME, className, methodName);
        AgentBuilder agentBuilder = new AgentBuilder.Default();

        AgentBuilder.Transformer transformer = (builder, typeDescription, classLoader, javaModule, protectionDomain) ->
                builder.visit(Advice.to(advice).on(ElementMatchers.named(methodName)));

        // 忽略Byte Buddy自身类
        agentBuilder.ignore(ElementMatchers.nameStartsWith("net.bytebuddy."));
        agentBuilder = agentBuilder
                .type(ElementMatchers.named(className)).transform(transformer);

        if (MockConstants.DEBUG) {
            agentBuilder.with(listener);
        }
        // 将bytebuddy的插桩逻辑安装到instrument
        agentBuilder.installOn(instrumentation);
    }

    /**
     * 注册对FeignClient方法的拦截.
     * <pre>
     * Spring启动时，为FeignClient生成的代理类的名称，可能形如以下的某一种（可能不同的java厂商、版本，生成的代理类的名称会不同？）:
     * 1. jdk.proxy2.$Proxy96(我在本地测试中得到的结果)
     * 2. com.example.UserService$Proxy123(DeepSeek给出的结果：JDK 动态代理示例)
     * 3. com.example.UserService$$EnhancerBySpringCGLIB$$a1b2c3d4(DeepSeek给出的结果：Spring CGLIB 代理示例)
     * 所以，我们在启动前的Java Agent里，不能带上具体的类名，只能匹配更广泛的范围。
     * 在运行过程中，再根据配置的拦截目标，根据其实现的接口类，再做进一步的筛选。
     * </pre>
     *
     * @param instrumentation Java Agent的参数
     * @param listener        对动态代理操作的侦听
     * @param advice          动态代理要做的增强
     * @param methodName      将被拦截的方法的名称
     * @param paramTypeNames  如果方法名称在该类中有重复，则需要通过方法参数唯一确定.
     *                        2025/3/13: 方法参数的名称，应该与<code>bytebuddy</code>中的<code>@Advice.OnMethodEnter</code>
     *                        中的<code>@Advice.Origin("#s")</code>传入的字符串完全相同，但我目前不知道其完整的语法。
     */
    public static void interceptForDynamic(Instrumentation instrumentation, AgentBuilder.Listener listener, Class<?> advice,
                                           String methodName, String paramTypeNames) {
        AgentBuilder agentBuilder = new AgentBuilder.Default();

        AgentBuilder.Transformer transformer = (builder, typeDescription, classLoader, javaModule, protectionDomain) ->
                builder.visit(Advice.to(advice).on(ElementMatchers.named(methodName)));

        // 忽略Byte Buddy自身类
        agentBuilder.ignore(ElementMatchers.nameStartsWith("net.bytebuddy."));
        // 不能使用: className + "\\$Proxy\\d+|.*\\$\\$EnhancerBySpringCGLIB\\$.*";
        // 详见上面的解释
        AgentBuilder.Identified.Narrowable narrowable;
        if (MockPremain.targets.getConfig().isInterceptAllProxy()) {
            narrowable = agentBuilder.type(ElementMatchers.any());
        } else {
            narrowable = agentBuilder.type(ElementMatchers.nameMatches(MockConstants.FEIGN_PROXY_NAME_PATTERN_STR));
            for (String pattern : MockPremain.targets.getConfig().getProxyPattern()) {
                // 允许用户根据项目的实际情况，定义动态代理类名称的正则表达式
                narrowable = narrowable.or(ElementMatchers.nameMatches(pattern));
            }
        }

        agentBuilder = narrowable.transform(transformer)
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION);

        if (MockConstants.DEBUG) {
            agentBuilder.with(listener);
        }
        // 将bytebuddy的插桩逻辑安装到instrument
        agentBuilder.installOn(instrumentation);
    }

    /**
     * 注册对"YUSP(udp-cloud)"中对"FeignClient"调用封装的方法的拦截。
     *
     * @param instrumentation Java Agent的参数
     * @param listener        对动态代理操作的侦听
     * @see YuspSentinelAdvice
     */
    public static void interceptYuspSentinel(Instrumentation instrumentation, AgentBuilder.Listener listener) {
        String className = "cn.com.yusys.udp.cloud.sentinel.feign.UcSentinelInvocationHandler";
        String methodName = "invoke";
        log.info("{} 注册拦截目标[YUSP对FeignClient的封装], 类名[{}], 方法名[{}]",
                MockConstants.COMPONENT_NAME, className, methodName);

        String paramTypeNames = null;
        Class<?> advice = YuspSentinelAdvice.class;
        intercept(instrumentation, listener, advice, className, methodName, paramTypeNames);
    }
}
