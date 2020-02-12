package com.github.kmaeda.tutorial.consul.consulservice1;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "service2", url = "${service2.url}")
public interface Service2Client {
    @GetMapping(value="/hello", produces = "text/plain") String hello();
}
