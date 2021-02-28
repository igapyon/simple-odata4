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
public class TinySqlExprExpander {
    private TinySqlInfo sqlInfo = null;

    public TinySqlExprExpander(TinySqlInfo sqlInfo) {
        this.sqlInfo = sqlInfo;
    }

    /**
     * フィルタを展開。WHEREになる。
     * 
     * @param filterExpression フィルタ表現.
     * @return 展開後SQL.
     */
    public static String expand(Expression filterExpression) {
        if (filterExpression instanceof AliasImpl) {
            throw new IllegalArgumentException("NOT SUPPORTED:Expression:AliasImpl");
        } else if (filterExpression instanceof BinaryImpl) {
            return expandBinary((BinaryImpl) filterExpression);
        } else if (filterExpression instanceof EnumerationImpl) {
            throw new IllegalArgumentException("NOT SUPPORTED:Expression:EnumerationImpl");
        } else if (filterExpression instanceof LambdaRefImpl) {
            throw new IllegalArgumentException("NOT SUPPORTED:Expression:LambdaRefImpl");
        } else if (filterExpression instanceof LiteralImpl) {
            return expandLiteral((LiteralImpl) filterExpression);
        } else if (filterExpression instanceof MemberImpl) {
            return expandMember((MemberImpl) filterExpression);
        } else if (filterExpression instanceof MethodImpl) {
            return expandMethod((MethodImpl) filterExpression);
        } else if (filterExpression instanceof TypeLiteralImpl) {
            throw new IllegalArgumentException("NOT SUPPORTED:Expression:TypeLiteralImpl");
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
            throw new IllegalArgumentException("NOT SUPPORTED:BinaryOperatorKind:" + opKind);
        } else if (opKind == BinaryOperatorKind.IN) {
            // IN
            throw new IllegalArgumentException("NOT SUPPORTED:BinaryOperatorKind:" + opKind);
        } else if (opKind == BinaryOperatorKind.MUL) {
            // MUL
            throw new IllegalArgumentException("NOT SUPPORTED:BinaryOperatorKind:" + opKind);
        } else if (opKind == BinaryOperatorKind.DIV) {
            // DIV
            throw new IllegalArgumentException("NOT SUPPORTED:BinaryOperatorKind:" + opKind);
        } else if (opKind == BinaryOperatorKind.MOD) {
            // MOD
            throw new IllegalArgumentException("NOT SUPPORTED:BinaryOperatorKind:" + opKind);
        } else if (opKind == BinaryOperatorKind.ADD) {
            // ADD
            throw new IllegalArgumentException("NOT SUPPORTED:BinaryOperatorKind:" + opKind);
        } else if (opKind == BinaryOperatorKind.SUB) {
            // SUB
            throw new IllegalArgumentException("NOT SUPPORTED:BinaryOperatorKind:" + opKind);
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

    private static String expandLiteral(LiteralImpl impl) {
        // そのままSQLのリテラルとする。
        return impl.toString();
    }

    private static String expandMember(MemberImpl impl) {
        // そのままSQLのメンバーとする。
        return impl.toString();
    }

    private static String expandMethod(MethodImpl impl) {

        // CONTAINS
        if (impl.getMethod() == MethodKind.CONTAINS) {
            // h2 database の POSITION は 1 オリジンで発見せずが0 なので 1 を減らしています。
            return "(POSITION(" + expand(impl.getParameters().get(1)) + "," + expand(impl.getParameters().get(0))
                    + ") > 0)";
        }

        // STARTSWITH
        if (impl.getMethod() == MethodKind.STARTSWITH) {
            // h2 database の POSITION は 1 オリジンで発見せずが0 なので 1 を減らしています。
            return "(POSITION(" + expand(impl.getParameters().get(1)) + "," + expand(impl.getParameters().get(0))
                    + ") = 1)";
        }

        // ENDSWITH

        // LENGTH

        // INDEXOF
        if (impl.getMethod() == MethodKind.INDEXOF) {
            // h2 database の POSITION は 1 オリジンで発見せずが0 なので 1 を減らしています。
            return "(POSITION(" + expand(impl.getParameters().get(1)) + "," + expand(impl.getParameters().get(0))
                    + ") - 1)";
        }

        // SUBSTRING

        // TOLOWER

        // TOUPPER

        // TRIM

        // CONCAT

        // YEAR

        // MONTH

        // DAY

        // HOUR

        // MINUTE

        // SECOND

        // FRACTIONALSECONDS

        // TOTALSECONDS

        // DATE

        // TIME

        // TOTALOFFSETMINUTES

        // MINDATETIME

        // MAXDATETIME

        // NOW

        // ROUND

        // FLOOR

        // CEILING

        // GEODISTANCE

        // GEOLENGTH

        // GEOINTERSECTS

        // CAST

        // ISOF

        // SUBSTRINGOF

        final String message = "Unexpected Case: NOT SUPPORTED MethodKind:" + impl.getMethod() + "," + impl.toString()
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
