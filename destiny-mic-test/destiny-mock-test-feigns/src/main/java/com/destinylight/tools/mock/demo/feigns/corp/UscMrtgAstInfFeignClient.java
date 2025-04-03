package com.destinylight.tools.mock.demo.feigns.corp;

import com.destinylight.tools.mock.demo.feigns.Const;
import com.destinylight.tools.mock.demo.pojo.corp.vo.UscMrtgAstInfResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * <p>
 * [POST /api/w/demom/corp/uscmrtgastinf/*]
 * </p>
 *
 * @author 郑靖华 (11821967@qq.com)
 * @date 2025/3/14
 */
@FeignClient(name = "corp", url = Const.SIMULATOR_URL)
public interface UscMrtgAstInfFeignClient {
    /**
     * 从Mock服务器(元素设计平台)取得mock数据
     *
     * @param id
     * @return
     */
    @PostMapping("/api/w/demom/corp/uscmrtgastinf/query")
    UscMrtgAstInfResp queryFromMockServer(@RequestParam("id") String id);

    /**
     * 从Mock服务器(元素设计平台)取得mock数据，但Mock服务器生成数据失败。之后，会使用极简规则生成mock数据。
     *
     * @param id
     * @return
     */
    @GetMapping("/api/w/demom/corp/uscmrtgastinf/query")
    UscMrtgAstInfResp queryFromMockServerFail(@RequestParam("id") String id);

    /**
     * 从本地Mock数据配置文件取得mock数据
     *
     * @param id
     * @return
     */
    @GetMapping("/api/w/demom/corp/uscmrtgastinf/query1")
    UscMrtgAstInfResp queryFromLocalFile(@RequestParam("id") String id);

    /**
     * 不尝试使用Mock服务器(元素设计平台)取得mock数据，直接使用极简规则生成mock数据
     *
     * @param id
     * @return
     */
    @GetMapping("/api/w/demom/corp/uscmrtgastinf/query3")
    UscMrtgAstInfResp queryFromMinRule(@RequestParam("id") String id);
}
