package jp.igapyon.simpleodata4.h2data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlPropertyRef;

public class TinyH2EdmBuilder {
    /**
     * 要素型名. さしあたりはリレーショナルデータベースのテーブル名に相当するものと考えて差し支えない.
     */
    private String etTargetName = null /* "MyProduct" */;

    /**
     * 要素型名の複数形. さしあたりはリレーショナルデータベースのテーブル名に相当するものに「s」をつけたものと考えて差し支えない. URIにも影響がある.
     */
    private String esTargetsName = null /* "MyProducts" */;

    public TinyH2EdmBuilder(String esTargetsName, String etTargetName) {
        this.esTargetsName = esTargetsName;
        this.etTargetName = etTargetName;
    }

    public CsdlEntityType getEntityType() {
        // この一覧を可変に対応できるようにしたい。

        // インメモリ作業データベースに接続.
        Connection conn = TinyH2Util.getH2Connection();

        // テーブルをセットアップ.
        TinyH2DbSample.createTable(conn);

        // バッファ的な h2 データベースから該当情報を取得.
        final List<CsdlProperty> propertyList = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " + esTargetsName)) {
            ResultSetMetaData rsmeta = stmt.getMetaData();
            final int columnCount = rsmeta.getColumnCount();
            for (int idxColumn = 1; idxColumn <= columnCount; idxColumn++) {
                final CsdlProperty prop = new CsdlProperty().setName(rsmeta.getColumnName(idxColumn));
                propertyList.add(prop);
                switch (rsmeta.getColumnType(idxColumn)) {
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
                    prop.setScale(rsmeta.getScale(idxColumn));
                    prop.setPrecision(rsmeta.getPrecision(idxColumn));
                    break;
                case Types.BOOLEAN:
                    prop.setType(EdmPrimitiveTypeKind.Boolean.getFullQualifiedName());
                    break;
                case Types.DATE:
                    prop.setType(EdmPrimitiveTypeKind.Date.getFullQualifiedName());
                    break;
                case Types.TIMESTAMP:
                    prop.setType(EdmPrimitiveTypeKind.DateTimeOffset.getFullQualifiedName());
                    break;
                case Types.CHAR:
                case Types.VARCHAR:
                    prop.setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
                    // TODO 桁数の取得方法不明.
                    break;
                default:
                    prop.setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
                    break;
                }

                // TODO デフォルト値の取得???
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IllegalArgumentException("DB meta 取得失敗:" + ex.toString(), ex);
        }

        // キー要素を CsdlPropertyRef として設定.
        CsdlPropertyRef propertyRef = new CsdlPropertyRef();
        propertyRef.setName("ID");

        // CSDL要素型として情報を組み上げ.
        CsdlEntityType entityType = new CsdlEntityType();
        entityType.setName(etTargetName);
        entityType.setProperties(propertyList);
        entityType.setKey(Collections.singletonList(propertyRef));

        return entityType;
    }
}
