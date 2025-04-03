package com.destinylight.tools.mock.data;

import com.destinylight.tools.mock.config.MockTarget;
import com.destinylight.tools.mock.utils.MockConstants;
import com.destinylight.tools.mock.utils.MockUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static com.destinylight.tools.mock.utils.MockConstants.MOCK_ALG_MIN_RULE;

/**
 * <p>
 * 根据极简规则(Minimalist Rules)生成Mock数据.
 * </p>
 *
 * @author 郑靖华 (11821967@qq.com)
 * @date 2025/3/16
 */
public class MockDataFromMinRule implements IMockData {
    private static final Logger log = LoggerFactory.getLogger(MockDataFromMinRule.class);

    @Override
    public boolean execute(MockTarget target, Object[] args) {
        if (MockConstants.DEBUG) {
            log.info("{} 拦截目标[{}]使用极简规则生成mock数据",
                    MockConstants.COMPONENT_NAME, target.getTargetStr());
        }
        try {
            target.setData(mock(target.getDataType()));
            if (MockConstants.DEBUG) {
                log.info("{} 拦截目标[{}]成功使用极简规则生成mock数据",
                        MockConstants.COMPONENT_NAME, target.getTargetStr());
            }
            return true;
        } catch (Exception e) {
            log.error("{} 拦截目标[{}]使用极简规则生成mock数据时出错[{}]",
                    MockConstants.COMPONENT_NAME, target.getTargetStr(), e.getMessage(), e);
            return false;
        }
    }

    @Override
    public String name() {
        return MOCK_ALG_MIN_RULE;
    }

    /**
     * 按照极简规则，给简单类型的成员变量赋值
     *
     * @param clz 类型
     * @return 该类型的Mock数据对象
     */
    private Object mock(Class<?> clz)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        // 如果返回值类型本身就是简单类型，则使用极简规则构造数据
        if (MockUtils.isSimpleType(clz)) {
            return mockDataForSimpleType(clz);
        }

        Object obj = clz.getDeclaredConstructor().newInstance();
        for (Field field : clz.getDeclaredFields()) {
            Class<?> fieldType = field.getType();
            if (MockConstants.DEBUG) {
                log.info("字段名称[{}] 字段类型名称(getName)[{}], getTypeName[{}]",
                        field.getName(), fieldType.getName(), fieldType.getTypeName());
            }

            if (Modifier.isStatic(field.getModifiers())) {
                // 静态字段，不需要赋值
                continue;
            }
            // final字段，需要不需要赋值呢？

            Object data = mockDataForSimpleType(fieldType);
            field.setAccessible(true);
            field.set(obj, data);
        }

        return obj;
    }

    /**
     * @param clz 类型，可能是简单类型，也可能不是简单类型
     * @return 按照极简规则生成数据。如果不是简单类型，则返回 <code>null</code>
     */
    private Object mockDataForSimpleType(Class<?> clz) {
        String typeName = clz.getTypeName();
        if ("int".equals(typeName) || "Integer".equals(typeName) || "java.lang.Integer".equals(typeName)) {
            return 1;
        } else if ("short".equals(typeName) || "Short".equals(typeName) || "java.lang.Short".equals(typeName)) {
            return (short) 1;
        } else if ("long".equals(typeName) || "Long".equals(typeName) || "java.lang.Long".equals(typeName)) {
            return (long) 1;
        } else if ("float".equals(typeName) || "Float".equals(typeName) || "java.lang.Float".equals(typeName)) {
            return (float) 1.0;
        } else if ("double".equals(typeName) || "Double".equals(typeName) || "java.lang.Double".equals(typeName)) {
            return (double) 1.0;
        } else if ("boolean".equals(typeName) || "Boolean".equals(typeName) || "java.lang.Boolean".equals(typeName)) {
            return true;
        } else if ("byte".equals(typeName) || "Byte".equals(typeName) || "java.lang.Byte".equals(typeName)) {
            return "b".getBytes()[0];
        } else if ("char".equals(typeName) || "Character".equals(typeName) || "java.lang.Character".equals(typeName)) {
            return 'c';
        } else if ("String".equals(typeName) || "java.lang.String".equals(typeName)) {
            return "s";
        }
        return null;
    }
}
