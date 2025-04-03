package com.destinylight.tools.mock.data;

import com.destinylight.tools.mock.config.MockTarget;
import com.destinylight.tools.mock.utils.MockConstants;
import com.destinylight.tools.mock.utils.MockUtils;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.destinylight.tools.mock.utils.MockConstants.MOCK_ALG_FROM_LOCAL_FILE;

/**
 * <p>
 * 根据本地的Mock数据配置文件生成mock数据
 * </p>
 *
 * @author 郑靖华 (11821967@qq.com)
 * @date 2025/3/16
 */
public class MockDataFromLocalFile implements IMockData {
    private static final Logger log = LoggerFactory.getLogger(MockDataFromLocalFile.class);

    @Override
    public boolean execute(MockTarget target, Object[] args) {
        Class<?> returnType = target.getDataType();
        try {
            if (MockConstants.DEBUG) {
                log.info("{} 拦截目标[{}]尝试使用本地Mock数据配置文件中的mock数据",
                        MockConstants.COMPONENT_NAME, target.getTargetStr());
            }
            if (target.getFile() == null || !target.getFile().exists()) {
                // 本地mock数据配置文件不存在
                if (MockConstants.DEBUG) {
                    log.info("{} 拦截目标[{}]的本地Mock数据配置文件不存在",
                            MockConstants.COMPONENT_NAME, target.getTargetStr());
                }
                return false;
            }
            // 即使时间戳没有发生变化，并且拦截目标的"dataJson"和"data"都不为空，
            // 为了以后引入自定义函数和表达式，也仍然重新计算一次Mock数据。
            long lastModified = target.getFile().lastModified();

            // Mock数据
            JSONObject json = MockUtils.parseJsonFile(target.getFile());
            Object dataJson = json.get("data");
            if ((dataJson == null)) {
                // 本地mock数据配置文件没有配置mock数据
                if (MockConstants.DEBUG) {
                    log.info("{} 拦截目标[{}]的本地Mock数据配置文件[{}]中没有[data]元素",
                            MockConstants.COMPONENT_NAME, target.getTargetStr(), target.getFile().getAbsoluteFile());
                }
                return false;
            }
            if (MockConstants.DEBUG) {
                log.info("JSON节点[data]的数据类型是[{}]", dataJson.getClass().getName());
                log.info("拦截目标的返回值类型是[{}]", returnType.getName());
            }
            target.setDataJson(dataJson);

            // 反序列化
            target.setData(deserialize(dataJson, returnType));
            if (MockConstants.DEBUG) {
                log.info("{} 拦截目标[{}]成功解析本地Mock数据配置文件中的mock数据",
                        MockConstants.COMPONENT_NAME, target.getTargetStr());
            }
            target.setFileUpdatedTime(lastModified);
            return true;
        } catch (Exception e) {
            log.error("{} 拦截目标[{}]从本地Mock数据配置文件生成mock数据时出错[{}]",
                    MockConstants.COMPONENT_NAME, target.getTargetStr(), e.getMessage(), e);
            return false;
        }
    }

    @Override
    public String name() {
        return MOCK_ALG_FROM_LOCAL_FILE;
    }

    /**
     * 将JSON中读入的数据，反序列化为指定的返回值类型
     *
     * @param data       从JSON文件中读取的"data"节点的内容
     * @param returnType 拦截目标的返回值类型
     * @return 拦截目标的Mock数据对象
     */
    private Object deserialize(Object data, Class<?> returnType) {
        // 简单数据类型
        if (isSimpleType(returnType.getName())) {
            return data;
        }

        // 简单类型的集合类型

        // 如果"data"是"com.alibaba.fastjson.JSONObject"，表示还需要进一步的反序列化
        if (isJSONType(data.getClass().getName())) {
            JSONObject data0 = (JSONObject) data;
            return data0.toJavaObject(returnType);
        }

        return data;
    }


    /**
     * @param dataTypeName 数据类型的名称
     * @return 是否是JSON数据类型
     */
    private boolean isJSONType(String dataTypeName) {
        return "JSONObject".equals(dataTypeName) || "com.alibaba.fastjson.JSONObject".equals(dataTypeName);
    }

    /**
     * @param typeName 类型的名称
     * @return 是否是简单数据类型
     */
    private boolean isSimpleType(String typeName) {
        return "String".equals(typeName) || "java.lang.String".equals(typeName);
    }
}
