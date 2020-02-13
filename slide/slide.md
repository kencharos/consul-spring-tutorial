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
        padding-top: 40px;
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
        font-weight: bold; /* If you want it to be bold */
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

at JSUG

---

# About Me

+ Kentaro Maeda (前多賢太郎)
+ @kencharos (twitter)
+ サーバーサイドエンジニア  
at LINE株式会社 フィナンシャル開発センター 

![icon](images/icon.jpg)

