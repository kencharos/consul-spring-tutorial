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
// API呼び出し。レスポンスは一部省略
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

# Key/Value Storage

consul クラスタ間で設定の登録・参照を行う
登録された値はクラスタ内で共有

```bash
# key1=value1 で登録
$ curl -X PUT http://localhost:8500/v1/kv/key1 -d "value1"
# common/key2=value2 で登録
$ curl -X PUT http://localhost:8500/v1/kv/common/key2 -d "value2"
```

```bash
# key1をJSONで取得。valueは base64 エンコード
$ curl http://localhost:8500/v1/kv/key1
[{ 
　　"Key": "key1", "Value": "dmFsdWUx","LockIndex": 0, 
　　"Flags": 0, "CreateIndex": 22,"ModifyIndex": 22
}]
# common/key2 を RAW形式で取得
curl http://localhost:8500/v1/common/key2
value2
```

---

# Watch/Event

サービスの状態の変化やKey/Valueの変化、ヘルスチェック、任意のイベントについて通知を受け取ることができる。

```bash
# service1 サービスの状態を監視し、変化したら hoge.sh を実行する
consul watch -type service -service "service1" ./hoge.sh
```

``` bash
# 任意のイベント発行
consul event -name=test value1
# イベント通知が来たら cat で内容を出力
consul watch -type=event -name=test cat
```

Web APIの場合、Blocking Query というロングポーリングの手段がある
```bash
# 特定の時点(index=83)以降 key1の値が変化するまで10秒待つ
$ curl http://localhost:8500/v1/kv/key1?index=83&wait=10s
```

---

# Consul のユースケース

Consul はクラスタ内のサービスの状態管理とKey/Valueの値の保持に専念
これを使って何をするかは実装者次第。
Consul Connect, Spring Cloud Consul はサービス間通信をConsulで行う

+ 例1) サービス情報を使用して、動的な構成変更に追従するサービス間通信を行う
    + Consul Connect, Spring Cloud Consul
+ 例2) Key/Value Storage でクラスタ全体で共有する設定を一元管理する
    + Spring Cloud Consul
+ 例3) 負荷分散対象のサービスをwatchで監視して、変化があればロードバランサーの設定を変更して再起動
+ 例4) Multi Datacenterで クラウド/オンプレミス/k8s を横断するサービス一覧を作る

---

# Consul Connect (Service Mesh)

Consul に標準で組み込まれている、サービス間通信を管理する機能

+ L4プロキシ か L7プロキシ(envoy) をサービスごとにサイドカーとして起動し、サービス間通信で使用するローカルポートを提供
    + サービス間、負荷分散、死活監視はConsul側で提供
    + 通信相手のサービスがどのノードにあってもローカルポートに繋げばConsulが解決する
+ 通信経路は mTLSで暗号化、Consul クラスタ外からのアクセスは不可
+ Consul APIで通信許可を自由に設定できる
+ L7 Traffic Management, Mesh Gateway など機能強化により、高度なルーティングや複数DC間での通信もできるようになった

---

# Consul Connect の設定

サービス登録時にサイドカープロキシの設定で通信先とポートの設定を行う

```
{ "ID": "service1",  "Name": "service1",
  "Address": "127.0.0.1",  "Port": 8080,
  "Connect": {
    "SidecarService":{
      "Proxy":{ "upstreams": [{ // service2に 9000ポートで接続したい!
          "destination_name": "service2", "local_bind_port": 9000 }]}}}
```
サイドカープロキシをサービスごとに起動する
```
consul connect proxy -sidecar-for service1 
```
アプリケーションは service2呼び出しを locahost:9000 経由で行う
```java
@FeignClient(name = "service2", url = "${service2.url}") //設定ファイル or 環境変数
public interface Service2Client {
    @GetMapping(value="/hello", produces = "text/plain") String hello();
}
```


---

# Demo - Consul, Connsul Connect

+ 手動でSpring Bootサービスの登録、Connectによる通信
+ https://github.com/kencharos/consul-spring-tutorial/blob/master/consulservice1/tutorial_consul_basic/readme.md
+ ローカルで試すには `consul agent --dev` で全設定有効のServer兼Agentが起動

TODO 図

---

# Consul のまとめ

+ 複数ネットワークを横断してサービス情報を集約・管理するソフトウェア
    + どう使うは自分次第
+ 小規模でも大規模でも同じように使える
+ 全ての機能にAPIがあり、API設計が綺麗
+ クラウドでもオンプレでも、一度導入してみても損はない

---

# Spring Cloud Consul

