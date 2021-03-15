package jp.igapyon.simpleodata4.entity;

import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;

import jp.igapyon.simpleodata4.h2data.TinyH2EntityTypeBuilder;

/**
 * CsdlEntitySet の Iyokan 拡張
 */
public class OiyokanCsdlEntitySet extends CsdlEntitySet {
    /**
     * コンテナに関する情報を記憶.
     */
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

    private TinyH2EntityTypeBuilder edmBuilder = null;

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
        this.setType(new FullQualifiedName(containerInfo.getNamespaceIyo(), entityName));

        this.edmBuilder = new TinyH2EntityTypeBuilder(this);
    }

    /**
     * エンティティ名. MyProduct 相当.
     * 
     * @return エンティティ名. MyProduct 相当.
     */
    public String getEntityNameIyo() {
        return entityName;
    }

    /**
     * エンティティのFQNを取得.
     * 
     * @return エンティティのFQN(完全修飾名).
     */
    public FullQualifiedName getEntityNameFqnIyo() {
        return new FullQualifiedName(csdlEntityContainer.getNamespaceIyo(), getEntityNameIyo());
    }

    /**
     * 独自に追加した項目。
     * 
     * @return DBテーブル名。
     */
    public String getDbTableNameIyo() {
        return dbTableName;
    }

    //////////////////////////////////

    public TinyH2EntityTypeBuilder getEdmBuilder() {
        return edmBuilder;
    }
}
