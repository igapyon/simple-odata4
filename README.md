# simple-odata4

Simple OData v4 server usage sample. (with Apache Olingo / Spring Boot / h2 database)

# Try to run simple sample

## Spring Boot Web Server

```sh
simple-odata4 % mvn clean install spring-boot:run
```

## Run query

```sh
- http://localhost:8080/simple.svc/$metadata
- http://localhost:8080/simple.svc/MyProducts?$orderby=ID&$top=20
- http://localhost:8080/simple.svc/MyProducts?$top=10&$count=true&$select=Description,ID,Name
- http://localhost:8080/simple.svc/MyProducts?$top=2001&$filter=Description eq 'MacBook Pro (13-inch, 2020, Thunderbolt 3ポートx 4)' and ID eq 1.0&$count=true&$select=ID,Name
- http://localhost:8080/simple.svc/MyProducts?$top=5&$orderby=Description&$count=true&$select=Description,ID
- http://localhost:8080/simple.svc/MyProducts?$top=6&$search=macbook&$count=true&$select=ID
```

# Refs

## 最も大切なチュートリアル

- https://olingo.apache.org/doc/odata4/index.html

## 参考 OData 2ベース機能 + h2 機能

- https://www.odata.org/documentation/odata-version-2-0/uri-conventions/
- http://www.h2database.com/html/functions.html

# TODO

- TODO 実行時エラーを調整すること。現在 IllegalArgumentExceptionでそのまま500になったうえにエラー内容が見えてしまう。
- 日時型、日付型の対応実装。
- 認証の実験。
- 実験的に全文検索である `$search` をサポートしたものの、もう少し詳しいところが調べられていない。また全文検索で有効なのはアルファベットのみ。

