package com.destinylight.tools.mock.config;

import com.destinylight.tools.mock.utils.MockConstants;
import com.destinylight.tools.mock.utils.MockUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 向Mock服务器(元素设计平台)转发请求报文时，需要传递的HTTP HEADER数据的相关配置
 * </p>
 *
 * @author 郑靖华 (11821967@qq.com)
 * @date 2025/3/23
 */
public class MockHttpReqHeader {
    private static final Logger log = LoggerFactory.getLogger(MockHttpReqHeader.class);

    /**
     * 是否从客户端复制所有可以复制的HEAD到给元素设计平台的HTTP REQUEST HEADER中。默认值为true
     */
    private boolean copyFromClient = true;
    /**
     * 是否显示的配置了参数 <code>copyFromClient</code>
     */
    private boolean copyFromClientPresent = false;
    /**
     * 追加的HEAD(可以有相同的KEY)
     */
    private MultiValueMap<String, String> appends = new LinkedMultiValueMap<>();
    /**
     * 重写(如果没有，则忽略)的HEAD(不允许有相同的KEY)。
     * 要重写的HEAD，必须也是唯一的KEY。
     */
    private Map<String, String> updates = new HashMap<>();

    /**
     * 在给元素的报文中排除某些HEAD。
     * <p>
     * 如果全局配置中排除了某项，但在某个具体的目标中又配置了该HEAD的add或者update项，则忽略全局配置中该HEAD(不影响其他的HEAD)。
     * </p>
     */
    private List<String> exclusions = new ArrayList<>();

    public boolean isCopyFromClient() {
        return copyFromClient;
    }

    public MockHttpReqHeader setCopyFromClient(boolean copyFromClient) {
        this.copyFromClient = copyFromClient;
        return this;
    }

    public boolean isCopyFromClientPresent() {
        return copyFromClientPresent;
    }

    public MockHttpReqHeader setCopyFromClientPresent(boolean copyFromClientPresent) {
        this.copyFromClientPresent = copyFromClientPresent;
        return this;
    }

    public MultiValueMap<String, String> getAppends() {
        return appends;
    }

    public MockHttpReqHeader setAppends(MultiValueMap<String, String> appends) {
        this.appends = appends;
        return this;
    }

    public Map<String, String> getUpdates() {
        return updates;
    }

    public MockHttpReqHeader setUpdates(Map<String, String> updates) {
        this.updates = updates;
        return this;
    }

    public List<String> getExclusions() {
        return exclusions;
    }

    public void setExclusions(List<String> exclusions) {
        this.exclusions = exclusions;
    }

    /**
     * 解析HTTP REQUEST HEADER的配置项
     * <pre>
     * JSON格式如下:
     * 1. 只定义是否复制客户端的HTTP HEAD(默认值为true):
     *    "header": false
     * 2. 定义更复杂的规则:
     *    "header": {
     *       "copyClient": false,  // 可选, 是否复制客户端的HTTP HEAD(默认值为true).
     *       "add": [              // 可选，在给元素的报文中新增加HEAD
     *           {"name1": "value1"},
     *           {"name2": "value2"}
     *       ],
     *       "update": [           // 可选，在给元素的报文中修改HEAD。如果之前没有，则忽略
     *           {"name3": "value3"},
     *           {"name4": "value4"}
     *       ],
     *       "exclude": [           // 可选，在给元素的报文中排除某些HEAD
     *           "name5",
     *           "name6"
     *       ]
     *    }
     * </pre>
     *
     * @param obj 配置文件中的参数
     * @return HTTP HEADER数据的相关配置
     */
    public static MockHttpReqHeader parse(Object obj) {
        MockHttpReqHeader header = new MockHttpReqHeader();
        if (obj == null) {
            return header;
        }
        if (obj instanceof Boolean) {
            header.setCopyFromClientPresent(true);
            return header.setCopyFromClient(MockUtils.nullAs((Boolean) obj, true));
        } else if (!(obj instanceof JSONObject)) {
            // 我们不认识的配置格式
            log.error("{} 解析HTTP HEAD的[header]失败，错误的语法", MockConstants.COMPONENT_NAME);
            return header;
        }

        JSONObject jsonObject = (JSONObject) obj;
        // 为了避免某一个配置项语法错误对其他配置项的影响，我们对每一个配置项解析可能出现的异常，都单独catch
        try {
            if (jsonObject.getBoolean("copyClient") != null) {
                header.setCopyFromClientPresent(true);
            }
            header.setCopyFromClient(MockUtils.nullAs(jsonObject.getBoolean("copyClient"), true));
        } catch (Exception e) {
            log.error("{} 解析HTTP HEAD的[copyClient]失败，错误信息[{}]", MockConstants.COMPONENT_NAME, e.getMessage(), e);
        }
        try {
            header.setAppends(parseAdds(jsonObject.getJSONArray("add")));
        } catch (Exception e) {
            log.error("{} 解析HTTP HEAD的[add]失败，错误信息[{}]", MockConstants.COMPONENT_NAME, e.getMessage(), e);
        }
        try {
            header.setUpdates(parseUpdates(jsonObject.getJSONArray("update")));
        } catch (Exception e) {
            log.error("{} 解析HTTP HEAD的[update]失败，错误信息[{}]", MockConstants.COMPONENT_NAME, e.getMessage(), e);
        }
        try {
            header.setExclusions(parseExclusions(jsonObject.getJSONArray("exclude")));
        } catch (Exception e) {
            log.error("{} 解析HTTP HEAD的[exclude]失败，错误信息[{}]", MockConstants.COMPONENT_NAME, e.getMessage(), e);
        }
        return header;
    }

    private static MultiValueMap<String, String> parseAdds(JSONArray jsonArray) {
        MultiValueMap<String, String> appends = new LinkedMultiValueMap<>();
        if (jsonArray == null || jsonArray.size() < 1) {
            return appends;
        }
        for (HashMap<String, String> map : jsonArray.toJavaList(HashMap.class)) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                appends.add(entry.getKey(), entry.getValue());
            }
        }
        return appends;
    }

    private static Map<String, String> parseUpdates(JSONArray jsonArray) {
        Map<String, String> updates = new HashMap<>();
        if (jsonArray == null || jsonArray.size() < 1) {
            return updates;
        }
        for (HashMap<String, String> map : jsonArray.toJavaList(HashMap.class)) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                updates.put(entry.getKey(), entry.getValue());
            }
        }
        return updates;
    }

    private static List<String> parseExclusions(JSONArray jsonArray) {
        List<String> exclusions = new ArrayList<>();
        if (jsonArray == null || jsonArray.size() < 1) {
            return exclusions;
        }
        for (String name : jsonArray.toJavaList(String.class)) {
            exclusions.add(name);
        }
        return exclusions;
    }
}
