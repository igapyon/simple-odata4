package jp.igapyon.simpleodata4.util;

import java.util.ArrayList;
import java.util.List;

public class TinySqlInfo {
    private final StringBuilder sqlBuilder = new StringBuilder();
    private final List<?> sqlParamList = new ArrayList<>();

    public StringBuilder getSqlBuilder() {
        return sqlBuilder;
    }

    public List<?> getSqlParamList() {
        return sqlParamList;
    }
}
