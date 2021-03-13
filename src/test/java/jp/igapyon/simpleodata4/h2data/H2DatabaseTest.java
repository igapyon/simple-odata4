package jp.igapyon.simpleodata4.h2data;

import java.sql.Connection;

import org.junit.jupiter.api.Test;

// @SpringBootTest
class H2DatabaseTest {

    @Test
    void test01() throws Exception {
        Connection conn = TinyH2Util.getH2Connection();

        // テーブルをセットアップ.
        TinyH2DbSample.createTable(conn);

        // テーブルデータをセットアップ.
        TinyH2DbSample.setupTableData(conn);

        try (var stmt = conn.prepareStatement("SELECT ID, Name, Description FROM MyProducts ORDER BY ID")) {
            stmt.executeQuery();
            var rset = stmt.getResultSet();
            for (; rset.next();) {
                // System.err.println(rset.getString(1) + ":" + rset.getString(2) + ":" +
                // rset.getString(3));
            }
        }

        conn.close();
    }

}
