package jp.igapyon.simpleodata4.entity;

import java.sql.Connection;
import java.util.ArrayList;

import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.core.uri.parser.Parser;
import org.apache.olingo.server.core.uri.parser.UriTokenizer;
import org.apache.olingo.server.core.uri.parser.UriTokenizer.TokenKind;
import org.junit.jupiter.api.Test;

class ParserTest {

	@Test
	void test01() throws Exception {
		OData odata = OData.newInstance();
		ServiceMetadata edm = odata.createServiceMetadata(new SimpleEdmProvider(), new ArrayList<>());

		final Parser parser = new Parser(edm.getEdm(), odata);
		final UriInfo uriInfo = parser.parseUri("/MyProducts", "$filter=ID eq 1.0", "",
				"https://localhost//simple.svc/");
		System.err.println(uriInfo.getFilterOption().getExpression());
	}

}
