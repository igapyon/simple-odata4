package jp.igapyon.simpleodata4.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.queryoption.expression.BinaryOperatorKind;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.core.uri.parser.Parser;
import org.apache.olingo.server.core.uri.queryoption.expression.BinaryImpl;
import org.apache.olingo.server.core.uri.queryoption.expression.LiteralImpl;
import org.apache.olingo.server.core.uri.queryoption.expression.MemberImpl;
import org.junit.jupiter.api.Test;

class UriInfoTest {

	@Test
	void test01() throws Exception {
		OData odata = OData.newInstance();
		ServiceMetadata edm = odata.createServiceMetadata(new SimpleEdmProvider(), new ArrayList<>());

		final Parser parser = new Parser(edm.getEdm(), odata);
		final UriInfo uriInfo = parser.parseUri("/MyProducts", "$filter=ID eq 1.0", "",
				"https://localhost//simple.svc/");
		assertEquals("([ID] = 1.0)", expandExpression(uriInfo.getFilterOption().getExpression()));
	}

	@Test
	void test02() throws Exception {
		OData odata = OData.newInstance();
		ServiceMetadata edm = odata.createServiceMetadata(new SimpleEdmProvider(), new ArrayList<>());

		final Parser parser = new Parser(edm.getEdm(), odata);
		final UriInfo uriInfo = parser.parseUri("/MyProducts", "$filter=Description eq 'Mac' and ID eq 2.0", "",
				"https://localhost//simple.svc/");
		assertEquals("(([Description] = 'Mac') AND ([ID] = 2.0))",
				expandExpression(uriInfo.getFilterOption().getExpression()));
	}

	static String expandExpression(Expression filterExpression) {
		if (filterExpression instanceof MemberImpl) {
			return ((MemberImpl) filterExpression).toString();
		} else if (filterExpression instanceof LiteralImpl) {
			return ((LiteralImpl) filterExpression).toString();
		} else if (filterExpression instanceof BinaryImpl) {
			BinaryImpl impl = (BinaryImpl) filterExpression;
			BinaryOperatorKind opKind = impl.getOperator();
			if (opKind == BinaryOperatorKind.AND) {
				return "(" + expandExpression(impl.getLeftOperand()) + " AND "
						+ expandExpression(impl.getRightOperand()) + ")";
			} else if (opKind == BinaryOperatorKind.OR) {
				return "(" + expandExpression(impl.getLeftOperand()) + " OR " + expandExpression(impl.getRightOperand())
						+ ")";
			} else if (opKind == BinaryOperatorKind.NE) {
				return "(" + expandExpression(impl.getLeftOperand()) + " <> " + expandExpression(impl.getRightOperand())
						+ ")";
			} else if (opKind == BinaryOperatorKind.EQ) {
				return "(" + expandExpression(impl.getLeftOperand()) + " = " + expandExpression(impl.getRightOperand())
						+ ")";
			} else {
				System.err.println("対応しないOperator:" + opKind);
				return "[unsupported Operator:" + opKind + "," + impl.toString() + "]";
			}
		} else {
			System.err.println("対応しないクラス:" + filterExpression.getClass().getName());
			System.err.println("filterExpression:" + filterExpression.toString());
		}
		return filterExpression.toString();
	}
}
