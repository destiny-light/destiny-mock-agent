package com.destinylight.tools.mock.demo.swagger.controller;

import com.destinylight.tools.mock.demo.swagger.model.MyModel1;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "MyController1")
@RequestMapping("/api/mycontroller1")
public class MyController1 {

    @PostMapping
    @ApiOperation(value = "Create a new MyModel1", response = MyModel1.class)
    public MyModel1 create(@RequestBody MyModel1 model) {
        return model;
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Get MyModel1 by ID", response = MyModel1.class)
    public MyModel1 read(@PathVariable Long id) {
        // Dummy implementation
        return new MyModel1();
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Update MyModel1 by ID", response = MyModel1.class)
    public MyModel1 update(@PathVariable Long id, @RequestBody MyModel1 model) {
        // Dummy implementation
        return model;
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete MyModel1 by ID")
    public void delete(@PathVariable Long id) {
        // Dummy implementation
    }

}
