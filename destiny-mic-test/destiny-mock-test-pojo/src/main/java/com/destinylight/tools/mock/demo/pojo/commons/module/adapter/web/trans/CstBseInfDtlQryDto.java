package com.destinylight.tools.mock.demo.pojo.commons.module.adapter.web.trans;

/**
 * <p>
 * 用于测试的某个交易的信息
 * </p>
 *
 * @author 郑靖华 (11821967@qq.com)
 * @date 2025/3/25
 */
public class CstBseInfDtlQryDto {
    /**
     * 客户号
     **/
    private String cstId;
    /**
     * 客户类型
     **/
    private String cstTyp;

    public String getCstId() {
        return cstId;
    }

    public void setCstId(String cstId) {
        this.cstId = cstId;
    }

    public String getCstTyp() {
        return cstTyp;
    }

    public void setCstTyp(String cstTyp) {
        this.cstTyp = cstTyp;
    }
}
