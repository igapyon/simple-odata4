package jp.igapyon.simpleodata4.entity;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.core.uri.queryoption.SearchOptionImpl;

import jp.igapyon.simpleodata4.util.TinySqlBuilder;

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

        if (uriInfo.getSearchOption() != null) {
            // $search はサポート外.
            SearchOptionImpl searchOpt = (SearchOptionImpl) uriInfo.getSearchOption();
            throw new ODataRuntimeException("NOT SUPPORTED:$search:" + searchOpt.toString());
        }

        {
            // 件数をカウントして設定。
            TinySqlBuilder tinySql = new TinySqlBuilder();
            tinySql.getSelectCountQuery(uriInfo);
            final String sql = tinySql.getSqlInfo().getSqlBuilder().toString();

            System.err.println("TRACE:SQL: " + sql);
            int countWithWhere = 0;
            try (var stmt = conn.prepareStatement(sql)) {
                for (Object look : tinySql.getSqlInfo().getSqlParamList()) {
                    System.err.println("DEBUG:param:" + look);
                }
    
                stmt.executeQuery();
                var rset = stmt.getResultSet();
                rset.next();
                countWithWhere = rset.getInt(1);
            } catch (SQLException ex) {
                throw new IllegalArgumentException("検索失敗:" + ex.toString(), ex);
            }
            eCollection.setCount(countWithWhere);
        }

        TinySqlBuilder tinySql = new TinySqlBuilder();
        tinySql.getSelectQuery(uriInfo);
        final String sql = tinySql.getSqlInfo().getSqlBuilder().toString();

        System.err.println("TRACE:SQL: " + sql);
        try (var stmt = conn.prepareStatement(sql)) {
            for (Object look : tinySql.getSqlInfo().getSqlParamList()) {
                System.err.println("DEBUG:param:" + look);
            }

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
                ent.setId(createId(SimpleEdmProvider.ES_MYPRODUCTS_NAME, rset.getInt("ID")));
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
