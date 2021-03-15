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
    private static final OiyokanCsdlEntityContainer localTemplateEntityContainer = new OiyokanCsdlEntityContainer();

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
        localTemplateEntityContainer.ensureBuild();

        if (entityTypeName.equals(
                localTemplateEntityContainer.getLocalEntityInfoByEntityNameFQN(entityTypeName).getEntityNameFqnIyo())) {
            // 処理対象の型名です。
            return localTemplateEntityContainer.getLocalEntityInfoByEntityNameFQN(entityTypeName).getEdmBuilder().getEntityType();
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
        localTemplateEntityContainer.ensureBuild();

        if (!entityContainer.equals(localTemplateEntityContainer.getInternalContainerFQN())) {
            // 該当する型名の要素セットはありません.
            return null;
        }

        // コンテナが一致する場合.

        // 要素セット名が一致する場合.
        // CSDL要素セットとして情報を組み上げ.

        return localTemplateEntityContainer.getEntitySet(entitySetName);

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
        localTemplateEntityContainer.ensureBuild();

        // 要素セットを作成.
        List<CsdlEntitySet> entitySets = new ArrayList<>();
        for (CsdlEntitySet look : localTemplateEntityContainer.getEntitySets()) {
            OiyokanCsdlEntitySet look2 = (OiyokanCsdlEntitySet) look;
            // TODO 増殖か?
            entitySets.add(getEntitySet(localTemplateEntityContainer.getInternalContainerFQN(), look2.getName()));
        }

        // 要素コンテナを作成.
        OiyokanCsdlEntityContainer entityContainer = new OiyokanCsdlEntityContainer();
        entityContainer.setName(localTemplateEntityContainer.getContainerNameIyo());
        entityContainer.setEntitySets(entitySets);

        entityContainer.ensureBuild();

        return entityContainer;
    }

    /**
     * スキーマ一覧を取得.
     * 
     * @return CSDLスキーマ.
     */
    @Override
    public List<CsdlSchema> getSchemas() {
        localTemplateEntityContainer.ensureBuild();

        // CSDLスキーマを作成.
        CsdlSchema schema = new CsdlSchema();
        schema.setNamespace(localTemplateEntityContainer.getNamespaceIyo());

        // 要素型を設定.
        List<CsdlEntityType> look = new ArrayList<>();
        for (CsdlEntitySet localEntryInfo : localTemplateEntityContainer.getEntitySets()) {
            OiyokanCsdlEntitySet local2 = (OiyokanCsdlEntitySet) localEntryInfo;
            // TODO 増殖.
            look.add(getEntityType(local2.getEntityNameFqnIyo()));
        }

        schema.setEntityTypes(look);

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
        localTemplateEntityContainer.ensureBuild();

        if (entityContainerName == null || entityContainerName.equals(localTemplateEntityContainer.getInternalContainerFQN())) {
            CsdlEntityContainerInfo entityContainerInfo = new CsdlEntityContainerInfo();
            entityContainerInfo.setContainerName(localTemplateEntityContainer.getInternalContainerFQN());
            return entityContainerInfo;
        }

        return null;
    }
}
