package jp.igapyon.simpleodata4.entity;

import org.apache.olingo.commons.api.edm.FullQualifiedName;

import jp.igapyon.simpleodata4.h2data.TinyH2EdmBuilder;

public class SimpleEntityInfo {
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

    private TinyH2EdmBuilder edmBuilder = null;

    private SimpleEntityInfo() {
    }

    /**
     * エンティティ情報.
     * 
     * @param containerInfo コンテナ情報.
     * @param entitySetName MyProducts 相当.
     * @param entityName    MyProduct 相当.
     * @param dbTableName   データベース上のテーブル名.
     */
    public SimpleEntityInfo(SimpleContainerInfo containerInfo, String entitySetName, String entityName,
            String dbTableName) {
        this.containerInfo = containerInfo;
        this.entitySetName = entitySetName;
        this.entityName = entityName;
        this.dbTableName = dbTableName;

        // TODO 引数を変更すべきか
        this.edmBuilder = new TinyH2EdmBuilder(entitySetName, entityName);
    }

    public SimpleContainerInfo getContainerInfo() {
        return containerInfo;
    }

    public void setContainerInfo(SimpleContainerInfo containerInfo) {
        this.containerInfo = containerInfo;
    }

    /**
     * エンティティ名. MyProduct 相当.
     * 
     * @return エンティティ名. MyProduct 相当.
     */
    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntitySetName() {
        return entitySetName;
    }

    public void setEntitySetName(String entitySetName) {
        this.entitySetName = entitySetName;
    }

    public String getDbTableName() {
        return dbTableName;
    }

    public void setDbTableName(String dbTableName) {
        this.dbTableName = dbTableName;
    }

    public TinyH2EdmBuilder getEdmBuilder() {
        return edmBuilder;
    }

    //////////////////////////////////
    //

    /**
     * 要素型のFQN(完全修飾名).
     * 
     * @return 要素型のFQN(完全修飾名).
     */
    public FullQualifiedName getEntityNameFQN() {
        return new FullQualifiedName(containerInfo.getNamespace(), getEntityName());
    }
}
