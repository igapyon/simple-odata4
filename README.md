# simple-odata4

Simple OData v4 server usage sample. (with h2 database)


# How to run server odata

動かし方

```sh
http://localhost:8080/simple.svc/
http://localhost:8080/simple.svc/$metadata
http://localhost:8080/simple.svc/MyProducts?$orderby=ID&$top=20
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

