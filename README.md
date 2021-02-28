# simple-odata4
Simple な OData v4 のサンプル

```sh
http://localhost:8080/simple.svc/
http://localhost:8080/simple.svc/$metadata
http://localhost:8080/simple.svc/MyProducts?$orderby=ID&$top=20
```

## 参考 OData 2

- https://www.odata.org/documentation/odata-version-2-0/uri-conventions/
- http://www.h2database.com/html/functions.html

## TODO

- TODO 検索条件をパラメータクエリに変更すること. (インジェクション対策)
- TODO 検索条件パラメータクエリ化には、SQL組み立ての何かしらデザパタの導入が必要.

## 制限

- 当面 `$search` はサポート外。

