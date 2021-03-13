package jp.igapyon.simpleodata4.h2data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * h2 database 用の小さなユーティリティクラス
 */
public class TinyH2Util {
    private TinyH2Util() {
    }

    /**
     * h2 データベースへのDB接続を取得します。
     * 
     * @return データベース接続。
     */
    public static Connection getH2Connection() {
        Connection conn;
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e);
        }
        // SQL Server 互換モードで動作させる.
        final var jdbcConnStr = "jdbc:h2:mem:myproducts;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=FALSE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;MODE=MSSQLServer";
        // System.err.println("TRACE: DEMO: [connect jdbc] " + jdbcConnStr);
        try {
            conn = DriverManager.getConnection(//
                    jdbcConnStr, "sa", "");
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new IllegalArgumentException(ex);
        }

        return conn;
    }

    /**
     * SQL検索プレースホルダの文字列を生成します。
     * 
     * @param count プレースホルダ数。
     * @return プレースホルダ文字列。
     */
    public static String getQueryPlaceholderString(int count) {
        String queryPlaceholder = "";
        for (int col = 0; col < count; col++) {
            if (col != 0) {
                queryPlaceholder += ",";
            }
            queryPlaceholder += "?";
        }

        return queryPlaceholder;
    }
}
