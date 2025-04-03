package com.destinylight.tools.mock.config;

import com.destinylight.tools.mock.utils.MockUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * Mock拦截目标
 * </p>
 *
 * @author 郑靖华 (11821967@qq.com)
 * @date 2025/3/11
 */
public class MockTarget {
    /**
     * 目标的主键
     */
    private Key key = new Key();
    /**
     * 是否禁用对当前目标的拦截。默认值为<code>true</code>。
     */
    private boolean enable = true;
    /**
     * 原始的Mock拦截目标配置串。来自于Mock数据配置文件。
     */
    private String targetStr;
    /**
     * Mock数据配置文件的全路径名称
     */
    private String filename;
    /**
     * Mock数据配置文件
     */
    private File file;
    /**
     * Mock数据文件的最后修改时间
     */
    private long fileUpdatedTime;

    /**
     * 拦截目标的类
     */
    private Class<?> targetType;
    /**
     * 拦截目标的方法
     */
    private Method method;
    /**
     * 拦截目标的方法的参数
     */
    private List<Param> params;
    /**
     * 返回值类型是否是void或者Void
     */
    private boolean dataTypeVoid = false;
    /**
     * Mock数据的类名称
     */
    private String dataTypeName;
    /**
     * Mock数据的类(泛型)。通过 <code>Method.getGenericReturnType()</code> 获得的泛型的返回类型
     */
    private Type dataGenericType;
    /**
     * Mock数据的类。通过 <code>Method.getReturnType()</code> 获得的返回类型
     */
    private Class<?> dataType;
    /**
     * Mock数据的data。原始的JSON对象，是Mock数据文件中"data"子节点下的内容。
     */
    private Object dataJson;
    /**
     * Mock数据(之前已经反序列化后得到的数据)
     */
    private Object data;
    /**
     * 是否是FeignClient接口类
     */
    private boolean feignClient;
    /**
     * 微服务或者应用名称，在<code>@FeignClient(name=?)</code>中指定的字符串。
     * 在调用Mock服务器(元素设计平台)的Mock服务接口时，该名称将被写入到HTTP HEADER里。
     */
    private String microServiceName;
    /**
     * 在<code>@FeignClient(path=?)</code>中指定的字符串，该字符串将被插入到该FeignClient中所有方法的中的HTTP URL前面。
     */
    private String contextPath;
    /**
     * 该方法所代表的HTTP METHOD
     */
    private RequestMethod httpMethod;
    /**
     * 该方法所代表的HTTP URL
     */
    private String httpUrl;
    /**
     * 是否是第一次拦截本目标
     */
    private boolean firstInvoke = true;
    /**
     * 是否每一次调用时，都使用最新的数据，而不是上一次已经生成的数据。
     */
    private boolean refreshEveryTime = true;
    /**
     * 是否允许从Mock服务器(元素设计平台)获得mock数据。默认值为true。
     */
    private boolean shouldFromMockServer = true;
    /**
     * HTTP REQUEST HEADER的配置
     */
    private MockHttpReqHeader header = new MockHttpReqHeader();

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getTargetStr() {
        return targetStr;
    }

