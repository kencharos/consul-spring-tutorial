package com.github.kmaeda.springcloud.springcloudservice2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope // set refresh if reload kv params. @ConfigurationProperties is RefreshScoped default.
public class Hello2Controller {

    // fetch form consul kvs.
    @Value("${greeting.common}") String greetingPrefix;
    @Value("${greeting.service2}") String greeting;

    @GetMapping(value = "/hello", produces = "text/plain")
    public String greeting() {
        return greetingPrefix + greeting;
    }

}

