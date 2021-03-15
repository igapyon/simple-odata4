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
    /**
     * Oiyokan実装のキモ。シングルトンなコンテナ.
     */
    private static final OiyokanCsdlEntityContainer localTemplateEntityContainer = new OiyokanCsdlEntityContainer();

    private static OiyokanEdmProvider provider = new OiyokanEdmProvider();

    private OiyokanEdmProvider() {
    }

    public static OiyokanEdmProvider getInstance() {
        return provider;
    }

    /**
     * 与えられた型名のEntityTypeを取得.
     * 
     * @param entityTypeName 要素型名のFQN.
     * @return CSDL要素型.
     */
    @Override
    public CsdlEntityType getEntityType(FullQualifiedName entityTypeName) {
        // テンプレートを念押しビルド.
        localTemplateEntityContainer.ensureBuild();

        if (entityTypeName.equals(
                localTemplateEntityContainer.getEntitySetByEntityNameFqnIyo(entityTypeName).getEntityNameFqnIyo())) {
            // 処理対象の型名です。
            return localTemplateEntityContainer.getEntitySetByEntityNameFqnIyo(entityTypeName).getEdmBuilder()
                    .getEntityType();
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
        // テンプレートを念押しビルド.
        localTemplateEntityContainer.ensureBuild();

        // コンテナが一致する場合のみ処理対象.
        if (!entityContainer.equals(localTemplateEntityContainer.getContainerFqnIyo())) {
            return null;
        }

        // 要素セット名が一致する場合はそれを返却.
        // ヒットしない場合は対象外。その場合は null返却.
        return localTemplateEntityContainer.getEntitySet(entitySetName);
    }

    /**
     * 要素コンテナを取得.
     * 
     * @return CSDL要素コンテナ.
     */
    @Override
    public CsdlEntityContainer getEntityContainer() {
        // テンプレートを念押しビルド.
        localTemplateEntityContainer.ensureBuild();

        // 要素セットを作成.
        List<CsdlEntitySet> newEntitySetList = new ArrayList<>();
        for (CsdlEntitySet look : localTemplateEntityContainer.getEntitySets()) {
            OiyokanCsdlEntitySet look2 = (OiyokanCsdlEntitySet) look;
            // TODO 増殖か?
            newEntitySetList.add(getEntitySet(localTemplateEntityContainer.getContainerFqnIyo(), look2.getName()));
        }

        // 要素コンテナを作成.
        final OiyokanCsdlEntityContainer newEntityContainer = new OiyokanCsdlEntityContainer();
        newEntityContainer.setName(localTemplateEntityContainer.getContainerNameIyo());
        newEntityContainer.setEntitySets(newEntitySetList);

        newEntityContainer.ensureBuild();

        return newEntityContainer;
    }

    /**
     * スキーマ一覧を取得.
     * 
     * @return CSDLスキーマ.
     */
    @Override
    public List<CsdlSchema> getSchemas() {
        // テンプレートを念押しビルド.
        localTemplateEntityContainer.ensureBuild();

        // CSDLスキーマを作成.
        final CsdlSchema newSchema = new CsdlSchema();
        newSchema.setNamespace(localTemplateEntityContainer.getNamespaceIyo());

        // 要素型を設定.
        final List<CsdlEntityType> newEntityTypeList = new ArrayList<>();
        for (CsdlEntitySet look : localTemplateEntityContainer.getEntitySets()) {
            OiyokanCsdlEntitySet local2 = (OiyokanCsdlEntitySet) look;
            // エンティティタイプを設定.
            newEntityTypeList.add(getEntityType(local2.getEntityNameFqnIyo()));
        }
        newSchema.setEntityTypes(newEntityTypeList);

        // 要素コンテナを設定.
        newSchema.setEntityContainer(getEntityContainer());

        // CSDLスキーマを設定.
        final List<CsdlSchema> newSchemaList = new ArrayList<>();
        newSchemaList.add(newSchema);

        return newSchemaList;
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
        // テンプレートを念押しビルド.
        localTemplateEntityContainer.ensureBuild();

        if (entityContainerName == null
                || entityContainerName.equals(localTemplateEntityContainer.getContainerFqnIyo())) {
            final CsdlEntityContainerInfo entityContainerInfo = new CsdlEntityContainerInfo();
            entityContainerInfo.setContainerName(localTemplateEntityContainer.getContainerFqnIyo());
            return entityContainerInfo;
        }

        return null;
    }
}
