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
		
		conn.close();
	}

}
