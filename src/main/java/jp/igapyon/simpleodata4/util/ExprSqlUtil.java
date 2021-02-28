package jp.igapyon.simpleodata4.util;

import org.apache.olingo.server.api.uri.queryoption.expression.BinaryOperatorKind;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.api.uri.queryoption.expression.MethodKind;
import org.apache.olingo.server.api.uri.queryoption.expression.UnaryOperatorKind;
import org.apache.olingo.server.core.uri.queryoption.expression.AliasImpl;
import org.apache.olingo.server.core.uri.queryoption.expression.BinaryImpl;
import org.apache.olingo.server.core.uri.queryoption.expression.EnumerationImpl;
import org.apache.olingo.server.core.uri.queryoption.expression.LambdaRefImpl;
import org.apache.olingo.server.core.uri.queryoption.expression.LiteralImpl;
import org.apache.olingo.server.core.uri.queryoption.expression.MemberImpl;
import org.apache.olingo.server.core.uri.queryoption.expression.MethodImpl;
import org.apache.olingo.server.core.uri.queryoption.expression.TypeLiteralImpl;
import org.apache.olingo.server.core.uri.queryoption.expression.UnaryImpl;

/**
 * Expression を SQLに変換。
 */
public class ExprSqlUtil {
    private ExprSqlUtil() {
    }

    /**
     * フィルタを展開。WHEREになる。
     * 
     * @param filterExpression フィルタ表現.
     * @return 展開後SQL.
     */
    public static String expand(Expression filterExpression) {
        if (filterExpression instanceof AliasImpl) {
            throw new IllegalArgumentException("NOT IMPLEMENTED:AliasImpl");
        } else if (filterExpression instanceof BinaryImpl) {
            BinaryImpl impl = (BinaryImpl) filterExpression;
            return expandBinary(impl);
        } else if (filterExpression instanceof EnumerationImpl) {
            throw new IllegalArgumentException("NOT IMPLEMENTED:EnumerationImpl");
        } else if (filterExpression instanceof LambdaRefImpl) {
            throw new IllegalArgumentException("NOT IMPLEMENTED:LambdaRefImpl");
        } else if (filterExpression instanceof LiteralImpl) {
            return ((LiteralImpl) filterExpression).toString();
        } else if (filterExpression instanceof MemberImpl) {
            return ((MemberImpl) filterExpression).toString();
        } else if (filterExpression instanceof MethodImpl) {
            MethodImpl impl = (MethodImpl) filterExpression;
            return expandMethod(impl);
        } else if (filterExpression instanceof TypeLiteralImpl) {
            throw new IllegalArgumentException("NOT IMPLEMENTED:TypeLiteralImpl");
        } else if (filterExpression instanceof UnaryImpl) {
            UnaryImpl impl = (UnaryImpl) filterExpression;
            return expandUnary(impl);
        }

        final String message = "Unexpected Case: Unsupported expression:" + filterExpression.getClass().getName() + ","
                + filterExpression.toString() + "]";
        System.err.println(message);
        throw new IllegalArgumentException(message);
    }

    ///////////////////////////////////////////////////////////////
    // 内部の実際処理.

    private static String expandBinary(BinaryImpl impl) {
        BinaryOperatorKind opKind = impl.getOperator();
        if (opKind == BinaryOperatorKind.HAS) {
            // HAS
            throw new IllegalArgumentException("NOT IMPLEMENTED:" + opKind);
        } else if (opKind == BinaryOperatorKind.IN) {
            // IN
            throw new IllegalArgumentException("NOT IMPLEMENTED:" + opKind);
        } else if (opKind == BinaryOperatorKind.MUL) {
            // MUL
            throw new IllegalArgumentException("NOT IMPLEMENTED:" + opKind);
        } else if (opKind == BinaryOperatorKind.DIV) {
            // DIV
            throw new IllegalArgumentException("NOT IMPLEMENTED:" + opKind);
        } else if (opKind == BinaryOperatorKind.MOD) {
            // MOD
            throw new IllegalArgumentException("NOT IMPLEMENTED:" + opKind);
        } else if (opKind == BinaryOperatorKind.ADD) {
            // ADD
            throw new IllegalArgumentException("NOT IMPLEMENTED:" + opKind);
        } else if (opKind == BinaryOperatorKind.SUB) {
            // SUB
            throw new IllegalArgumentException("NOT IMPLEMENTED:" + opKind);
        } else if (opKind == BinaryOperatorKind.GT) {
            // GT
            return "(" + expand(impl.getLeftOperand()) + " > " + expand(impl.getRightOperand()) + ")";
        } else if (opKind == BinaryOperatorKind.GE) {
            // GE
            return "(" + expand(impl.getLeftOperand()) + " >= " + expand(impl.getRightOperand()) + ")";

        } else if (opKind == BinaryOperatorKind.LT) {
            // LT
            return "(" + expand(impl.getLeftOperand()) + " < " + expand(impl.getRightOperand()) + ")";
        } else if (opKind == BinaryOperatorKind.LE) {
            // LE
            return "(" + expand(impl.getLeftOperand()) + " <= " + expand(impl.getRightOperand()) + ")";
        } else if (opKind == BinaryOperatorKind.EQ) {
            // EQ
            return "(" + expand(impl.getLeftOperand()) + " = " + expand(impl.getRightOperand()) + ")";
        } else if (opKind == BinaryOperatorKind.NE) {
            // NE
            return "(" + expand(impl.getLeftOperand()) + " <> " + expand(impl.getRightOperand()) + ")";
        } else if (opKind == BinaryOperatorKind.AND) {
            // AND
            return "(" + expand(impl.getLeftOperand()) + " AND " + expand(impl.getRightOperand()) + ")";
        } else if (opKind == BinaryOperatorKind.OR) {
            // OR
            return "(" + expand(impl.getLeftOperand()) + " OR " + expand(impl.getRightOperand()) + ")";
        }

        final String message = "Unexpected Case: Unsupported binary operator:" + opKind + "," + impl.toString() + "]";
        System.err.println(message);
        throw new IllegalArgumentException(message);
    }

    private static String expandMethod(MethodImpl impl) {
        if (impl.getMethod() == MethodKind.INDEXOF) {
            // h2 database の POSITION は 0 オリジンなので 1 を減らしています。
            return "(POSITION(" + expand(impl.getParameters().get(1)) + "," + expand(impl.getParameters().get(0))
                    + ") - 1)";
        } else if (impl.getMethod() == MethodKind.STARTSWITH) {
            // h2 database の POSITION は 0 オリジンなので 1 を減らしています。
            return "(POSITION(" + expand(impl.getParameters().get(1)) + "," + expand(impl.getParameters().get(0))
                    + ") = 1)";
        }

        final String message = "Unexpected Case: Unsupported MethodKind:" + impl.getMethod() + "," + impl.toString()
                + "]";
        System.err.println(message);
        throw new IllegalArgumentException(message);
    }

    private static String expandUnary(UnaryImpl impl) {
        if (impl.getOperator() == UnaryOperatorKind.NOT) {
            return "(NOT (" + expand(impl.getOperand()) + "))";
        } else if (impl.getOperator() == UnaryOperatorKind.MINUS) {
            return "(-(" + expand(impl.getOperand()) + "))";
        }

        final String message = "Unexpected Case: Unsupported UnaryOperatorKind:" + impl.getOperator() + ","
                + impl.toString() + "]";
        System.err.println(message);
        throw new IllegalArgumentException(message);
    }
}
