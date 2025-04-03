package com.destinylight.tools.mock.data;

import com.destinylight.tools.mock.config.MockTarget;

/**
 * 生成Mock数据。
 * 之所以采用接口类+实现类的方式，是为了考虑到未来的扩展性，比如通过自定义函数及表达式等生成mock数据等。
 * 以后如果需要增加新的算法，只需要开发算法后注册，不需要修改其他算法的程序。
 * <pre>
 * 目前已知的情况，Mock数据可能来自于：
 * 1. 本地Mock文件。
 * 2. Mock服务器。
 * 3. 极简规则。
 * </pre>
 *
 * @author 郑靖华 (11821967@qq.com)
 * @date 2025/3/16
 */
public interface IMockData {
    /**
     * 为指定的拦截目标生成mock数据
     *
     * @param target 拦截目标
     * @param args   被拦截目标的参数值
     * @return <code>true</code>表示数据生成成功，不需要再继续尝试后续的算法。
     * <code>false</code>表示数据生成失败，还需要继续尝试后续的算法。
     */
    default boolean execute(MockTarget target, Object[] args) {
        return true;
    }

    /**
     * @return Mock数据生成算法的名称
     */
    String name();
}
