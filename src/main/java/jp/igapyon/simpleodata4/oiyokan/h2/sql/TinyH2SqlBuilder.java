package jp.igapyon.simpleodata4.oiyokan.h2.sql;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlPropertyRef;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.queryoption.OrderByItem;
import org.apache.olingo.server.api.uri.queryoption.SelectItem;
import org.apache.olingo.server.core.uri.queryoption.FilterOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.expression.MemberImpl;

import jp.igapyon.simpleodata4.oiyokan.OiyokanCsdlEntitySet;

/**
 * SQL文を構築するための簡易クラス.
 */
public class TinyH2SqlBuilder {
    /**
     * SQL構築のデータ構造.
     */
    private final TinySqlBuildInfo sqlInfo = new TinySqlBuildInfo();

    /**
     * SQL構築のデータ構造を取得.
     * 
     * @return SQL構築のデータ構造.
     */
    public TinySqlBuildInfo getSqlInfo() {
        return sqlInfo;
    }

    /**
     * 件数カウント用のSQLを生成.
     * 
     * @param uriInfo URI情報.
     */
    public void getSelectCountQuery(UriInfo uriInfo) {
        sqlInfo.getSqlBuilder().append("SELECT COUNT(*) FROM " + sqlInfo.getEntitySet().getDbTableNameIyo());
        if (uriInfo.getFilterOption() != null) {
            FilterOptionImpl filterOpt = (FilterOptionImpl) uriInfo.getFilterOption();
            sqlInfo.getSqlBuilder().append(" WHERE ");
            new TinyH2SqlExprExpander(sqlInfo).expand(filterOpt.getExpression());
        }
    }

    /**
     * 検索用のSQLを生成.
     * 
     * @param uriInfo URI情報.
     */
    public void getSelectQuery(UriInfo uriInfo) {
        sqlInfo.getSqlBuilder().append("SELECT ");

        if (uriInfo.getSelectOption() == null) {
            // アスタリスクは利用せず、項目を指定する。
            OiyokanCsdlEntitySet entitySet = (OiyokanCsdlEntitySet) sqlInfo.getEntitySet();
            CsdlEntityType entityType = entitySet.getEntityType();
            String strColumns = "";
            for (CsdlProperty prop : entityType.getProperties()) {
                if (strColumns.length() > 0) {
                    strColumns += ",";
                }
                strColumns += prop.getName();
            }
            sqlInfo.getSqlBuilder().append(strColumns);
        } else {
            final OiyokanCsdlEntitySet iyoEntitySet = (OiyokanCsdlEntitySet) sqlInfo.getEntitySet();
            final List<String> keyTarget = new ArrayList<>();
            for (CsdlPropertyRef propRef : iyoEntitySet.getEntityType().getKey()) {
                keyTarget.add(propRef.getName());
            }
            int itemCount = 0;
            for (SelectItem item : uriInfo.getSelectOption().getSelectItems()) {
                for (UriResource res : item.getResourcePath().getUriResourceParts()) {
                    sqlInfo.getSqlBuilder().append(itemCount++ == 0 ? "" : ",");
                    sqlInfo.getSqlBuilder().append("[" + res.toString() + "]");
                    for (int index = 0; index < keyTarget.size(); index++) {
                        if (keyTarget.get(index).equals(res.toString())) {
                            keyTarget.remove(index);
                            break;
                        }
                    }
                }
            }
            for (int index = 0; index < keyTarget.size(); index++) {
                // レコードを一位に表すID項目が必須。検索対象にない場合は追加.
                sqlInfo.getSqlBuilder().append(itemCount++ == 0 ? "" : ",");
                sqlInfo.getSqlBuilder().append("[" + keyTarget.get(index) + "]");
            }
        }

        // 取得元のテーブル.
        sqlInfo.getSqlBuilder().append(" FROM " + sqlInfo.getEntitySet().getDbTableNameIyo());

        // uriInfo.getCountOption は明示的には記載しない.
        // 現状の実装では指定があろうがなかろうが件数はカウントする実装となっている.

        if (uriInfo.getFilterOption() != null) {
            FilterOptionImpl filterOpt = (FilterOptionImpl) uriInfo.getFilterOption();
            // WHERE部分についてはパラメータクエリで処理するのを基本とする.
            sqlInfo.getSqlBuilder().append(" WHERE ");
            new TinyH2SqlExprExpander(sqlInfo).expand(filterOpt.getExpression());
        }

        if (uriInfo.getOrderByOption() != null) {
            List<OrderByItem> orderByItemList = uriInfo.getOrderByOption().getOrders();
            for (int index = 0; index < orderByItemList.size(); index++) {
                OrderByItem orderByItem = orderByItemList.get(index);
                if (index == 0) {
                    sqlInfo.getSqlBuilder().append(" ORDER BY ");
                } else {
                    sqlInfo.getSqlBuilder().append(",");
                }

                // 項目名を SQL Serverクオート付きで指定.
                // SQL Server 互換モードで h2 を動作させているから可能になる指定方法.
                sqlInfo.getSqlBuilder().append((MemberImpl) orderByItem.getExpression()).toString();

                if (orderByItem.isDescending()) {
                    sqlInfo.getSqlBuilder().append(" DESC");
                }
            }
        }

        if (uriInfo.getTopOption() != null) {
            sqlInfo.getSqlBuilder().append(" LIMIT ");
            sqlInfo.getSqlBuilder().append(uriInfo.getTopOption().getValue());
        }

        if (uriInfo.getSkipOption() != null) {
            sqlInfo.getSqlBuilder().append(" OFFSET ");
            sqlInfo.getSqlBuilder().append(uriInfo.getSkipOption().getValue());
        }
    }
}
