package com.destinylight.tools.mock.interceptor;

import com.destinylight.tools.mock.config.MockTarget;
import com.destinylight.tools.mock.config.MockTargets;
import com.destinylight.tools.mock.utils.MockConstants;
import net.bytebuddy.asm.Advice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.DYNAMIC;

/**
 * <p>
 * 对"YUSP(udp-cloud)"中对"FeignClient"调用封装的方法的拦截。
 * </p>
 *
 * <pre>
 * "YUSP"的"udp-cloud"模块中，为了服务治理，将FeignClient调用做了封装。
 * 封装之后，YUSP根据接口类的信息，在<code>UcSentinelInvocationHandler</code>中自己封装HTTP请求等，
 * 而不是调用Spring为FeignClient生成的代理类。
 * 这将导致通过{@link MockInterceptor#interceptForDynamic}注册的拦截失效。
 * 因此，我们需要先拦截<code>UcSentinelInvocationHandler</code>，如果判定本次调用的方法是拦截目标中的，
 * 则返回Mock数据；如果不是拦截目标，则继续执行<code>UcSentinelInvocationHandler</code>。
 * </pre>
 *
 * @author 郑靖华 (11821967@qq.com)
 * @date 2025/3/19
 */
public class YuspSentinelAdvice {
    /**
     * 必须使用<code>public</code>修饰符。
     * 否则，当被代理的方法被执行时，会抛出异常，不能访问这个<code>YuspSentinelAdvice</code>中的<code>log</code>变量。
     */
    public static final Logger log = LoggerFactory.getLogger(YuspSentinelAdvice.class);

    /**
     * 当进入被拦截的方法之前时，执行的增强代码。
     *
     * @param className  被拦截的类名
     * @param methodName 被拦截的方法名
     * @param returnType 被拦截的方法的返回值类型
     * @param paramTypes 被拦截的方法的参数类型名(多个方法用逗号分隔)
     * @param args       被拦截的方法的参数
     * @return 总是返回<code>false</code>。
     * 与<code>@Advice.OnMethodEnter</code>中的<code>skipOn = Advice.OnDefaultValue.class</code>配套。
     * 当<code>onEnter()</code>方法返回的值等于<code>skipOn</code>指定的默认值时，表示跳过被拦截的原始方法的执行。
     */
    @Advice.OnMethodEnter(skipOn = Advice.OnDefaultValue.class)
    public static boolean onEnter(@Advice.Origin("#t") String className,
                                  @Advice.Origin("#m") String methodName,
                                  @Advice.Origin("#r") String returnType,
                                  @Advice.Origin("#s") String paramTypes,
                                  @Advice.AllArguments(typing = DYNAMIC) Object[] args) {
        // 类名固定是: cn.com.yusys.udp.cloud.sentinel.feign.UcSentinelInvocationHandler
        // 方法固定是: public Object invoke(final object proxy, final Method method, final Object[] args)
        if (MockConstants.DEBUG) {
            log.info("{} 执行原方法之前的增强函数 类名[{}], 方法名[{}], 返回值类型名[{}], 方法参数名[{}]",
                    MockConstants.COMPONENT_NAME, className, methodName, returnType, paramTypes);
        }
        try {
            Object proxy = args[0];
            Method method = (Method) args[1];
            Object[] methodArgs = (Object[]) args[2];
            if (MockConstants.DEBUG) {
                log.info("{} YUSP拦截的类名[{}], 方法名[{}], 返回值类型名[{}], 方法参数名[{}]",
                        MockConstants.COMPONENT_NAME, proxy.getClass().getName(), method.getName(), methodArgs);
            }
        } catch (Exception e) {
        }

        // 在拦截目标中查找当前方法

        // 判断是否需要跳过原方法执行
        // 因为Spring对FeignClient等动态代理的实现机制，Java Agent拦截的方法可能会在我们的目标之外。
        // 对于这些方法，应该继续原方法的执行。
        return true;
    }

    /**
     * 当被拦截的方法退出时，执行的增强代码
     *
     * @param className  被拦截的类名
     * @param methodName 被拦截的方法名
     * @param returnType 被拦截的方法的返回值类型
     * @param paramTypes 被拦截的方法的参数类型名(多个方法用逗号分隔)
     * @param args       被拦截的方法的参数
     * @param returned   被拦截的方法的返回值对象。
     *                   由于被拦截的方法，不同方法有不同的返回值类型，
     *                   所以，必须使用<code>typing = Assigner.Typing.DYNAMIC</code>。
     */
    @Advice.OnMethodExit()
    public static void onExit(@Advice.Origin("#t") String className,
                              @Advice.Origin("#m") String methodName,
                              @Advice.Origin("#r") String returnType,
                              @Advice.Origin("#s") String paramTypes,
                              @Advice.AllArguments(readOnly = false, typing = DYNAMIC) Object[] args,
                              @Advice.Return(readOnly = false, typing = DYNAMIC) Object returned) {
        if (MockConstants.DEBUG) {
            log.info("{} 原方法执行结束之后的增强函数 类名[{}], 方法名[{}], 返回值类型名[{}], 方法参数名[{}]",
                    MockConstants.COMPONENT_NAME, className, methodName, returnType, paramTypes);
        }

        // 需要根据Mock配置文件，修改返回值。
        // 在拦截目标中查找当前方法
        MockTarget target = MockTargets.search(className, methodName, paramTypes);
        if (target != null && target.isEnable() && !target.isDataTypeVoid()) {
            log.info("{} 确实是待拦截的目标[{}] 类名[{}], 方法名[{}], 方法参数名[{}], 重新赋值",
                    MockConstants.COMPONENT_NAME, target.getTargetStr(), className, methodName, paramTypes);
            // returned = target.getData();
            // returned = MockDataBuilder.data(target);
        }
    }
}
