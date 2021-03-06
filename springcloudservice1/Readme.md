 # spring cloud consul

## setup
 
setup kvs
 
```
consul kv put config/application/greeting/common service
consul kv put config/springcloudservice2/greeting/service2 B-1
consul kv put config/springcloudservice2,beta/greeting/service2 B-2
consul kv put config/springcloudservice2,beta/server/port 8092
```

## case1. ServiceDiscovery and DistributedConfig.

run 
+ springcloudservice1(8090)
+ springcloudservice2(8091)
+ springcloudservice2 with profile beta (8092)
  + beta properties fetch from consul.
  
access

+ http://localhost:8090/hello
+ http://localhost:8090/service2

down springcloudservice2 of one.

change kv for refresh scope test.

```
consul kv put config/application/greeting/common service_update
```

## case 2, DiscoveryClient and SpringCloudGateway

https://cloud.spring.io/spring-cloud-gateway/reference/html/#the-discoveryclient-route-definition-locator

using DiscoveryClient Route Definition Locator.

run springcloudgateway app.

access

http://<spring cloud gateawy adderss>/<consul service name>/<target service endpoint>

+ http://localhost:8099/springcloudservice1/hello
+ http://localhost:8099/springcloudservice2/hello