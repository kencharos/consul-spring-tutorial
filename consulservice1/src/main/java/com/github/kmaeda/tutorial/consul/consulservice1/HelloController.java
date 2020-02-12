package com.github.kmaeda.tutorial.consul.consulservice1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    private final Service2Client client;

    public HelloController(Service2Client client) {
        this.client = client;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Service1 -> " + client.hello();
    }
}
