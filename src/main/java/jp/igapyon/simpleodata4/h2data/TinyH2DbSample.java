package jp.igapyon.simpleodata4.h2data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 実際に返却するデータ本体を組み上げるクラス.
 * 
 * このクラスには、テスト用データを構築する処理も含む.
 */
public class TinyH2DbSample {
    // 増殖カウント. 最終的に 5000を目標にしたい.
    private static final int ZOUSYOKU = 5000;

    private TinyH2DbSample() {
    }

    /**
     * 情報を格納するためのテーブルをセットアップします。
     * 
     * @param conn データベース接続。
     */
    public static void createTable(final Connection conn) {
        // System.err.println("TRACE: 作業用データベーステーブルを作成");

        try (var stmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS " //
                + "MyProducts (" //
                + "ID BIGINT NOT NULL" // primary key.
                + ",Name VARCHAR(80)" //
                + ",Description VARCHAR(250)" //

                // 実験的フィールド一覧.

                // SByte, h2:TINYINT(?)
                + ",Sbyte1 TINYINT DEFAULT 127" //

                // Int16, h2:SMALLINT
                + ",Int16a SMALLINT DEFAULT 32767" //

                // Int32, h2:INT
                + ",Int32a INT DEFAULT 2147483647" //

                // Int64, h2:BIGINT
                + ",Int64a BIGINT DEFAULT 9223372036854775807" //

                // Decimal, h2:DECIMAL
                + ",Decimal1 DECIMAL(6,2) DEFAULT 1234.56" //

                // String, h2:VARCHAR, h2:CHAR
                + ",String1 CHAR(2) DEFAULT 'C1'" //
                + ",String2 VARCHAR(255) DEFAULT 'VARCHAR1'" //

                // H2の全文検索の対象外: Binary, h2:BINARY

                // Boolean, h2:BOOLEAN
                + ",Boolean1 BOOLEAN DEFAULT FALSE" //

                // TODO とりあえずパス: Single, h2:REAL
                // TODO とりあえずパス: Double, h2:DOUBLE

                // Date, h2:DATE(?) h2:TIMESTAMP(?)
                + ",Date1 DATE DEFAULT CURRENT_DATE()" //
                + ",Date2 TIMESTAMP DEFAULT CURRENT_TIMESTAMP()" //

                // TODO とりあえずパス: TimeOfDay, h2:TIME(?)

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

        try (var stmt = conn.prepareStatement("INSERT INTO MyProducts (ID, Name, Description) VALUES ("
                + TinyH2Util.getQueryPlaceholderString(3) + ")")) {
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