    public void setTargetStr(String targetStr) {
        this.targetStr = targetStr;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Class<?> getTargetType() {
        return targetType;
    }

    public void setTargetType(Class<?> targetType) {
        this.targetType = targetType;
    }

    public String getTargetTypeName() {
        return key.getTargetTypeName();
    }

    public void setTargetTypeName(String targetTypeName) {
        key.setTargetTypeName(targetTypeName);
    }

    public String getTargetMethodName() {
        return key.getTargetMethodName();
    }

    public void setTargetMethodName(String targetMethodName) {
        key.setTargetMethodName(targetMethodName);
    }

    public String getTargetMethodParams() {
        return key.getTargetMethodParams();
    }

    public void setTargetMethodParams(String targetMethodParams) {
        key.setTargetMethodParams(targetMethodParams);
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public List<Param> getParams() {
        return params;
    }

    public void setParams(List<Param> params) {
        this.params = params;
    }

    public long getFileUpdatedTime() {
        return fileUpdatedTime;
    }

    public void setFileUpdatedTime(long fileUpdatedTime) {
        this.fileUpdatedTime = fileUpdatedTime;
    }

    public boolean isDataTypeVoid() {
        return dataTypeVoid;
    }

    public void setDataTypeVoid(boolean dataTypeVoid) {
        this.dataTypeVoid = dataTypeVoid;
    }

    public String getDataTypeName() {
        return dataTypeName;
    }

    public void setDataTypeName(String dataTypeName) {
        this.dataTypeName = dataTypeName;
    }

    public Type getDataGenericType() {
        return dataGenericType;
    }

    public void setDataGenericType(Type dataGenericType) {
        this.dataGenericType = dataGenericType;
    }

    public Class<?> getDataType() {
        return dataType;
    }

    public void setDataType(Class<?> dataType) {
        this.dataType = dataType;
    }

    public Object getDataJson() {
        return dataJson;
    }

    public void setDataJson(Object dataJson) {
        this.dataJson = dataJson;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public boolean isFeignClient() {
        return feignClient;
    }

    public void setFeignClient(boolean feignClient) {
        this.feignClient = feignClient;
    }

    public String getMicroServiceName() {
        return microServiceName;
    }

    public void setMicroServiceName(String microServiceName) {
        this.microServiceName = microServiceName;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public RequestMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(RequestMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getHttpUrl() {
        return httpUrl;
    }

    public void setHttpUrl(String httpUrl) {
        this.httpUrl = httpUrl;
    }

    public boolean isFirstInvoke() {
        return firstInvoke;
    }

    public void setFirstInvoke(boolean firstInvoke) {
        this.firstInvoke = firstInvoke;
    }

    public boolean isRefreshEveryTime() {
        return refreshEveryTime;
    }

    public void setRefreshEveryTime(boolean refreshEveryTime) {
        this.refreshEveryTime = refreshEveryTime;
    }

    public boolean isShouldFromMockServer() {
        return shouldFromMockServer;
    }

    public void setShouldFromMockServer(boolean shouldFromMockServer) {
        this.shouldFromMockServer = shouldFromMockServer;
    }

    public MockHttpReqHeader getHeader() {
        return header;
    }

    public void setHeader(MockHttpReqHeader header) {
        this.header = header;
    }

    public static class Key {
        /**
         * 拦截目标的类的名称
         */
        private String targetTypeName;
        /**
         * 拦截目标的方法名称
         */
        private String targetMethodName;

        /**
         * 拦截目标的方法的参数类型。形如以下中的某一种:
         * <pre>
         * 1. <code>()</code> - 该方法没有参数。或者该方法名在该类中是唯一的，可以忽略参数。
         * 2. <code>(Integer,boolean,SomePerson,List)</code> - 有参数，参数类型之间用逗号分隔，不允许有空格，可能还需要使用泛型。
         * </pre>
         */
        private String targetMethodParams = "()";

        public String getTargetTypeName() {
            return targetTypeName;
        }

        public Key setTargetTypeName(String targetTypeName) {
            this.targetTypeName = targetTypeName;
            return this;
        }

        public String getTargetMethodName() {
            return targetMethodName;
        }

        public Key setTargetMethodName(String targetMethodName) {
            this.targetMethodName = targetMethodName;
            return this;
        }

        public String getTargetMethodParams() {
            return targetMethodParams;
        }

        public Key setTargetMethodParams(String targetMethodParams) {
            this.targetMethodParams = MockUtils.isBlank(targetMethodParams) ? "()" : targetMethodParams;
            return this;
        }

        /**
         * 要将{@link Key}作为<code>Map</code>的<code>key</code>，必须要重载<code>equals()</code>和<code>hashCode()</code>这2个方法.
         *
         * @param o 待比较的对象
         * @return 是否与待比较的对象相同
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Key k = (Key) o;

            // 依次比较类名称、方法名称、方法参数
            return (equal(this.targetTypeName, k.targetTypeName)
                    && equal(this.targetMethodName, k.targetMethodName)
                    && equal(this.targetMethodParams, k.targetMethodParams));
        }

        /**
         * 比较2个字符串是否相同
         *
         * @param s 字符串1
         * @param t 字符串2
         * @return 2个字符串是否相同
         */
        private boolean equal(String s, String t) {
            if ((s == null || t == null) && !(s == null && t == null)) {
                // 2个字符串只有一个是null
                return false;
            } else if (s != null && !s.equals(t)) {
                // 2个字符串都不是null，并且不相同
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            return Objects.hash(targetTypeName, targetMethodName, targetMethodParams);
        }
    }

    /**
     * 方法的参数
     */
    public static class Param {
        /**
         * 参数的原始名称，通过字节码解析得到的名称。
         * <pre>
         * 例如: foo(@PathVariable("id") String id)
         * 1. 如果在编译时指定了"-parameter"选项，则 <code>origName</code> 等于 <code>id</code> 。
         * 2. 如果在编译时没有指定"-parameter"选项，则 <code>origName</code> 可能是 <code>arg1</code> 。
         * </pre>
         */
        private String origName;
        /**
         * 经过修正后的参数名称。在 <code>origName</code> 的基础上，再根据参数上面的注解中携带的信息，补充得到参数名称
         * <pre>
         * 1. 如果该参数上有 {@link PathVariable} 之类的注解，并在注解里给出了参数名称，则取值为注解中给出的名称，本例中 <code>name</code> 等于 <code>id</code> 。
         * 2. 如果在编译时没有 {@link PathVariable} 之类的注解，则 <code>name</code> 等于 <code>origName</code> 。
         * </pre>
         */
        private String name;
        /**
         * 参数的类型
         */
        private Class<?> clz;
        /**
         * 是否必须指定该参数的值
         */
        private boolean required = false;
        /**
         * 参数的种类
         *
         * @see MockTarget.ParamType
         */
        private ParamType type;
        /**
         * 默认值
         */
        private String defaultValue;

        public String getOrigName() {
            return origName;
        }

        public void setOrigName(String origName) {
            this.origName = origName;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Class<?> getClz() {
            return clz;
        }

        public void setClz(Class<?> clz) {
            this.clz = clz;
        }

        public boolean isRequired() {
            return required;
        }

        public void setRequired(boolean required) {
            this.required = required;
        }

        public ParamType getType() {
            return type;
        }

        public void setType(ParamType type) {
            this.type = type;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }
    }

    /**
     * FeignClient方法的参数的种类
     */
    public enum ParamType {
        /**
         * {@link PathVariable}
         */
        PATH,
        /**
         * {@link org.springframework.web.bind.annotation.RequestParam}
         */
        QUERY,
        /**
         * {@link org.springframework.web.bind.annotation.RequestBody}
         */
        BODY,
        /**
         * {@link org.springframework.web.bind.annotation.RequestHeader}
         */
        HEAD,
        /**
         * {@link org.springframework.web.bind.annotation.RequestAttribute}
         */
        ATTR;
    }
}
