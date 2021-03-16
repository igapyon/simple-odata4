package jp.igapyon.simpleodata4.oiyokan.h2.sql;

import java.util.ArrayList;
import java.util.List;

import jp.igapyon.simpleodata4.oiyokan.OiyokanCsdlEntitySet;

/**
 * SQL文を構築するための簡易クラスの、SQL構築のデータ構造.
 * 
 * このクラスの利用時には、SQL文を追加時に同時に併せてパラメータを追加すること。
 * 
 * 当面は、このクラスはSQL文とパラメータを蓄える。
 */
public class TinySqlBuildInfo {
    private OiyokanCsdlEntitySet entitySet = null;
    private final StringBuilder sqlBuilder = new StringBuilder();
    private final List<Object> sqlParamList = new ArrayList<>();

    /**
     * 処理対象のエンティティ名を設定.
     * 
     * @return 処理対象のエンティティ名.
     */
    public OiyokanCsdlEntitySet getEntitySet() {
        return entitySet;
    }

    public void setEntitySet(OiyokanCsdlEntitySet entitySet) {
        this.entitySet = entitySet;
    }

    /**
     * SQL文をビルド.
     * 
     * @return SQL文ビルド.
     */
    public StringBuilder getSqlBuilder() {
        return sqlBuilder;
    }

    /**
     * SQLパラメータ.
     * 
     * @return SQLパラメータのリスト.
     */
    public List<Object> getSqlParamList() {
        return sqlParamList;
    }
}
