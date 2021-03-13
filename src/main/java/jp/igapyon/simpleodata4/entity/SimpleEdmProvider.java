package jp.igapyon.simpleodata4.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlAbstractEdmProvider;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainer;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainerInfo;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlPropertyRef;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;

import jp.igapyon.simpleodata4.h2data.TinyH2DbSample;
import jp.igapyon.simpleodata4.h2data.TinyH2Util;

/**
 * OData Common Schema Definition Language (CSDL) を提供するクラス.
 * 
 * コードの多くは olingo のための基礎的な記述に該当.
 */
public class SimpleEdmProvider extends CsdlAbstractEdmProvider {
    /**
     * サービスの名前空間. このソースをベースにカスタマイズする場合には変更.
     */
    public static final String NAMESPACE = "Igapyon.Simple";

    /**
     * EDMコンテナ名.
     */
    public static final String CONTAINER_NAME = "Container";

    /**
     * 要素型名. さしあたりはリレーショナルデータベースのテーブル名に相当するものと考えて差し支えない.
     */
    public static final String ET_MYPRODUCT_NAME = "MyProduct";

    /**
     * 要素型名の複数形. さしあたりはリレーショナルデータベースのテーブル名に相当するものに「s」をつけたものと考えて差し支えない. URIにも影響がある.
     */
    public static final String ES_MYPRODUCTS_NAME = "MyProducts";

    /**
     * エンティティのフィールド一覧.
     * 
     * 一意なID的なものがあるのが必要.
     * 
     * TODO この一覧は何かの工夫により自動的に可変になるようにしたい。
     */
    public static final String[] FIELDS = new String[] { "ID", "Name", "Description" };

    /**
     * EDMコンテナ名のFQN(完全修飾名).
     */
    public static final FullQualifiedName CONTAINER = new FullQualifiedName(NAMESPACE, CONTAINER_NAME);

    /**
     * 要素型のFQN(完全修飾名).
     */
    public static final FullQualifiedName ET_MYPRODUCT_FQN = new FullQualifiedName(NAMESPACE, ET_MYPRODUCT_NAME);

    /**
     * 与えられた型名のEntityType(要素型)のCSDLを取得.
     * 
     * @param entityTypeName 要素型名のFQN.
     * @return CSDL要素型.
     */
    @Override
    public CsdlEntityType getEntityType(FullQualifiedName entityTypeName) {
        if (entityTypeName.equals(ET_MYPRODUCT_FQN)) {
            // 処理対象の型名です。

            // この一覧を可変に対応できるようにしたい。

                // インメモリ作業データベースに接続.
                Connection conn = TinyH2Util.getH2Connection();

                // テーブルをセットアップ.
                TinyH2DbSample.createTable(conn);

               final List<CsdlProperty> propertyList=new ArrayList<>();
                try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM MyProducts")) {
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
                        case Types.INTEGER: /*INT*/
                            prop.setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());
                            break;
                        case Types.BIGINT:
                            prop.setType(EdmPrimitiveTypeKind.Int64.getFullQualifiedName());
                            break;
                        case Types.CHAR:
                        case Types.VARCHAR:
                        default:
                            prop.setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
                            break;

                        // Decimal, h2:DECIMAL

                        // Boolean, h2:BOOLEAN

                        // Date, h2:DATE(?) h2:TIMESTAMP(?)

                        }
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    throw new IllegalArgumentException("DB meta 取得失敗:" + ex.toString(), ex);
                }

            if (false) {
                // 要素の情報をプロパティとして組み上げ.
                CsdlProperty id = new CsdlProperty().setName(FIELDS[0])
                        .setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());
                CsdlProperty name = new CsdlProperty().setName(FIELDS[1])
                        .setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
                CsdlProperty description = new CsdlProperty().setName(FIELDS[2])
                        .setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
            }

            // キー要素を CsdlPropertyRef として設定.
            CsdlPropertyRef propertyRef = new CsdlPropertyRef();
            propertyRef.setName(FIELDS[0]);

            // CSDL要素型として情報を組み上げ.
            CsdlEntityType entityType = new CsdlEntityType();
            entityType.setName(ET_MYPRODUCT_NAME);
            entityType.setProperties(propertyList);
            entityType.setKey(Collections.singletonList(propertyRef));

            return entityType;
        }

        // 該当する型名の要素型はありません.
        return null;
    }

    /**
     * 与えられた型名の EntitySet(要素セット)情報を取得. 複数形.
     * 
     * @param entityContainer 要素コンテナ.
     * @param entitySetName   要素セット(複数形)の名前.
     * @return CSDL要素セット.
     */
    @Override
    public CsdlEntitySet getEntitySet(FullQualifiedName entityContainer, String entitySetName) {
        if (!entityContainer.equals(CONTAINER)) {
            // 該当する型名の要素セットはありません.
            return null;
        }

        // コンテナが一致する場合.

        // 要素セット名が一致する場合.
        if (entitySetName.equals(ES_MYPRODUCTS_NAME)) {
            // 要素セット名が一致する場合.
            // CSDL要素セットとして情報を組み上げ.
            CsdlEntitySet entitySet = new CsdlEntitySet();
            entitySet.setName(ES_MYPRODUCTS_NAME);
            entitySet.setType(ET_MYPRODUCT_FQN);

            return entitySet;
        }

        // 該当する型名の要素セットはありません.
        return null;
    }

    /**
     * 要素コンテナを取得.
     * 
     * @return CSDL要素コンテナ.
     */
    @Override
    public CsdlEntityContainer getEntityContainer() {
        // 要素セットを作成.
        List<CsdlEntitySet> entitySets = new ArrayList<>();
        entitySets.add(getEntitySet(CONTAINER, ES_MYPRODUCTS_NAME));

        // 要素コンテナを作成.
        CsdlEntityContainer entityContainer = new CsdlEntityContainer();
        entityContainer.setName(CONTAINER_NAME);
        entityContainer.setEntitySets(entitySets);

        return entityContainer;
    }

    /**
     * スキーマ一覧を取得.
     * 
     * @return CSDLスキーマ.
     */
    @Override
    public List<CsdlSchema> getSchemas() {
        // CSDLスキーマを作成.
        CsdlSchema schema = new CsdlSchema();
        schema.setNamespace(NAMESPACE);

        // 要素型を設定.
        List<CsdlEntityType> entityTypes = new ArrayList<>();
        entityTypes.add(getEntityType(ET_MYPRODUCT_FQN));
        schema.setEntityTypes(entityTypes);

        // 要素コンテナを設定.
        schema.setEntityContainer(getEntityContainer());

        // CSDLスキーマを設定.
        List<CsdlSchema> schemas = new ArrayList<>();
        schemas.add(schema);

        return schemas;
    }

    /**
     * 要素コンテナ情報を取得.
     * 
     * 次のようなURLの場合に呼び出される: http://localhost:8080/simple.svc/
     * 
     * @param entityContainerName 要素コンテナ名.
     * @return CSDL要素コンテナ情報.
     */
    @Override
    public CsdlEntityContainerInfo getEntityContainerInfo(FullQualifiedName entityContainerName) {
        if (entityContainerName == null || entityContainerName.equals(CONTAINER)) {
            CsdlEntityContainerInfo entityContainerInfo = new CsdlEntityContainerInfo();
            entityContainerInfo.setContainerName(CONTAINER);
            return entityContainerInfo;
        }

        return null;
    }
}
