package jp.igapyon.simpleodata4.entity;

import java.lang.reflect.Member;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.queryoption.OrderByItem;
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

        String sql = "SELECT ID, Name, Description FROM Products";

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
                MemberImpl member = (MemberImpl) orderByItem.getExpression();
                // 前後の鉤括弧を除去。
                sql += member.toString().replace("[", "").replace("]", "");
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
                final Entity ent = new Entity() //
                        .addProperty( //
                                new Property(null, SimpleEdmProvider.FIELDS[0], ValueType.PRIMITIVE, //
                                        rset.getInt(1)))
                        .addProperty( //
                                new Property(null, SimpleEdmProvider.FIELDS[1], ValueType.PRIMITIVE, //
                                        rset.getString(2)))
                        .addProperty( //
                                new Property(null, SimpleEdmProvider.FIELDS[2], ValueType.PRIMITIVE, //
                                        rset.getString(3)));
                ent.setId(createId(SimpleEdmProvider.ES_MYPRODUCTS_NAME, rset.getInt(1)));
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
