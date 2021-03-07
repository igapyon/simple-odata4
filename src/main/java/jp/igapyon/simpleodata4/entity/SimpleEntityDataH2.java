package jp.igapyon.simpleodata4.entity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 実際に返却するデータ本体を組み上げるクラス.
 * 
 * このクラスには、テスト用データを構築する処理も含む.
 */
public class SimpleEntityDataH2 {
    // 増殖カウント. 最終的に 5000を目標にしたい.
    private static final int ZOUSYOKU = 5000;

    private SimpleEntityDataH2() {
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

    /**
     * 情報を格納するためのテーブルをセットアップします。
     * 
     * @param conn データベース接続。
     */
    public static void setupTable(final Connection conn) {
        // System.err.println("TRACE: 作業用データベーステーブルを作成");

        try (var stmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS " //
                + "MyProducts (" //
                + "ID BIGINT NOT NULL" //
                + ",Name VARCHAR(80)" //
                + ",Description VARCHAR(250)" //
                + ",PRIMARY KEY(ID)" //
                + ")")) {
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new IllegalArgumentException("テーブル作成に失敗: " + ex.toString(), ex);
        }
    }

    /**
     * 情報を格納するためのテーブルをセットアップします。
     * 
     * @param conn データベース接続。
     */
    public static void setupTableData(final Connection conn) {
        try (var stmt = conn.prepareStatement("SELECT COUNT(ID) FROM MyProducts")) {
            stmt.executeQuery();
            var rset = stmt.getResultSet();
            rset.next();
            if (rset.getInt(1) > 0) {
                return;
            }
        } catch (SQLException ex) {
            throw new IllegalArgumentException("検索失敗:" + ex.toString(), ex);
        }

        // System.err.println("TRACE: 作業用サンプルデータを作成");

        // 全文検索関連の準備.
        try {
            try (PreparedStatement stmt = conn
                    .prepareStatement("CREATE ALIAS IF NOT EXISTS FT_INIT FOR \"org.h2.fulltext.FullText.init\"")) {
                stmt.executeUpdate();
            }
            try (PreparedStatement stmt = conn.prepareStatement("CALL FT_INIT()")) {
                stmt.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new IllegalArgumentException("全文検索の初期設定に失敗: " + ex.toString(), ex);
        }

        try (var stmt = conn.prepareStatement(
                "INSERT INTO MyProducts (ID, Name, Description) VALUES (" + getQueryPlaceholderString(3) + ")")) {
            int idCounter = 1;
            stmt.setInt(1, idCounter++);
            stmt.setString(2, "MacBookPro16,2");
            stmt.setString(3, "MacBook Pro (13-inch, 2020, Thunderbolt 3ポートx 4)");
            stmt.executeUpdate();

            stmt.clearParameters();
            stmt.setInt(1, idCounter++);
            stmt.setString(2, "MacBookPro E2015");
            stmt.setString(3, "MacBook Pro (Retina, 13-inch, Early 2015)");
            stmt.executeUpdate();

            stmt.clearParameters();
            stmt.setInt(1, idCounter++);
            stmt.setString(2, "Surface Laptop 2");
            stmt.setString(3, "Surface Laptop 2, 画面:13.5 インチ PixelSense ディスプレイ, インテル Core");
            stmt.executeUpdate();

            conn.commit();

            for (int index = 0; index < ZOUSYOKU; index++) {
                stmt.clearParameters();
                stmt.setInt(1, idCounter++);
                stmt.setString(2, "PopTablet" + idCounter);
                stmt.setString(3, "増殖タブレット Laptop Intel Core" + idCounter);
                stmt.executeUpdate();
            }
            conn.commit();

            for (int index = 0; index < ZOUSYOKU; index++) {
                stmt.clearParameters();
                stmt.setInt(1, idCounter++);
                stmt.setString(2, "DummyPC" + idCounter);
                stmt.setString(3, "ダミーなPC" + idCounter);
                stmt.executeUpdate();
            }
            conn.commit();

        } catch (SQLException ex) {
            throw new IllegalArgumentException("テーブル作成に失敗: " + ex.toString(), ex);
        }

        try {
            try (PreparedStatement stmt = conn.prepareStatement("CALL FT_CREATE_INDEX('PUBLIC', 'MyProducts', NULL)")) {
                stmt.executeUpdate();
            }
            try (PreparedStatement stmt = conn.prepareStatement("CALL FT_REINDEX()")) {
                stmt.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new IllegalArgumentException("全文検索の初期設定に失敗: " + ex.toString(), ex);
        }
    }
}
