package com.handonbizmsg.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BisMsgController {
    @GetMapping(value = "/bizmsg")
    public String index() {
        return "Hello World";
    }
}
