# oiyokan

Oiyokan is a simple OData v4 Server. (based on Apache Olingo / Spring Boot / h2 database)

# Try to run oiyokan

## Spring Boot Web Server

```sh
mvn clean install spring-boot:run
```

## Run query

### $metadata

```sh
http://localhost:8080/odata4.svc/$metadata
```

### $orderby

```sh
http://localhost:8080/odata4.svc/MyProducts?$orderby=ID&$top=20&$count=true
```

### $filter

```sh
http://localhost:8080/odata4.svc/MyProducts?$top=2001&$filter=Description eq 'MacBook Pro (13-inch, 2020, Thunderbolt 3ポートx 4)' and ID eq 1.0&$count=true&$select=ID,Name
```

### $search

```sh
http://localhost:8080/odata4.svc/MyProducts?$top=6&$search=macbook&$count=true&$select=ID
```

### root

```sh
http://localhost:8080/odata4.svc/
```

### internal version

```sh
http://localhost:8080/odata4.svc/ODataAppInfos
```

# 中身を理解するために役立つ情報源

## 最も大切な OData v4 server チュートリアル

- https://olingo.apache.org/doc/odata4/index.html

## 参考: 別バージョンながら役立つ OData 2情報

- https://www.odata.org/documentation/odata-version-2-0/uri-conventions/

## 参考: h2機能を調べる際に

- http://www.h2database.com/html/functions.html

# 作業メモ

## 更新内容

- ODataの自動テストを組み込んだ。

## TODO

- Container や Entity のローカル情報体を観察していると、これは CsdlEntitySet などとほぼ一致することに気がつき始めた。extends でカスタムクラスを作成する方向性を考察開始。
- IDについてPrimaryKeyで動作するようにする。データベース項目名も可変にしたい。ただし当面は単一項目でユニークと期待。
- PreparedStatementの入力の型対応に先立ち、引数の型バリエーションを追加。特に日付・日時絡みは調整が必要な見込み。
- PreparedStatementの入力の型対応の追加.
- 実行時エラーを調整すること。現在 IllegalArgumentExceptionでそのまま500になったうえにエラー内容が見えてしまう。ODataApplicationException に対応することが第一案.
- 対応しない命令の場合、適切に例外で異常停止。ODataApplicationExceptionの利用を想定。
- 認証の実験。
- 実験的に全文検索である `$search` をサポートしたものの、もう少し詳しいところが調べられていない。また全文検索で有効なのはアルファベットのみ。h2 database でここを深掘りしても不毛か?
- ($search対応の後続となるため、しばらく対応できない) TODO Null の対応。
