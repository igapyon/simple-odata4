package jp.igapyon.simpleodata4.entity;

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

/**
 * OData Common Schema Definition Language (CSDL) を提供するクラス.
 */
public class SimpleEdmProvider extends CsdlAbstractEdmProvider {
    /**
     * サービスの名前空間.
     */
    public static final String NAMESPACE = "OData.Demo";

    /**
     * EDMコンテナ名.
     */
    public static final String CONTAINER_NAME = "Container";

    /**
     * EDMコンテナ名のFQN(完全修飾名).
     */
    public static final FullQualifiedName CONTAINER = new FullQualifiedName(NAMESPACE, CONTAINER_NAME);

    /**
     * 要素型名.
     */
    public static final String ET_PRODUCT_NAME = "Product";

    /**
     * 要素型のFQN(完全修飾名).
     */
    public static final FullQualifiedName ET_PRODUCT_FQN = new FullQualifiedName(NAMESPACE, ET_PRODUCT_NAME);

    /**
     * 要素型名の複数形.
     */
    public static final String ES_PRODUCTS_NAME = "Products";

    /**
     * 与えられた型名のEntityType(要素型)のCSDLを取得.
     * 
     * @param entityTypeName 要素型名のFQN.
     * @return CSDL要素型.
     */
    @Override
    public CsdlEntityType getEntityType(FullQualifiedName entityTypeName) {
        if (entityTypeName.equals(ET_PRODUCT_FQN)) {
            // 処理対象の型名です。

            // 要素の情報をプロパティとして組み上げ.
            CsdlProperty id = new CsdlProperty().setName("ID")
                    .setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());
            CsdlProperty name = new CsdlProperty().setName("Name")
                    .setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
            CsdlProperty description = new CsdlProperty().setName("Description")
                    .setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());

            // キー要素を CsdlPropertyRef として設定.
            CsdlPropertyRef propertyRef = new CsdlPropertyRef();
            propertyRef.setName("ID");

            // CSDL要素型として情報を組み上げ.
            CsdlEntityType entityType = new CsdlEntityType();
            entityType.setName(ET_PRODUCT_NAME);
            entityType.setProperties(Arrays.asList(id, name, description));
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
        if (entitySetName.equals(ES_PRODUCTS_NAME)) {
            // 要素セット名が一致する場合.
            // CSDL要素セットとして情報を組み上げ.
            CsdlEntitySet entitySet = new CsdlEntitySet();
            entitySet.setName(ES_PRODUCTS_NAME);
            entitySet.setType(ET_PRODUCT_FQN);

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
        entitySets.add(getEntitySet(CONTAINER, ES_PRODUCTS_NAME));

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
        entityTypes.add(getEntityType(ET_PRODUCT_FQN));
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
