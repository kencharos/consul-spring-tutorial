package com.github.kmaeda.springcloud.springcloudservice1;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@LoadBalancerClient // use @LoadBalanced in @Bean method, e.g, RestTemplate/WebClient
@FeignClient("springcloudservice2") // set consul service name.
public interface Service2Client {
    @GetMapping(value = "/hello", produces = "text/plain")
    String hello2();
}
