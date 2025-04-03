package com.destinylight.tools.mock.demo.service;

import com.destinylight.tools.mock.demo.feigns.corp.UscMrtgAstInfFeignClient;
import com.destinylight.tools.mock.demo.pojo.corp.vo.UscMrtgAstInfResp;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 测试Mock服务器提供的mock接口
 * </p>
 *
 * @author 郑靖华 (11821967@qq.com)
 * @date 2025/3/14
 */
@Service
public class CorpService {
    private static final Logger log = LoggerFactory.getLogger(CorpService.class);

    @Autowired
    private UscMrtgAstInfFeignClient uscMrtgAstInfFeignClient;

    public void execute() {
        String id = "138";

        log.info("----  使用本地文件中配置的mock数据  ----");
        UscMrtgAstInfResp resp = uscMrtgAstInfFeignClient.queryFromLocalFile(id);
        log.info("返回值[{}]", JSON.toJSONString(resp));


        log.info("----  使用Mock服务器配置的mock数据  ----");
        resp = uscMrtgAstInfFeignClient.queryFromMockServer(id);
        log.info("返回值[{}]", JSON.toJSONString(resp));

        log.info("----  使用Mock服务器配置的mock数据后，再使用极简规则生成的mock数据  ----");
        resp = uscMrtgAstInfFeignClient.queryFromMockServerFail(id);
        log.info("返回值[{}]", JSON.toJSONString(resp));

        log.info("----  直接使用极简规则生成的mock数据  ----");
        resp = uscMrtgAstInfFeignClient.queryFromMinRule(id);
        log.info("返回值[{}]", JSON.toJSONString(resp));
    }
}
