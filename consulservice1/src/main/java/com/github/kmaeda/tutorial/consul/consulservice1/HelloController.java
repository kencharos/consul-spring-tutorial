package com.github.kmaeda.tutorial.consul.consulservice1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    private final ServiceBClient client;

    public HelloController(ServiceBClient client) {
        this.client = client;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Servicea -> " + client.hello();
    }
}
