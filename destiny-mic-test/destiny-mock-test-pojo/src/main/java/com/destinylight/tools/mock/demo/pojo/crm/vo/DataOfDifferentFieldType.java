package com.destinylight.tools.mock.demo.pojo.crm.vo;

/**
 * <p>
 * 服务端返回的报文体中的某个字段，与客户端使用的实体类的字段类型不同。
 * 本类是在客户端使用的。
 * </p>
 *
 * @author 郑靖华 (11821967@qq.com)
 * @date 2025/3/24
 */
public class DataOfDifferentFieldType {
    /**
     * 客户端和服务端都是"String"
     */
    private String fld1;
    /**
     * 客户端是"int"，服务端是"String"
     */
    private int fld2;
    /**
     * 客户端是"String"，服务端是"int"
     */
    private String fld3;

    public String getFld1() {
        return fld1;
    }

    public void setFld1(String fld1) {
        this.fld1 = fld1;
    }

    public int getFld2() {
        return fld2;
    }

    public void setFld2(int fld2) {
        this.fld2 = fld2;
    }

    public String getFld3() {
        return fld3;
    }

    public void setFld3(String fld3) {
        this.fld3 = fld3;
    }
}
