package jp.igapyon.simpleodata4.util;

import java.util.ArrayList;
import java.util.List;

public class TinySqlInfo {
    private final StringBuilder sqlBuilder = new StringBuilder();
    private final List<Object> sqlParamList = new ArrayList<>();

    public StringBuilder getSqlBuilder() {
        return sqlBuilder;
    }

    public List<Object> getSqlParamList() {
        return sqlParamList;
    }
}
