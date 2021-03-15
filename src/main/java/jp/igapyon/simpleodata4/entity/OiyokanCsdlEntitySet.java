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

    private SimpleContainerInfo containerInfo = null;

    /**
     * 要素型名の複数形. さしあたりはリレーショナルデータベースのテーブル名に相当するものに「s」をつけたものと考えて差し支えない. URIにも影響がある.
     * 
     * MyProducts 相当.
     */
    private String entitySetName = null;

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
    public OiyokanCsdlEntitySet(SimpleContainerInfo containerInfo, String entitySetName, String entityName,
            String dbTableName) {
        this.containerInfo = containerInfo;
        this.entitySetName = entitySetName;
        this.entityName = entityName;
        this.dbTableName = dbTableName;

        this.edmBuilder = new TinyH2EdmBuilder(this);
    }

    public SimpleContainerInfo getInternalContainerInfo() {
        return containerInfo;
    }

    public void setInternalContainerInfo(SimpleContainerInfo containerInfo) {
        this.containerInfo = containerInfo;
    }

    /**
     * エンティティ名. MyProduct 相当.
     * 
     * @return エンティティ名. MyProduct 相当.
     */
    public String getInternalEntityName() {
        return entityName;
    }

    public void setInternalEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getInternalEntitySetName() {
        return entitySetName;
    }

    public void setInternalEntitySetName(String entitySetName) {
        this.entitySetName = entitySetName;
    }

    public String getInternalDbTableName() {
        return dbTableName;
    }

    public void setInternalDbTableName(String dbTableName) {
        this.dbTableName = dbTableName;
    }

    //////////////////////////////////
    //

    /**
     * 要素型のFQN(完全修飾名).
     * 
     * @return 要素型のFQN(完全修飾名).
     */
    public FullQualifiedName getInternalEntityNameFQN() {
        return new FullQualifiedName(containerInfo.getInternalNamespace(), getInternalEntityName());
    }
}
