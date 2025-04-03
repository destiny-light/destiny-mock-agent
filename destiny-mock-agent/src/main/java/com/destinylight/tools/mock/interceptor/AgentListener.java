package com.destinylight.tools.mock.interceptor;

import com.destinylight.tools.mock.utils.MockConstants;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.utility.JavaModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * 侦听代理的操作
 * </p>
 *
 * @author 郑靖华 (11821967@qq.com)
 * @date 2025/3/11
 */
public class AgentListener implements AgentBuilder.Listener {
    private static final Logger log = LoggerFactory.getLogger(AgentListener.class);

    /**
     * 是否打印冗余的信息
     */
    public boolean verbose = false;

    @Override
    public void onDiscovery(String s, ClassLoader classLoader, JavaModule javaModule, boolean b) {
        if (MockConstants.DEBUG && verbose) {
            // 不能用log.*()，否则，可能会造成"ClassCircularityError"异常
            System.out.println(String.format("%s onDiscovery()：classLoader[%s], javaModule[%s], loaded[%b]",
                    MockConstants.COMPONENT_NAME, classLoader.toString(), javaModule.toString(), b));
        }
    }

    @Override
    public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader,
                                 JavaModule javaModule, boolean b, DynamicType dynamicType) {
        if (MockConstants.DEBUG) {
            log.info("{} onTransformation()：typeDescription[{}], classLoader[{}], javaModule[{}], loaded[{}], dynamicType[{}]",
                    MockConstants.COMPONENT_NAME, typeDescription, classLoader, javaModule, b, dynamicType);
        }
    }

    @Override
    public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule javaModule, boolean b) {
        if (MockConstants.DEBUG && verbose) {
            // 不能用log.*()，否则，可能会造成"ClassCircularityError"异常
            System.out.println(String.format("%s onIgnored()：typeDescription[%s], classLoader[%s], javaModule[%s], loaded[%b]",
                    MockConstants.COMPONENT_NAME,
                    typeDescription.toString(), classLoader.toString(), javaModule.toString(), b));
        }
    }

    @Override
    public void onError(String s, ClassLoader classLoader, JavaModule javaModule, boolean b, Throwable throwable) {
        if (MockConstants.DEBUG && verbose) {
            // 不能用log.*()，否则，可能会造成"ClassCircularityError"异常
            System.out.println(String.format("%s onError()：instrumented type[%s], classLoader[%s], javaModule[%s], loaded[%b], msg[%s]",
                    MockConstants.COMPONENT_NAME,
                    s, classLoader.toString(), javaModule.toString(), b, throwable.getMessage()));
        }
    }

    @Override
    public void onComplete(String s, ClassLoader classLoader, JavaModule javaModule, boolean b) {
        if (MockConstants.DEBUG && verbose) {
            // 不能用log.*()，否则，可能会造成"ClassCircularityError"异常
            System.out.println(String.format("%s onComplete()：instrumented type[%s], classLoader[%s], javaModule[%s], loaded[%b]",
                    MockConstants.COMPONENT_NAME,
                    s, classLoader.toString(), javaModule.toString(), b));
        }
    }
}
