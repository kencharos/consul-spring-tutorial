---
marp: true
theme: base
size: 16:9
page_number: true
paginate: true
style: |
    h1, h2, h3, h4, h5, header, footer {
        color: #404040;
    }
    section {
        justify-content: start;
        background-color: white;
        font-family: "Hiragino Kaku Gothic Pro", sans-serif;
        color: #404040;
        padding-top: 50px;
    }
    section.chapter {
        justify-content: center;
        font-size: 2em;
    }
    em {
        color: #07B53B;
        font-style: normal;
        font-weight: bold;
    }
    ul {
        list-style: none; 
        padding-left: 1em;
    }
    ul li::before {
        content: "・";
        color: #07B53B;
        font-weight: bold; 
        display: inline-block; 
        width: 1em;
        margin-left: -1em;
    }
    table th {
        background-color: #404040;
        color: white;
        font-weight: bold;
    }
    table td {
        background-color: #D9D9D9;
        color: black;
    }
    img[alt~="icon"] {
        display: inline-block;
        margin-left: 2em;
    }
    pre {
        font-size: 0.7em;
    }
---

# *Consul/Spring Cloud Consul 入門*

2020/3/24
Kentaro Maeda @ LINE

#JSUG

---

# About Me

+ Kentaro Maeda (前多賢太郎)
+ Twitter @kencharos 
+ LINE株式会社 フィナンシャル開発センター 
  サーバーサイドエンジニア  


![icon](images/icon.jpg)

---

# 今日の内容

+ Consul 入門
+ Spring Cloud Consul 紹介

---

# Spring Cloud Consul

https://spring.io/projects/spring-cloud-consul より、

Spring Cloud Consul は HashiCorp Consul と連携して、Spring Boot に大規模分散システムを構築するための機能として Service Discovery, Distributed Configuration, Control Bus を提供する。

Spring Cloud Consul で提供される機能は Consul の機能であり、
Spring Cloud Consul を活用するには、Consul の理解が欠かせない。


---

# ![h:60px](images/consul.png) HashiCorp Consul について 

https://www.consul.io
分散システム上のサービス管理を行うソフトウェア

+ Service Discovery - どのノードに何のサービスがあるかカタログ化
+ Health Check      - ノード・サービスの死活監視と通知
+ Key/Value Storage  - サービス横断で設定を参照・更新する
+ Multi Datacenter  - 別のDCにあるConsulクラスタと接続・同期する
+ Service Mesh - サービス間のセキュアな通信を管理する

---

# Consul の特徴

+ Go言語製のシングルバイナリと設定ファイルのみで動く
    + DBなどは不要。インストールとノードの追加が楽
+ Server/Agent によるクラスタ構成
    + Agent は自ノードの情報をServerに伝え、ヘルスチェックを行う
    + Server はAgentの情報を収集・同期する
    + Server/Agentはどちらも同じバイナリで設定内容が異なる
+ 高可用性
    + Server の過半数が生存していればクラスタを維持できる
    + Agentの追加が容易であり、大規模なクラスタを構築しやすい
+ WebAPI, CLI, DNS, UI など複数のインタフェースがある

---
# Consul クラスタの構成

図

---

# Service の登録

Consul へのサービス登録・削除は自分でAPI経由で行う。自動収集はしない。

+ 例) service1 のアドレス、HTTPヘルスチェックを設定して登録
    
```json
{ // service1.json
  "ID": "service1", // consul 全体でユニークなID
  "Name": "service1", // 同じ種類のサービスをグルーピングするサービス名
  "Address": "127.0.0.1",
  "Port": 8080,
  "Check": {
    "DeregisterCriticalServiceAfter": "60m",
    "HTTP": "http://127.0.0.1:8080/actuator/health",
    "Interval": "5s"
  }
}
```

```bash
# 同一ノードのagentに対してAPI呼び出し
curl -X PUT http://localhost:8500/v1/agent/service/register -d @service1.json
```

---

# サービス情報の取得
登録したサービスはAPI, CLI, DNS経由で参照できる。
別ノードのAgentで登録されたサービスの情報も参照できるので、ノードやサービスが増えても常にローカルAgentに問い合わせれば良い。

```
#DNS
$ dig @127.0.0.1 -p 8600 service1.service.dc1.consul. SRV
;; ANSWER SECTION:
service1.service.dc1.consul. 0  IN  SRV  1 1 8080 myhost.node.dc1.consul.
;; ADDITIONAL SECTION:
myhost.node.dc1.consul. 0 IN A    127.0.0.1
myhost.node.dc1.consul. 0 IN TXT  "consul-network-segment="
```
```json
//
$ curl http://localhost:8500/v1/health/service/service1
[
    {
        "Node": { "ID": "xxx", "Node": "myhost", "Address": "127.0.0.1",},
        "Service": { "ID": "service1", "Service": "service1","Address":"127.0.0.1", "Port": 8080 },
        "Checks": [//]
    }
]
```


---

# Watch/Event

---

# Key/Value Storage



---

# Consul のユースケース

---

# Consul Connect (Service Mesh)

---

# Spring Cloud Consul

