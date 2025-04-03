package com.destinylight.tools.mock.main;

import com.destinylight.tools.mock.config.MockTarget;
import com.destinylight.tools.mock.config.MockTargets;
import com.destinylight.tools.mock.data.MockDataBuilder;
import com.destinylight.tools.mock.interceptor.AgentListener;
import com.destinylight.tools.mock.interceptor.MockAdvice;
import com.destinylight.tools.mock.interceptor.MockInterceptor;
import com.destinylight.tools.mock.utils.MockCommandLineArgs;
import com.destinylight.tools.mock.utils.MockConstants;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.Instrumentation;

/**
 * <p>
 * Java Agent在main之前执行的入口方法
 * </p>
 *
 * @author 郑靖华 (11821967@qq.com)
 * @date 2025/3/11
 */
public class MockPremain {
    private static final Logger log = LoggerFactory.getLogger(MockPremain.class);

    public static MockTargets targets = null;

    public static void premain(String agentArgs, Instrumentation instrumentation) {
        log.info("{} 加载Mock拦截器", MockConstants.COMPONENT_NAME);
        log.info("{} Java Agent的参数: {}", MockConstants.COMPONENT_NAME, agentArgs);
        MockCommandLineArgs.parse(agentArgs);

        // 解析配置参数
        targets = MockTargets.load();

        if (targets != null && !targets.getConfig().isDisable()) {
            // 注册Mock数据生成算法
            MockDataBuilder.init();

            // 启动Byte Buddy Agent
            ByteBuddyAgent.install();

            //监听
            AgentBuilder.Listener listener = new AgentListener();

            // 对于非FeignClient的普通方法，需要逐一注册代理
            for (MockTarget target : targets.getTargets()) {
                MockInterceptor.intercept(instrumentation, listener, MockAdvice.class,
                        target.getTargetTypeName(), target.getTargetMethodName(), null);
            }

            // 对于FeignClient方法，只需要统一添加一个代理，并在运行过程中再进一步筛选
            // 我们需要对拦截目标的所有方法逐一进行注册。
            // 如果不限制这些方法名，则所有代理类的所有方法都有可能会被拦截，并在拦截后再做判定，效率会比较低。
            // (不论其是否是FeignClient，因为在注入完成之前，我们无法通过Class.forName()来确定是否FeignClient)
            for (String methodName : targets.targetMethodNames()) {
                MockInterceptor.interceptForDynamic(instrumentation, listener, MockAdvice.class, methodName, null);
            }

            log.info("{} Mock拦截器被启用", MockConstants.COMPONENT_NAME);
        } else if (targets == null) {
            log.info("{} 没有需要拦截的目标，禁用Mock拦截器", MockConstants.COMPONENT_NAME);
        } else if (targets.getConfig().isDisable()) {
            log.info("{} 在全局参数配置文件[{}]中的参数[disable]被设置为[true]，禁用Mock拦截器",
                    MockConstants.COMPONENT_NAME, MockConstants.MOCK_SERVER_FILENAME);
        } else {
            log.info("{} 因为未知的原因，禁用Mock拦截器", MockConstants.COMPONENT_NAME);
        }
    }
}
