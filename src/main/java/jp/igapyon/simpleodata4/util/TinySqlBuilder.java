package jp.igapyon.simpleodata4.util;

import java.util.List;

import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.queryoption.OrderByItem;
import org.apache.olingo.server.api.uri.queryoption.SelectItem;
import org.apache.olingo.server.core.uri.queryoption.FilterOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.expression.MemberImpl;

public class TinySqlBuilder {
    /**
     * 件数カウント用のSQLを生成.
     * 
     * @param uriInfo URI情報.
     * @return SQL文.
     */
    public String getSelectCountQuery(UriInfo uriInfo) {
        String sql = "SELECT COUNT(*) FROM MyProducts";
        if (uriInfo.getFilterOption() != null) {
            FilterOptionImpl filterOpt = (FilterOptionImpl) uriInfo.getFilterOption();
            sql += " WHERE " + ExprSqlUtil.expand(filterOpt.getExpression());
        }

        return sql;
    }

    public String getSelectQuery(UriInfo uriInfo) {
        String sql = "SELECT ";

        if (uriInfo.getSelectOption() == null) {
            sql += "*";
        } else {
            boolean isIDExists = false;
            int itemCount = 0;
            for (SelectItem item : uriInfo.getSelectOption().getSelectItems()) {
                // TODO STAR未対応.
                for (UriResource res : item.getResourcePath().getUriResourceParts()) {
                    sql += (itemCount++ == 0 ? "" : ",");
                    sql += ("[" + res.toString() + "]");
                    if (res.toString().equals("ID")) {
                        isIDExists = true;
                    }
                }
            }
            if (!isIDExists) {
                sql += (itemCount++ == 0 ? "" : ",");
                sql += ("[ID]");
            }
        }

        sql += " FROM MyProducts";

        // TODO NOT IMPLEMENTED.
        // if (uriInfo.getCountOption() != null) {
        // }

        if (uriInfo.getFilterOption() != null) {
            FilterOptionImpl filterOpt = (FilterOptionImpl) uriInfo.getFilterOption();
            sql += " WHERE " + ExprSqlUtil.expand(filterOpt.getExpression());
        }

        if (uriInfo.getOrderByOption() != null) {
            List<OrderByItem> orderByItemList = uriInfo.getOrderByOption().getOrders();
            for (int index = 0; index < orderByItemList.size(); index++) {
                OrderByItem orderByItem = orderByItemList.get(index);
                if (index == 0) {
                    sql += " ORDER BY ";
                } else {
                    sql += ",";
                }

                // 項目名を SQL Serverクオート付きで指定.
                // SQL Server 互換モードで h2 を動作させているから可能になる指定方法.
                sql += ((MemberImpl) orderByItem.getExpression()).toString();

                if (orderByItem.isDescending()) {
                    sql += " DESC";
                }
            }
        }

        if (uriInfo.getTopOption() != null) {
            sql += " LIMIT " + uriInfo.getTopOption().getValue();
        }

        if (uriInfo.getSkipOption() != null) {
            sql += " OFFSET " + uriInfo.getSkipOption().getValue();
        }
        
        return sql;
    }
}
