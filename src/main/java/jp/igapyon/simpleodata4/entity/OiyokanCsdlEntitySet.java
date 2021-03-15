package jp.igapyon.simpleodata4.entity;

import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;

import jp.igapyon.simpleodata4.h2data.TinyH2EdmBuilder;

public class OiyokanCsdlEntitySet extends CsdlEntitySet {
    private TinyH2EdmBuilder edmBuilder = null;

    public TinyH2EdmBuilder getEdmBuilder() {
        return edmBuilder;
    }

    public void setEdmBuilder(TinyH2EdmBuilder edmBuilder) {
        this.edmBuilder = edmBuilder;
    }

    private OiyokanCsdlEntityContainer csdlEntityContainer = null;

    /**
     * 要素型名. さしあたりはリレーショナルデータベースのテーブル名に相当するものと考えて差し支えない.
     * 
     * MyProduct 相当.
     */
    private String entityName = null;

    /**
     * データベース上のテーブル名.
     */
    private String dbTableName = null;

    /**
     * エンティティ情報.
     * 
     * @param containerInfo コンテナ情報.
     * @param entitySetName MyProducts 相当.
     * @param entityName    MyProduct 相当.
     * @param dbTableName   データベース上のテーブル名.
     */
    public OiyokanCsdlEntitySet(OiyokanCsdlEntityContainer containerInfo, String entitySetName, String entityName,
            String dbTableName) {
        this.csdlEntityContainer = containerInfo;
        this.entityName = entityName;
        this.dbTableName = dbTableName;

        this.setName(entitySetName);
        this.edmBuilder = new TinyH2EdmBuilder(this);
    }

    /**
     * エンティティ名. MyProduct 相当.
     * 
     * @return エンティティ名. MyProduct 相当.
     */
    public String getInternalEntityName() {
        return entityName;
    }

    /**
     * 独自に追加した項目。
     * 
     * @return DBテーブル名。
     */
    public String getDbTableName() {
        return dbTableName;
    }

    //////////////////////////////////
    //

    /**
     * 要素型のFQN(完全修飾名).
     * 
     * @return 要素型のFQN(完全修飾名).
     */
    public FullQualifiedName getInternalEntityNameFQN() {
        return new FullQualifiedName(csdlEntityContainer.getNamespace(), getInternalEntityName());
    }
}
