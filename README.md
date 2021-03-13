# simple-odata4

Simple OData v4 server usage sample. (with Apache Olingo / Spring Boot / h2 database)

# Try to run simple sample

## Spring Boot Web Server

```sh
mvn clean install spring-boot:run
```

## Run query

### $metadata

```sh
http://localhost:8080/simple.svc/$metadata
```

### $orderby

```sh
http://localhost:8080/simple.svc/MyProducts?$orderby=ID&$top=20
```

### $filter

```sh
http://localhost:8080/simple.svc/MyProducts?$top=2001&$filter=Description eq 'MacBook Pro (13-inch, 2020, Thunderbolt 3ポートx 4)' and ID eq 1.0&$count=true&$select=ID,Name
```

### $search

```sh
http://localhost:8080/simple.svc/MyProducts?$top=6&$search=macbook&$count=true&$select=ID
```

# 中身を理解するために役立つ情報源

## 最も大切な OData v4 server チュートリアル

- https://olingo.apache.org/doc/odata4/index.html

## 参考: 別バージョンながら役立つ OData 2情報

- https://www.odata.org/documentation/odata-version-2-0/uri-conventions/

## 参考: h2機能を調べる際に

- http://www.h2database.com/html/functions.html

# TODO

- 文字列の長さ対応。
- TODO Null の対応。
- TODO 実行時エラーを調整すること。現在 IllegalArgumentExceptionでそのまま500になったうえにエラー内容が見えてしまう。
- 日時型、日付型の対応実装。
- 認証の実験。
- 実験的に全文検索である `$search` をサポートしたものの、もう少し詳しいところが調べられていない。また全文検索で有効なのはアルファベットのみ。
