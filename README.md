# simple-odata4
Simple な OData v4 のサンプル

```sh
http://localhost:8080/simple.svc/
http://localhost:8080/simple.svc/$metadata
http://localhost:8080/simple.svc/MyProducts?$orderby=ID&$top=20
```

## 参考 OData 2ベース機能 + h2 機能

- https://www.odata.org/documentation/odata-version-2-0/uri-conventions/
- http://www.h2database.com/html/functions.html

## TODO

- TODO 実行時エラーを調整すること。現在 IllegalArgumentExceptionでそのまま500になったうえにエラー内容が見えてしまう。
- 日時型、日付型の対応実装。
- 認証の実験。
- 当面 `$search` はサポートしない。ただしサポートは魅力的ではある。サポートすることにより効果的な全文検索が実現できる。
