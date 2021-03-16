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
package jp.igapyon.simpleodata4.oiyokan.basic;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlPropertyRef;

import jp.igapyon.simpleodata4.oiyokan.OiyokanConstants;
import jp.igapyon.simpleodata4.oiyokan.OiyokanCsdlEntitySet;
import jp.igapyon.simpleodata4.oiyokan.h2.data.TinyH2DbSample;

/**
 * 典型的で基本的な JDBC処理を用いて EntityType を構築します。
 */
public class BasicJdbcEntityTypeBuilder {
    /**
     * 処理対象となる EntitySet.
     */
    private OiyokanCsdlEntitySet entitySet = null;

    public BasicJdbcEntityTypeBuilder(OiyokanCsdlEntitySet entitySet) {
        this.entitySet = entitySet;
        System.err.println( //
                "OData v4: EntityType: " + entitySet.getName() + " (Oiyokan: " + OiyokanConstants.VERSION + ")");
    }

    /**
     * EntityType を取得.
     *
     * @return 取得された EntityType.
     */
    public CsdlEntityType getEntityType() {
        // インメモリ作業データベースに接続.
        try (Connection conn = BasicDbUtil.getH2Connection()) {
            // テーブルをセットアップ.
            TinyH2DbSample.createTable(conn);

            // CSDL要素型として情報を組み上げ.
            CsdlEntityType entityType = new CsdlEntityType();
            entityType.setName(entitySet.getEntityNameIyo());

            // 基本的な動作: バッファ的な h2 データベースから該当情報を取得.
            final List<CsdlProperty> propertyList = new ArrayList<>();
            entityType.setProperties(propertyList);

            // SELECT * について、この箇所のみ記述を許容したい。
            try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " + entitySet.getDbTableNameIyo())) {
                ResultSetMetaData rsmeta = stmt.getMetaData();
                final int columnCount = rsmeta.getColumnCount();
                for (int column = 1; column <= columnCount; column++) {
                    final CsdlProperty prop = new CsdlProperty().setName(rsmeta.getColumnName(column));
                    propertyList.add(prop);
                    switch (rsmeta.getColumnType(column)) {
                    case Types.TINYINT:
                        prop.setType(EdmPrimitiveTypeKind.SByte.getFullQualifiedName());
                        break;
                    case Types.SMALLINT:
                        prop.setType(EdmPrimitiveTypeKind.Int16.getFullQualifiedName());
                        break;
                    case Types.INTEGER: /* INT */
                        prop.setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());
                        break;
                    case Types.BIGINT:
                        prop.setType(EdmPrimitiveTypeKind.Int64.getFullQualifiedName());
                        break;
                    case Types.DECIMAL:
                        prop.setType(EdmPrimitiveTypeKind.Decimal.getFullQualifiedName());
                        prop.setScale(rsmeta.getScale(column));
                        prop.setPrecision(rsmeta.getPrecision(column));
                        break;
                    case Types.BOOLEAN:
                        prop.setType(EdmPrimitiveTypeKind.Boolean.getFullQualifiedName());
                        break;
                    case Types.REAL:
                        prop.setType(EdmPrimitiveTypeKind.Single.getFullQualifiedName());
                        break;
                    case Types.DOUBLE:
                        prop.setType(EdmPrimitiveTypeKind.Double.getFullQualifiedName());
                        break;
                    case Types.DATE:
                        prop.setType(EdmPrimitiveTypeKind.Date.getFullQualifiedName());
                        break;
                    case Types.TIMESTAMP:
                        prop.setType(EdmPrimitiveTypeKind.DateTimeOffset.getFullQualifiedName());
                        break;
                    case Types.TIME:
                        prop.setType(EdmPrimitiveTypeKind.TimeOfDay.getFullQualifiedName());
                        break;
                    case Types.CHAR:
                    case Types.VARCHAR:
                        prop.setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
                        prop.setMaxLength(rsmeta.getColumnDisplaySize(column));
                        break;
                    default:
                        // TODO なにか手当が必要。あるいは、この場合はログ吐いたうえで処理対象から外すのが無難かも。
                        prop.setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
                        break;
                    }

                    if (false) {
                        // TODO FIXME いまここを有効にすると、なんとエラーが出てしまう。
                        // NULL許容かどうか。不明な場合は設定しない。
                        switch (rsmeta.isNullable(column)) {
                        case ResultSetMetaData.columnNullable:
                            prop.setNullable(true);
                            break;
                        case ResultSetMetaData.columnNoNulls:
                            prop.setNullable(false);
                            break;
                        default:
                            // なにもしない.
                            break;
                        }
                    }

                    // TODO デフォルト値の取得???

                }

                // テーブルのキー情報
                final List<CsdlPropertyRef> keyRefList = new ArrayList<>();
                final DatabaseMetaData dbmeta = conn.getMetaData();
                final ResultSet rsKey = dbmeta.getPrimaryKeys(null, null, entitySet.getDbTableNameIyo());
                for (; rsKey.next();) {
                    // キー名は利用しない: rsKey.getString("PK_NAME");
                    String colName = rsKey.getString("COLUMN_NAME");

                    CsdlPropertyRef propertyRef = new CsdlPropertyRef();
                    propertyRef.setName(colName);
                    keyRefList.add(propertyRef);
                }

                entityType.setKey(keyRefList);
            }

            // 構築結果を記憶。
            entitySet.setEntityType(entityType);
            return entityType;
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IllegalArgumentException("DB meta 取得失敗:" + ex.toString(), ex);
        }
    }
}
