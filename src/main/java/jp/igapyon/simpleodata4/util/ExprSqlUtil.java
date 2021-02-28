package jp.igapyon.simpleodata4.util;

import org.apache.olingo.server.api.uri.queryoption.expression.BinaryOperatorKind;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.core.uri.queryoption.expression.BinaryImpl;
import org.apache.olingo.server.core.uri.queryoption.expression.LiteralImpl;
import org.apache.olingo.server.core.uri.queryoption.expression.MemberImpl;

public class ExprSqlUtil {
    private ExprSqlUtil() {
    }

    /**
     * フィルタを展開
     * 
     * @param filterExpression フィルタ表現.
     * @return 展開後SQL.
     */
    public static String expand(Expression filterExpression) {
        if (filterExpression instanceof MemberImpl) {
            return ((MemberImpl) filterExpression).toString();
        } else if (filterExpression instanceof LiteralImpl) {
            return ((LiteralImpl) filterExpression).toString();
        } else if (filterExpression instanceof BinaryImpl) {
            BinaryImpl impl = (BinaryImpl) filterExpression;
            BinaryOperatorKind opKind = impl.getOperator();
            if (opKind == BinaryOperatorKind.AND) {
                return "(" + expand(impl.getLeftOperand()) + " AND " + expand(impl.getRightOperand()) + ")";
            } else if (opKind == BinaryOperatorKind.OR) {
                return "(" + expand(impl.getLeftOperand()) + " OR " + expand(impl.getRightOperand()) + ")";
            } else if (opKind == BinaryOperatorKind.NE) {
                return "(" + expand(impl.getLeftOperand()) + " <> " + expand(impl.getRightOperand()) + ")";
            } else if (opKind == BinaryOperatorKind.EQ) {
                return "(" + expand(impl.getLeftOperand()) + " = " + expand(impl.getRightOperand()) + ")";
            } else if (opKind == BinaryOperatorKind.LT) {
                return "(" + expand(impl.getLeftOperand()) + " < " + expand(impl.getRightOperand()) + ")";
            } else if (opKind == BinaryOperatorKind.LE) {
                return "(" + expand(impl.getLeftOperand()) + " <= " + expand(impl.getRightOperand()) + ")";
            } else if (opKind == BinaryOperatorKind.GT) {
                return "(" + expand(impl.getLeftOperand()) + " > " + expand(impl.getRightOperand()) + ")";
            } else if (opKind == BinaryOperatorKind.GE) {
                return "(" + expand(impl.getLeftOperand()) + " >= " + expand(impl.getRightOperand()) + ")";
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
