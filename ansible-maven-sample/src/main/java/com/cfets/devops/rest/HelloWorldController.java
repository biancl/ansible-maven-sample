package com.cfets.devops.rest;

import org.springframework.web.bind.annotation.*;

@RestController
public class HelloWorldController {
    
    @RequestMapping(value="helloWorld/{name}")
    public String helloWorld(@PathVariable String name){
        return name;
    }
}