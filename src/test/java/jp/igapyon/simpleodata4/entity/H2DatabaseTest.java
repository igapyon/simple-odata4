package jp.igapyon.simpleodata4.entity;

import java.sql.Connection;

import org.junit.jupiter.api.Test;

// @SpringBootTest
class H2DatabaseTest {

	@Test
	void test01() throws Exception {
		Connection conn = SimpleEntityDataH2.getH2Connection();

		// テーブルをセットアップ.
		SimpleEntityDataH2.setupTable(conn);

		// テーブルデータをセットアップ.
		SimpleEntityDataH2.setupTableData(conn);

		try (var stmt = conn.prepareStatement("SELECT ID, Name, Description FROM Products ORDER BY ID")) {
			stmt.executeQuery();
			var rset = stmt.getResultSet();
			for (; rset.next();) {
				System.err.println(rset.getString(1) + ":" + rset.getString(2) + ":" + rset.getString(3));
			}
		}

		conn.close();
	}

}
