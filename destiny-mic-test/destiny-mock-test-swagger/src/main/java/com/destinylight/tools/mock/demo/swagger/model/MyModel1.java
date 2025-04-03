package com.destinylight.tools.mock.demo.swagger.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "MyModel1 entity")
public class MyModel1 {

    @ApiModelProperty(value = "Unique identifier", required = true)
    private Long id;
    @ApiModelProperty(value = "Property one")
    private String property1;
    @ApiModelProperty(value = "Property two")
    private String property2;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProperty1() {
        return property1;
    }

    public void setProperty1(String property1) {
        this.property1 = property1;
    }

    public String getProperty2() {
        return property2;
    }

    public void setProperty2(String property2) {
        this.property2 = property2;
    }
}
