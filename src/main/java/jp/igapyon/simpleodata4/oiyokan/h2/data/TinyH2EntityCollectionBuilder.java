/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */
package jp.igapyon.simpleodata4.oiyokan.h2.data;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.server.api.uri.UriInfo;

import jp.igapyon.simpleodata4.oiyokan.OiyokanEdmProvider;
import jp.igapyon.simpleodata4.oiyokan.h2.sql.TinyH2SqlBuilder;

/**
 * 実際に返却するデータ本体を組み上げるクラス.
 * 
 * EDM要素セットを入力に実際のデータを組み上げ.
 */
public class TinyH2EntityCollectionBuilder {
    private TinyH2EntityCollectionBuilder() {
    }

    /**
     * 指定のEDM要素セットに対応する要素コレクションを作成.
     * 
     * @param edmEntitySet EDM要素セット.
     * @param uriInfo      SQL構築のデータ構造.
     * @return 要素コレクション.
     */
    public static EntityCollection build(EdmEntitySet edmEntitySet, UriInfo uriInfo) {
        final EntityCollection eCollection = new EntityCollection();

        OiyokanEdmProvider provider = new OiyokanEdmProvider();
        if (!edmEntitySet.getEntityContainer().getName().equals(provider.getEntityContainer().getName())) {
            // Container 名が不一致. 処理せずに戻します.
            return eCollection;
        }

        CsdlEntitySet eSetTarget = null;
        String targetEntityName = null;
        for (CsdlEntitySet eSetProvided : provider.getEntityContainer().getEntitySets()) {
            if (edmEntitySet.getName().equals(eSetProvided.getName())) {
                eSetTarget = eSetProvided;
                targetEntityName = eSetProvided.getName();
                break;
            }
        }

        if (targetEntityName == null) {
            // 処理対象外の要素セットです. 処理せずに戻します.
            return eCollection;
        }

        // インメモリ作業データベースに接続.
        Connection conn = TinyH2Util.getH2Connection();

        // テーブルをセットアップ.
        TinyH2DbSample.createTable(conn);

        // テーブルデータをセットアップ.
        // サンプルデータを格納.
        TinyH2DbSample.setupTableData(conn);

        if (uriInfo.getSearchOption() != null) {
            // $search.
            new TinyH2TrialFullTextSearch().process(conn, edmEntitySet, uriInfo, eCollection);
            return eCollection;
        }

        {
            // 件数をカウントして設定。
            TinyH2SqlBuilder tinySql = new TinyH2SqlBuilder();
            tinySql.getSqlInfo().setEntityName(targetEntityName);
            tinySql.getSelectCountQuery(uriInfo);
            final String sql = tinySql.getSqlInfo().getSqlBuilder().toString();

            System.err.println("OData v4: TRACE: SQL: " + sql);
            int countWithWhere = 0;
            try (var stmt = conn.prepareStatement(sql)) {
                int column = 1;
                for (Object look : tinySql.getSqlInfo().getSqlParamList()) {
                    bindPreparedParameter(look, stmt, column++);
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

        TinyH2SqlBuilder tinySql = new TinyH2SqlBuilder();
        tinySql.getSqlInfo().setEntityName(targetEntityName);

        tinySql.getSelectQuery(uriInfo);
        final String sql = tinySql.getSqlInfo().getSqlBuilder().toString();

        System.err.println("OData v4: TRACE: SQL: " + sql);
        try (var stmt = conn.prepareStatement(sql)) {
            int idxColumn = 1;
            for (Object look : tinySql.getSqlInfo().getSqlParamList()) {
                bindPreparedParameter(look, stmt, idxColumn++);
            }

            stmt.executeQuery();
            var rset = stmt.getResultSet();
            ResultSetMetaData rsmeta = null;
            for (; rset.next();) {
                if (rsmeta == null) {
                    rsmeta = rset.getMetaData();
                }
                final Entity ent = new Entity();
                for (int column = 1; column <= rsmeta.getColumnCount(); column++) {
                    Property prop = resultSet2Property(rset, rsmeta, column);
                    ent.addProperty(prop);
                }
                ent.setId(createId(eSetTarget.getName(), rset.getInt("ID")));
                eCollection.getEntities().add(ent);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IllegalArgumentException("検索失敗:" + ex.toString(), ex);
        }

        try {
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
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

    ///////////////////
    // Mapping

    private static Property resultSet2Property(ResultSet rset, ResultSetMetaData rsmeta, int column)
            throws SQLException {
        Property prop = null;
        final String columnName = rsmeta.getColumnName(column);
        switch (rsmeta.getColumnType(column)) {
        case Types.TINYINT:
            prop = new Property(null, columnName, ValueType.PRIMITIVE, rset.getByte(column));
            break;
        case Types.SMALLINT:
            prop = new Property(null, columnName, ValueType.PRIMITIVE, rset.getShort(column));
            break;
        case Types.INTEGER:
            prop = new Property(null, columnName, ValueType.PRIMITIVE, rset.getInt(column));
            break;
        case Types.BIGINT:
            prop = new Property(null, columnName, ValueType.PRIMITIVE, rset.getLong(column));
            break;
        case Types.DECIMAL:
            prop = new Property(null, columnName, ValueType.PRIMITIVE, rset.getBigDecimal(column));
            break;
        case Types.BOOLEAN:
            prop = new Property(null, columnName, ValueType.PRIMITIVE, rset.getBoolean(column));
            break;
        case Types.REAL:
            prop = new Property(null, columnName, ValueType.PRIMITIVE, rset.getFloat(column));
            break;
        case Types.DOUBLE:
            prop = new Property(null, columnName, ValueType.PRIMITIVE, rset.getDouble(column));
            break;
        case Types.DATE:
            prop = new Property(null, columnName, ValueType.PRIMITIVE, rset.getDate(column));
            break;
        case Types.TIMESTAMP:
            prop = new Property(null, columnName, ValueType.PRIMITIVE, rset.getTimestamp(column));
            break;
        case Types.TIME:
            prop = new Property(null, columnName, ValueType.PRIMITIVE, rset.getTime(column));
            break;
        case Types.CHAR:
        case Types.VARCHAR:
        default:
            prop = new Property(null, columnName, ValueType.PRIMITIVE, rset.getString(column));
            break;
        }
        return prop;
    }

    private static void bindPreparedParameter(Object look, PreparedStatement stmt, int column) throws SQLException {
        if (look instanceof Integer) {
            stmt.setInt(column, (Integer) look);
        } else {
            // TODO 他の型への対応を追加。
            stmt.setString(column, (String) look);
        }
    }
}
