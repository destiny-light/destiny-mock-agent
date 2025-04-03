package com.destinylight.tools.mock.config;

import com.destinylight.tools.mock.utils.MockConstants;
import com.destinylight.tools.mock.utils.MockUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * Mock服务器配置文件解析
 * </p>
 *
 * @author 郑靖华 (11821967@qq.com)
 * @date 2025/3/11
 */
public class MockServerFileParser {
    private static final Logger log = LoggerFactory.getLogger(MockServerFileParser.class);

    /**
     * 解析固定目录[${user.dir}/mock]下的名为"mock-server.json"的文件。
     *
     * @return Mock拦截器全局参数
     */
    public MockConfig parse() {
        File file = MockUtils.serverConfigFile();
        if (!file.exists()) {
            log.info("{} Mock拦截器组件配置文件[{}]不存在", MockConstants.COMPONENT_NAME, file.getAbsolutePath());
            return new MockConfig();
        }

        return parse(file);
    }

    /**
     * 解析指定名称的Mock拦截器组件配置文件。
     *
     * @return Mock拦截器全局参数
     */
    public MockConfig parse(File file) {
        log.info("{} 解析Mock服务器配置文件[{}]", MockConstants.COMPONENT_NAME, file.getAbsolutePath());
        MockConfig config = new MockConfig();
        try {
            JSONObject json = MockUtils.parseJsonFile(file);
            if (json == null) {
                return config;
            }
            String server = json.getString("server");
            config.setServer(MockUtils.isBlank(server) ? null : server.trim());
            config.setDisable(MockUtils.nullAs(json.getBoolean("disable"), false));
            // 为了降低主方法的复杂度，将其中部分代码，移到单独的方法里来。
            config.setInterceptAllProxy(allProxy(json.getBoolean("allProxy")));
            config.setDump(dump(json.getBoolean("dump")));
            config.setDumpPath(dumpPath(json.getString("dumpPath"), config.isDump()));
            config.setInterceptAllProxy(allProxy(json.getBoolean("allProxy")));
            config.setProxyPattern(proxyPattern(json.get("proxyPattern")));
            config.setHeader(MockHttpReqHeader.parse(json.get("header")));
            config.setMockSourceOrder(mockSourceOrder(json.get("mockOrder")));
        } catch (Exception e) {
            log.error("{} 解析Mock拦截器组件配置文件[{}]失败[{}]",
                    MockConstants.COMPONENT_NAME, file.getAbsolutePath(), e.getMessage());
        }
        if (MockConstants.DEBUG) {
            log.info("{} 全局参数[{}]", MockConstants.COMPONENT_NAME, JSONObject.toJSONString(config));
        }
        return config;
    }

    /**
     * @param obj 字符串数组，按顺序列出mock数据来源算法的尝试顺序
     * @return mock数据来源算法的顺序
     */
    private List<String> mockSourceOrder(Object obj) {
        try {
            if (obj instanceof JSONArray) {
                JSONArray array = (JSONArray) obj;
                return array.toJavaList(String.class);
            }
        } catch (Exception e) {
            log.error("{} 解析Mock拦截器组件配置文件中的mock数据来源算法的顺序参数[{}]失败[{}]",
                    MockConstants.COMPONENT_NAME, obj, e.getMessage(), e);
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * 如果Spring启动时为FeignClient等生成的代理类的名称，不是我们内置的正则表达式，用户可以在配置文件中指定该正则表达式。
     * <pre>
     * 内置的正则表达式包括：
     * 1. .*\$Proxy\d+
     * 2. .*\$\$EnhancerBySpringCGLIB\$.*
     * </pre>
     *
     * @param obj 可以是单独的字符串，也可以是一个字符串数组
     * @return Spring启动时为FeignClient等生成的代理类的名称的正则表达式
     */
    private List<String> proxyPattern(Object obj) {
        try {
            if (obj instanceof String) {
                String pattern = (String) obj;
                List<String> patterns = new ArrayList<>();
                patterns.add(pattern);
                return patterns;
            } else if (obj instanceof JSONArray) {
                JSONArray array = (JSONArray) obj;
                return array.toJavaList(String.class);
            }
        } catch (Exception e) {
            log.error("{} 解析Mock拦截器组件配置文件中的代理类名称正则表达式参数[{}]失败[{}]",
                    MockConstants.COMPONENT_NAME, obj, e.getMessage(), e);
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * 我之所以要把这个语句拿来做成一个方法，只是为了写以下注释：
     * <p>
     * 如果我们不确定Spring启动时为FeignClient等生成的代理类的名称是什么样子的，
     * 那么，我们可以在配置文件中将本参数值设置为true，确定了之后，再删除本参数即可。
     * </p>
     *
     * @param value 配置文件中设置的值
     * @return 是否拦截所有方法，而不仅仅是指定名称的代理类的方法
     */
    private boolean allProxy(Boolean value) {
        return MockUtils.nullAs(value, false);
    }

    /**
     * 我之所以要把这个语句拿来做成一个方法，只是为了写以下注释：
     * <p>
     * 如果我们希望将bytebuddy生成的代理类，输出到"dumpPath"指定的目录下。
     * 那么，我们可以在配置文件中将本参数值设置为true。
     * </p>
     * <p>
     * 需要特别注意的是，如果将"allProxy"参数设置为了<code>true</code>，那么，最好不要将本参数设置为<code>true</code>，
     * 因为，这可能会导致bytebuddy生成大量的增强后的字节码到文件系统中，可能会导致系统启动用时非常久甚至于失败。
     * </p>
     *
     * @param value 配置文件中设置的值
     * @return 是否将bytebuddy生成的代理类，输出到"dumpPath"指定的目录下。
     */
    private boolean dump(Boolean value) {
        return MockUtils.nullAs(value, false);
    }

    /**
     * 将bytebuddy生成的代理类，输出到指定目录下。
     *
     * @param pathname 相对路径名或者全路径名
     * @param dump     是否真的要dump字节码
     * @return 全路径名
     */
    private String dumpPath(String pathname, boolean dump) {
        if (MockUtils.isBlank(pathname)) {
            return null;
        }
        try {
            File path = new File(pathname);
            if (dump) {
                if (!path.exists()) {
                    path.mkdirs();
                } else if (path.listFiles() != null && path.listFiles().length > 0) {
                    // 如果该目录下有之前产生的文件，本程序不负责删除，由用户自行删除。我们在这里只是给出提示
                    log.info("{} 字节码增强后的class的输出目录[{}]不为空，请用户自行判断是否应该在启动本应用之前清空该目录并自行处理",
                            MockConstants.COMPONENT_NAME, path.getAbsolutePath());
                }
                System.setProperty("net.bytebuddy.dump", path.getAbsolutePath());
            }
            return path.getAbsolutePath();
        } catch (Exception e) {
            log.error("{} 解析Mock拦截器组件配置文件中的dump目录参数[{}]失败[{}]",
                    MockConstants.COMPONENT_NAME, pathname, e.getMessage(), e);
            return null;
        }
    }
}