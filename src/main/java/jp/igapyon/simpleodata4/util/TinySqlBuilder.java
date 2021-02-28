package jp.igapyon.simpleodata4.util;

import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.core.uri.queryoption.FilterOptionImpl;

public class TinySqlBuilder {
    /**
     * 件数カウント用のSQLを生成.
     * 
     * @param uriInfo URI情報.
     * @return SQL文.
     */
    public String getSelectCount(UriInfo uriInfo) {
        String sql = "SELECT COUNT(*) FROM MyProducts";
        if (uriInfo.getFilterOption() != null) {
            FilterOptionImpl filterOpt = (FilterOptionImpl) uriInfo.getFilterOption();
            sql += " WHERE " + ExprSqlUtil.expand(filterOpt.getExpression());
        }

        return sql;
    }

}
