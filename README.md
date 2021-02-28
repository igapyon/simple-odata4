# simple-odata4
Simple な OData v4 のサンプル

```sh
http://localhost:8080/simple.svc/
http://localhost:8080/simple.svc/$metadata
http://localhost:8080/simple.svc/MyProducts?$orderby=ID&$top=20
```

## TODO

- TODO 検索条件をパラメータクエリに変更すること. (インジェクション対策)
- TODO 検索条件パラメータクエリ化には、SQL組み立ての何かしらデザパタの導入が必要.

## 制限

- 当面 `$search` はサポート外。

