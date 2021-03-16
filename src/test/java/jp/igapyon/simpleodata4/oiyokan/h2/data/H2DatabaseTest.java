package jp.igapyon.simpleodata4.oiyokan.h2.data;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;

import org.junit.jupiter.api.Test;

import jp.igapyon.simpleodata4.oiyokan.basic.BasicDbUtil;

/**
 * そもそも内部 h2 database への接続性を確認
 */
class H2DatabaseTest {
    @Test
    void test01() throws Exception {
        Connection conn = BasicDbUtil.getH2Connection();

        // テーブルをセットアップ.
        TinyH2DbSample.createTable(conn);

        // テーブルデータをセットアップ.
        TinyH2DbSample.setupTableData(conn);

        try (var stmt = conn.prepareStatement("SELECT ID, Name, Description FROM MyProducts ORDER BY ID LIMIT 3")) {
            stmt.executeQuery();
            var rset = stmt.getResultSet();
            assertEquals(true, rset.next());
        }

        conn.close();
    }

}
