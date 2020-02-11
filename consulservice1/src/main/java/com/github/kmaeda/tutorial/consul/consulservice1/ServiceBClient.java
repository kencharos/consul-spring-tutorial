package com.github.kmaeda.tutorial.consul.consulservice1;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "serviceb", url = "${serviceb.url}")
public interface ServiceBClient {
    @GetMapping(value="/hello", produces = "text/plain") String hello();
}
