package com.destinylight.tools.mock.demo.swagger.controller;

import com.destinylight.tools.mock.demo.swagger.model.MyModel0;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "MyController0")
@RequestMapping("/api/mycontroller0")
public class MyController0 {

    @PostMapping
    @ApiOperation(value = "Create a new MyModel0", response = MyModel0.class)
    public MyModel0 create(@RequestBody MyModel0 model) {
        return model;
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Get MyModel0 by ID", response = MyModel0.class)
    public MyModel0 read(@PathVariable Long id) {
        // Dummy implementation
        return new MyModel0();
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Update MyModel0 by ID", response = MyModel0.class)
    public MyModel0 update(@PathVariable Long id, @RequestBody MyModel0 model) {
        // Dummy implementation
        return model;
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete MyModel0 by ID")
    public void delete(@PathVariable Long id) {
        // Dummy implementation
    }

}
