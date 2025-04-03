package com.destinylight.tools.mock.demo.pojo.crm.vo;

/**
 * <p>
 * 个人信息
 * </p>
 *
 * @author 郑靖华 (11821967@qq.com)
 * @date 2025/3/10
 */
public class PersonInfo {
    private String id;
    private String name;
    private Integer age;

    public String getId() {
        return id;
    }

    public PersonInfo setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public PersonInfo setName(String name) {
        this.name = name;
        return this;
    }

    public Integer getAge() {
        return age;
    }

    public PersonInfo setAge(Integer age) {
        this.age = age;
        return this;
    }
}
