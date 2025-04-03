package com.destinylight.tools.mock.data;

import com.destinylight.tools.mock.config.MockTarget;
import com.destinylight.tools.mock.utils.MockConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.destinylight.tools.mock.utils.MockConstants.MOCK_ALG_FROM_CACHE;

/**
 * <p>
 * 直接返回上一次得到的mock数据
 * </p>
 *
 * @author 郑靖华 (11821967@qq.com)
 * @date 2025/3/16
 */
public class MockDataFromLastTime implements IMockData {
    private static final Logger log = LoggerFactory.getLogger(MockDataFromLastTime.class);

    @Override
    public boolean execute(MockTarget target, Object[] args) {
        if (target.isRefreshEveryTime()) {
            if (MockConstants.DEBUG) {
                log.info("{} 拦截目标[{}]需要每一次都刷新使用新的mock数据",
                        MockConstants.COMPONENT_NAME, target.getTargetStr());
            }
            return false;
        }
        if (MockConstants.DEBUG) {
            log.info("{} 拦截目标[{}]不需要每一次都刷新使用新的mock数据，可以使用之前已经生成的mock数据",
                    MockConstants.COMPONENT_NAME, target.getTargetStr());
        }

        boolean ret = target.getData() != null;
        if (MockConstants.DEBUG) {
            if (ret) {
                log.info("{} 拦截目标[{}]之前已经有mock数据，可以直接返回该mock数据",
                        MockConstants.COMPONENT_NAME, target.getTargetStr());
            } else {
                log.info("{} 拦截目标[{}]还没有mock数据，需要继续尝试",
                        MockConstants.COMPONENT_NAME, target.getTargetStr());
            }
        }
        return ret;
    }

    @Override
    public String name() {
        return MOCK_ALG_FROM_CACHE;
    }
}
