package com.github.kmaeda.consul.consulservice2;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloBController {

    @Value("${greeting}") String greeting;

    @GetMapping(value = "/hello", produces = "text/plain")
    public String hellob() {
        return greeting;
    }

}
