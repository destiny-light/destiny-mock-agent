package com.destinylight.tools.mock.data;

import com.destinylight.tools.mock.config.MockConfig;
import com.destinylight.tools.mock.config.MockTarget;
import com.destinylight.tools.mock.main.MockPremain;
import com.destinylight.tools.mock.utils.MockConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.destinylight.tools.mock.utils.MockConstants.MOCK_ALG_FROM_CACHE;
import static com.destinylight.tools.mock.utils.MockConstants.MOCK_ALG_FROM_LOCAL_FILE;
import static com.destinylight.tools.mock.utils.MockConstants.MOCK_ALG_FROM_MOCK_SERVER;
import static com.destinylight.tools.mock.utils.MockConstants.MOCK_ALG_MIN_RULE;

/**
 * <p>
 * 按照Mock数据的尝试顺序，依次调用对应的数据生成算法实现类，得到mock数据。
 * </p>
 *
 * @author 郑靖华 (11821967@qq.com)
 * @date 2025/3/16
 */
public class MockDataBuilder {
    private static final Logger log = LoggerFactory.getLogger(MockDataBuilder.class);

    /**
     * 按照顺序排列的Mock数据生成算法
     */
    private static List<IMockData> impls = null;
    /**
     * Mock拦截器组件已经注册的Mock数据生成算法
     */
    private static Map<String, IMockData> implRegistered = new HashMap<>();

    static {
        // 注册所有的Mock数据生成算法
        register(implRegistered, new MockDataFromLastTime());
        register(implRegistered, new MockDataFromLocalFile());
        register(implRegistered, new MockDataFromMockServer());
        register(implRegistered, new MockDataFromMinRule());
    }

    /**
     * 注册已经实现的mock数据生成算法
     *
     * @param implRegistered 注册表
     * @param impl           mock数据生成算法
     */
    private static void register(Map<String, IMockData> implRegistered, IMockData impl) {
        log.info("{} 注册Mock数据生成算法，算法名称[{}]，算法实现类[{}]",
                MockConstants.COMPONENT_NAME, impl.name(), impl.getClass().getName());
        implRegistered.put(impl.name(), impl);
    }

    /**
     * 初始化。
     * 之所以要分成 <code>init()</code> 和 <code>init0()</code> 这2个方法，是为了让 <code>init()</code> 达到同步操作的目标。
     */
    public synchronized static void init() {
        if (impls != null) {
            return;
        }
        impls = init0();
    }


    /**
     * 初始化的真正实现
     *
     * @return 排好序的mock数据生成算法
     */
    private static List<IMockData> init0() {
        // 已经加入到排序队列中的算法
        Map<String, IMockData> implOrdered = new HashMap<>();
        // 排好序的算法列表
        List<IMockData> impls = new ArrayList<>();

        // 从上次的mock结果直接返回，总是排在第一位
        implOrdered.put(MOCK_ALG_FROM_CACHE, implRegistered.get(MOCK_ALG_FROM_CACHE));
        impls.add(implRegistered.get(MOCK_ALG_FROM_CACHE));
        // 极简规则，总是排在最后一位，作为兜底算法
        implOrdered.put(MOCK_ALG_MIN_RULE, implRegistered.get(MOCK_ALG_MIN_RULE));
        impls.add(implRegistered.get(MOCK_ALG_MIN_RULE));

        MockConfig config = MockPremain.targets.getConfig();
        if (!config.getMockSourceOrder().isEmpty()) {
            // 按照指定顺序加载
            order(implOrdered, impls, config.getMockSourceOrder().toArray(new String[0]));
        }
        // 将未指定顺序的算法，按照默认顺序加入到列表中
        // order()会确保不会重复加入
        order(implOrdered, impls, MOCK_ALG_FROM_LOCAL_FILE, MOCK_ALG_FROM_MOCK_SERVER);

        if (MockConstants.DEBUG) {
            log.info("{} 排序后的Mock数据生成算法的个数[{}]", MockConstants.COMPONENT_NAME, impls.size());
            int loc = 1;
            for (IMockData imp : impls) {
                log.info("{} 顺序号[{}], Mock数据生成算法名称[{}]", MockConstants.COMPONENT_NAME, loc++, imp.name());
            }
        }
        return impls;
    }

    /**
     * 将 <code>names</code> 代表的算法，逐一加入到列表(插入到最后一个元素"MOCK_ALG_MIN_RULE"之前)中。
     * 如果某个算法之前已经加入过，则忽略。
     *
     * @param implOrdered 已经加入到排序队列中的算法
     * @param impls       排好序的算法列表
     * @param names       本次按顺序待增加的算法
     */
    private static void order(Map<String, IMockData> implOrdered, List<IMockData> impls, String... names) {
        for (String name : names) {
            if (implOrdered.containsKey(name)) {
                continue;
            }
            if (!implRegistered.containsKey(name)) {
                log.error("{} Mock数据生成算法[{}]不存在，请仔细检查拼写是否有错误",
                        MockConstants.COMPONENT_NAME, name);
                continue;
            }
            implOrdered.put(name, implRegistered.get(name));
            impls.add(impls.size() - 1, implRegistered.get(name));
        }
    }

    /**
     * 按照优先级顺序，逐一尝试mock数据生成算法。
     * <pre>
     * 当某个算法返回<code>true</code>时，表示使用该算法已经得到了mock数据，返回该拦截对象的mock数据。
     * 如果所有算法都返回<code>false</code>，表示所有算法都没有正确生成mock数据，那么，不管三七二十一，仍然返回该拦截对象的mock数据(不论其是否为null)。
     * </pre>
     *
     * @param target 拦截目标
     * @param args   被拦截目标的参数值
     * @return 为该拦截目标生成的mock数据
     */
    public static Object data(MockTarget target, Object[] args) {
        init();
        for (IMockData impl : impls) {
            if (impl.execute(target, args)) {
                return target.getData();
            }
        }

        return target.getData();
    }
}
