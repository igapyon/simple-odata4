package jp.igapyon.simpleodata4.entity;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlAbstractEdmProvider;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainer;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainerInfo;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;

/**
 * OData Common Schema Definition Language (CSDL) を提供するクラス.
 * 
 * コードの多くは olingo のための基礎的な記述に該当.
 */
public class OiyokanEdmProvider extends CsdlAbstractEdmProvider {
    public static final OiyokanCsdlEntityContainer localContainerInfo = new OiyokanCsdlEntityContainer();

    private static OiyokanEdmProvider provider = new OiyokanEdmProvider();

    private OiyokanEdmProvider() {
    }

    public static OiyokanEdmProvider getInstance() {
        return provider;
    }

    /**
     * 与えられた型名のEntityType(要素型)のCSDLを取得.
     * 
     * @param entityTypeName 要素型名のFQN.
     * @return CSDL要素型.
     */
    @Override
    public CsdlEntityType getEntityType(FullQualifiedName entityTypeName) {
        if (entityTypeName.equals(
                localContainerInfo.getLocalEntityInfoByEntityNameFQN(entityTypeName).getInternalEntityNameFQN())) {
            // 処理対象の型名です。
            return localContainerInfo.getLocalEntityInfoByEntityNameFQN(entityTypeName).getEdmBuilder().getEntityType();
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
        if (!entityContainer.equals(localContainerInfo.getInternalContainerFQN())) {
            // 該当する型名の要素セットはありません.
            return null;
        }

        // コンテナが一致する場合.

        // 要素セット名が一致する場合.
        // CSDL要素セットとして情報を組み上げ.

        OiyokanCsdlEntitySet entitySet = null;
        if (entitySetName.equals("MyProducts")) {
            entitySet = new OiyokanCsdlEntitySet(localContainerInfo, "MyProducts", "MyProduct", "MyProducts");
        } else if (entitySetName.equals("ODataAppInfos")) {
            entitySet = new OiyokanCsdlEntitySet(localContainerInfo, "ODataAppInfos", "ODataAppInfo", "ODataAppInfos");
        }
        entitySet.setName(entitySetName);
        entitySet.setType(
                localContainerInfo.getLocalEntityInfoByEntitySetName(entitySetName).getInternalEntityNameFQN());

        return entitySet;

        // 該当する型名の要素セットはありません.
        // return null;
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
        for (OiyokanCsdlEntitySet localEntryInfo : localContainerInfo.getLocalEntityInfoList()) {
            // TODO 増殖か?
            entitySets.add(getEntitySet(localContainerInfo.getInternalContainerFQN(),
                    localEntryInfo.getInternalEntitySetName()));
        }

        // 要素コンテナを作成.
        OiyokanCsdlEntityContainer entityContainer = new OiyokanCsdlEntityContainer();
        entityContainer.setName(localContainerInfo.getInternalContainerName());
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
        schema.setNamespace(localContainerInfo.getInternalNamespace());

        // 要素型を設定.
        List<CsdlEntityType> entityTypes = new ArrayList<>();
        for (OiyokanCsdlEntitySet localEntryInfo : localContainerInfo.getLocalEntityInfoList()) {
            // TODO 増殖.
            entityTypes.add(getEntityType(localEntryInfo.getInternalEntityNameFQN()));
        }

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
        if (entityContainerName == null || entityContainerName.equals(localContainerInfo.getInternalContainerFQN())) {
            CsdlEntityContainerInfo entityContainerInfo = new CsdlEntityContainerInfo();
            entityContainerInfo.setContainerName(localContainerInfo.getInternalContainerFQN());
            return entityContainerInfo;
        }

        return null;
    }
}
