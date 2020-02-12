# tutorial consul base service registration and interaction

## setup

+ consul in local machine. 
    + Notice, if consul run in docker, consul need host netowrk mode. it cannot run in Mac.
      so that, I recommend local install consul in Mac.
+ Java 1.8+
+ getenvoy (https://www.getenvoy.io/)

## run in consul

```
consul agent -dev -ui #connect?
```

### STEP1 - register one serviceA, two serviceB 

running services
+ consulservice1(8080)
+ consulservice2(8081),
+ consulservice2 with spring profile=BETA(8082)

register services to consul

```
curl -X PUT http://localhost:8500/v1/agent/service/register -d @basic_service1.json
curl -X PUT http://localhost:8500/v1/agent/service/register -d @basic_service2_1.json
curl -X PUT http://localhost:8500/v1/agent/service/register -d @basic_service2_2.json
```

(optional) register watch

watch.sh write stdin to /tmp/log.txt

```
consul watch -type service -service "service2" $(pwd)/watch.sh
```

then, shutdown service2, check/log.txt

delete services

```
consul services deregister --id=service1
consul services deregister --id=service2_1
consul services deregister --id=service2_2
```


### STEP2 - serviceA connect serviceB via consul connect

register services with con

```
curl -X PUT http://localhost:8500/v1/agent/service/register -d @connect_service1.json
curl -X PUT http://localhost:8500/v1/agent/service/register -d @connect_service2_1.json
curl -X PUT http://localhost:8500/v1/agent/service/register -d @connect_service2_2.json
```


start proxies.

if use consul connect native proxy.(this support L4 proxy, so that cannot load balancing per connection, TCP level.)

```
consul connect proxy -sidecar-for service1 &
consul connect proxy -sidecar-for service2_1 &
consul connect proxy -sidecar-for service2_2 &
```

if use envoy, execute this
(this support L4 proxy. And L7 proxy when L7 traffic management uses. L7 traffic management config is https://www.consul.io/docs/connect/l7-traffic-management.html )
(start-envoy.sh is warp shell for getenvoy.)

```
consul connect envoy -sidecar-for service1 -envoy-binary $(pwd)/start-envoy.sh &
consul connect envoy -sidecar-for service2_1 -admin-bind 0.0.0.0:19001 -envoy-binary $(pwd)/start-envoy.sh &
consul connect envoy -sidecar-for service2_2 -admin-bind 0.0.0.0:19002 -envoy-binary $(pwd)/start-envoy.sh &
```

start service1 with env SERVICE2_URL=http://localhost:9000　

then http://localhost:8080/hello .
TCP timeout reached or a part of serviceB down, connect other service.

stop all

```
consul services deregister --id=service1-sidecar-proxy
consul services deregister --id=service2_1-sidecar-proxy
consul services deregister --id=service2_2-sidecar-proxy
consul services deregister --id=service1
consul services deregister --id=service2_1
consul services deregister --id=service2_2
```