package com.destinylight.tools.mock.config;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Mock拦截器的全局参数
 * </p>
 *
 * @author 郑靖华 (11821967@qq.com)
 * @date 2025/3/19
 */
public class MockConfig {
    /**
     * 是否禁用Mock拦截器组件, 默认值是禁用
     */
    private boolean disable = true;
    /**
     * Mock服务器(元素设计平台)Mock数据生成的URL
     */
    private String server;
    /**
     * 是否拦截所有的代理类，而不仅仅是局限于名称匹配"$Proxy"或者"$EnhancerBySpringCGLIB"的代理类的名称。
     * 基于YUSP开发的应用，其代码类，可能不是这个名称，因此，我们需要先将所有的代理类都拦截一遍，再配置对应的正则表达式。
     */
    private boolean interceptAllProxy = false;
    /**
     * 可以手工配置主应用通过Spring等为FeignClient等生成的接口类的名称的正则表达式
     */
    private List<String> proxyPattern;

    /**
     * 是否需要BYTEBUDDY将生成的代码类的字节码输出到文件中
     */
    private boolean dump = false;
    /**
     * 如果需要BYTEBUDDY将生成的代码类的字节码输出到文件中，则在此指定保存文件的目录
     */
    private String dumpPath;
    /**
     * HTTP REQUEST HEADER的配置
     */
    private MockHttpReqHeader header = new MockHttpReqHeader();
    /**
     * Mock数据来源算法的尝试顺序
     */
    private List<String> mockSourceOrder = new ArrayList<>();

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public boolean isInterceptAllProxy() {
        return interceptAllProxy;
    }

    public List<String> getProxyPattern() {
        return proxyPattern;
    }

    public void setProxyPattern(List<String> proxyPattern) {
        this.proxyPattern = proxyPattern;
    }

    public void setInterceptAllProxy(boolean interceptAllProxy) {
        this.interceptAllProxy = interceptAllProxy;
    }

    public boolean isDump() {
        return dump;
    }

    public void setDump(boolean dump) {
        this.dump = dump;
    }

    public String getDumpPath() {
        return dumpPath;
    }

    public void setDumpPath(String dumpPath) {
        this.dumpPath = dumpPath;
    }

    public MockHttpReqHeader getHeader() {
        return header;
    }

    public void setHeader(MockHttpReqHeader header) {
        this.header = header;
    }

    public List<String> getMockSourceOrder() {
        return mockSourceOrder;
    }

    public void setMockSourceOrder(List<String> mockSourceOrder) {
        this.mockSourceOrder = mockSourceOrder;
    }
}
