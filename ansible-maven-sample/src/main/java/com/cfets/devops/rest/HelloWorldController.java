package com.cfets.devops.rest;

import org.springframework.web.bind.annoation.PathVariable;
import org.springframework.web.bind.annoation.RequestMapping;
import org.springframework.web.bind.annoation.RestController;

@RestController
public Class HelloWorldController {
    
    @RequestMapping(value="helloWorld/{name}")
    public void helloWorld(@PathVariable String name){
        return name;
    }
}