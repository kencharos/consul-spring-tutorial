package com.github.kmaeda.springcloud.springcloudservice1;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableFeignClients
public class HelloController {

    @Autowired
    Service2Client client;

    @Autowired
    DiscoveryClient dicovery;

    @GetMapping("/hello")
    public String hello() {
        return "service1 ->" + client.hello2();
    }

    @GetMapping("/service2")
    public List<String> service2List() {
        return dicovery.getInstances("springcloudservice2")
                .stream().map(si -> si.getHost() +":"+si.getPort())
                .collect(Collectors.toList());
    }

}
