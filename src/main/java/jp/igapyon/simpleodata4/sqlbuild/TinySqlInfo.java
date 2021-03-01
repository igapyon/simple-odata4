package jp.igapyon.simpleodata4.sqlbuild;

import java.util.ArrayList;
import java.util.List;

/**
 * SQL構築のデータ構造. SQL文を追加時に並行してパラメータを追加すること。
 * 
 * 当初は、SQL文とパラメータを蓄える。
 */
public class TinySqlInfo {
    private final StringBuilder sqlBuilder = new StringBuilder();
    private final List<Object> sqlParamList = new ArrayList<>();

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
