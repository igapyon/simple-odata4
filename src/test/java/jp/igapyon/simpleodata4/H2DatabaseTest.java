package jp.igapyon.simpleodata4;

import java.sql.Connection;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import jp.igapyon.simpleodata4.entity.SimpleEntityDataBuilder;

@SpringBootTest
class H2DatabaseTest {

	@Test
	void test01() throws Exception {
		Connection conn = SimpleEntityDataBuilder.getH2Connection();

		try (var stmt = conn.prepareStatement("SELECT ID, Name, Description FROM Products")) {
			stmt.executeQuery();
			var rset = stmt.getResultSet();
			for (; rset.next();) {
				System.err.println(rset.getString(1) + ":" + rset.getString(2) + ":" + rset.getString(3));
			}
		}

		conn.close();
	}

}
