package com.destinylight.tools.mock.interceptor;

import com.destinylight.tools.mock.config.MockTarget;
import com.destinylight.tools.mock.config.MockTargets;
import com.destinylight.tools.mock.data.MockDataBuilder;
import com.destinylight.tools.mock.utils.MockConstants;
import net.bytebuddy.asm.Advice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.DYNAMIC;

/**
 * <p>
 * 字节码增强逻辑
 * </p>
 *
 * @author 郑靖华 (11821967@qq.com)
 * @date 2025/3/11
 */
public class MockAdvice {
    /**
     * 必须使用<code>public</code>修饰符。
     * 否则，当被代理的方法被执行时，会抛出异常，不能访问这个<code>MockAdvice</code>中的<code>log</code>变量。
     */
    public static final Logger log = LoggerFactory.getLogger(MockAdvice.class);

    /**
     * 当进入被拦截的方法之前时，执行的增强代码。
     *
     * @param className  被拦截的类名
     * @param methodName 被拦截的方法名
     * @param returnType 被拦截的方法的返回值类型
     * @param paramTypes 被拦截的方法的参数类型名(多个方法用逗号分隔)
     * @return 总是返回<code>false</code>。
     * 与<code>@Advice.OnMethodEnter</code>中的<code>skipOn = Advice.OnDefaultValue.class</code>配套。
     * 当<code>onEnter()</code>方法返回的值等于<code>skipOn</code>指定的默认值时，表示跳过被拦截的原始方法的执行。
     */
    @Advice.OnMethodEnter(skipOn = Advice.OnDefaultValue.class)
    public static boolean onEnter(@Advice.Origin("#t") String className,
                                  @Advice.Origin("#m") String methodName,
                                  @Advice.Origin("#r") String returnType,
                                  @Advice.Origin("#s") String paramTypes) {
        if (MockConstants.DEBUG) {
            log.info("{} 执行原方法之前的增强函数 类名[{}], 方法名[{}], 返回值类型名[{}], 方法参数名[{}]",
                    MockConstants.COMPONENT_NAME, className, methodName, returnType, paramTypes);
        }

        // 在拦截目标中查找当前方法
        MockTarget target = MockTargets.search(className, methodName, paramTypes);
        if (target != null && !target.isEnable()) {
            log.info("{} 配置文件中指定对该目标[{}]的拦截被禁用 类名[{}], 方法名[{}], 方法参数名[{}]",
                    MockConstants.COMPONENT_NAME, target.getTargetStr(), className, methodName, paramTypes);
            // 需要继续执行原方法
            return true;
        }

        // 判断是否需要跳过原方法执行
        // 因为Spring对FeignClient等动态代理的实现机制，Java Agent拦截的方法可能会在我们的目标之外。
        // 对于这些方法，应该继续原方法的执行。
        return target == null;
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
            returned = MockDataBuilder.data(target, args);
        }
    }
}
