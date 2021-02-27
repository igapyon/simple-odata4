package jp.igapyon.simpleodata4.entity;

import java.lang.reflect.Member;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.queryoption.OrderByItem;
import org.apache.olingo.server.api.uri.queryoption.SelectItem;
import org.apache.olingo.server.core.uri.queryoption.SelectItemImpl;
import org.apache.olingo.server.core.uri.queryoption.expression.MemberImpl;

/**
 * 実際に返却するデータ本体を組み上げるクラス.
 */
public class SimpleEntityDataBuilder {
    private SimpleEntityDataBuilder() {
    }

    /**
     * 指定のEDM要素セットに対応する要素コレクションを作成.
     * 
     * @param edmEntitySet EDM要素セット.
     * @return 要素コレクション.
     */
    public static EntityCollection buildData(EdmEntitySet edmEntitySet, UriInfo uriInfo) {
        // インメモリ作業データベースに接続.
        Connection conn = SimpleEntityDataH2.getH2Connection();

        // テーブルをセットアップ.
        SimpleEntityDataH2.setupTable(conn);

        // テーブルデータをセットアップ.
        // サンプルデータ.
        SimpleEntityDataH2.setupTableData(conn);

        EntityCollection eCollection = new EntityCollection();

        if (!SimpleEdmProvider.ES_MYPRODUCTS_NAME.equals(edmEntitySet.getName())) {
            // 処理対象外の要素セットです. 処理せずに戻します.
            return eCollection;
        }

        String sql = "SELECT ";

        if (uriInfo.getSelectOption() == null) {
            sql += "*";
        } else {
            int itemCount = 0;
            for (SelectItem item : uriInfo.getSelectOption().getSelectItems()) {
                // TODO STAR未対応.
                for (UriResource res : item.getResourcePath().getUriResourceParts()) {
                    sql += (itemCount++ == 0 ? "" : ",");
                    sql += ("[" + res.toString() + "]");
                }
            }
        }

        sql += " FROM Products";

        // TODO NOT IMPLEMENTED.
        // if (uriInfo.getCountOption() != null) {
        // }

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
        System.err.println("TRACE:sql:" + sql);
        try (var stmt = conn.prepareStatement(sql)) {
            stmt.executeQuery();
            var rset = stmt.getResultSet();
            for (; rset.next();) {
                final Entity ent = new Entity();
                ResultSetMetaData rsmeta = rset.getMetaData();
                for (int index = 0; index < rsmeta.getColumnCount(); index++) {
                    switch (rsmeta.getColumnType(index + 1)) {
                        case Types.BIGINT:
                        case Types.INTEGER:
                        case Types.SMALLINT:
                            ent.addProperty( //
                                    new Property(null, rsmeta.getColumnName(index + 1), ValueType.PRIMITIVE, //
                                            rset.getInt(index + 1)));
                            break;
                        default:
                            ent.addProperty( //
                                    new Property(null, rsmeta.getColumnName(index + 1), ValueType.PRIMITIVE, //
                                            rset.getString(index + 1)));
                            break;
                    }

                }
                eCollection.getEntities().add(ent);
            }
        } catch (SQLException ex) {
            throw new IllegalArgumentException("検索失敗:" + ex.toString(), ex);
        }

        try {
            conn.close();
        } catch (SQLException ex) {
            throw new IllegalArgumentException("検索失敗:" + ex.toString(), ex);
        }

        return eCollection;
    }

    /**
     * 与えられた情報をもとにURIを作成.
     * 
     * @param entitySetName 要素セット名.
     * @param id            ユニーク性を実現するId.
     * @return 要素セット名およびユニーク性を実現するIdをもとにつくられた部分的なURI.
     */
    public static URI createId(String entitySetName, Object id) {
        try {
            return new URI(entitySetName + "(" + String.valueOf(id) + ")");
        } catch (URISyntaxException ex) {
            throw new ODataRuntimeException("Fail to create ID EntitySet name: " + entitySetName, ex);
        }
    }
}
