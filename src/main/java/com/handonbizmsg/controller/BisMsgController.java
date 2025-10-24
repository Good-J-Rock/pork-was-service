package com.handonbizmsg.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BisMsgController {
    @RequestMapping(value = "")
    public String index() {
        return "Hello World";
    }
}
