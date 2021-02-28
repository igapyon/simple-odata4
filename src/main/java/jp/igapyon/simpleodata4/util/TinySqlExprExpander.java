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
    public void expand(Expression filterExpression) {
        if (filterExpression instanceof AliasImpl) {
            throw new IllegalArgumentException("NOT SUPPORTED:Expression:AliasImpl");
        } else if (filterExpression instanceof BinaryImpl) {
            expandBinary((BinaryImpl) filterExpression);
            return;
        } else if (filterExpression instanceof EnumerationImpl) {
            throw new IllegalArgumentException("NOT SUPPORTED:Expression:EnumerationImpl");
        } else if (filterExpression instanceof LambdaRefImpl) {
            throw new IllegalArgumentException("NOT SUPPORTED:Expression:LambdaRefImpl");
        } else if (filterExpression instanceof LiteralImpl) {
            expandLiteral((LiteralImpl) filterExpression);
            return;
        } else if (filterExpression instanceof MemberImpl) {
            expandMember((MemberImpl) filterExpression);
            return;
        } else if (filterExpression instanceof MethodImpl) {
            expandMethod((MethodImpl) filterExpression);
            return;
        } else if (filterExpression instanceof TypeLiteralImpl) {
            throw new IllegalArgumentException("NOT SUPPORTED:Expression:TypeLiteralImpl");
        } else if (filterExpression instanceof UnaryImpl) {
            UnaryImpl impl = (UnaryImpl) filterExpression;
            expandUnary(impl);
            return;
        }

        final String message = "Unexpected Case: Unsupported expression:" + filterExpression.getClass().getName() + ","
                + filterExpression.toString() + "]";
        System.err.println(message);
        throw new IllegalArgumentException(message);
    }

    ///////////////////////////////////////////////////////////////
    // 内部の実際処理.

    private void expandBinary(BinaryImpl impl) {
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
            sqlInfo.getSqlBuilder().append("(");
            expand(impl.getLeftOperand());
            sqlInfo.getSqlBuilder().append(" > ");
            expand(impl.getRightOperand());
            sqlInfo.getSqlBuilder().append(")");
            return;
        } else if (opKind == BinaryOperatorKind.GE) {
            // GE
            sqlInfo.getSqlBuilder().append("(");
            expand(impl.getLeftOperand());
            sqlInfo.getSqlBuilder().append(" >= ");
            expand(impl.getRightOperand());
            sqlInfo.getSqlBuilder().append(")");
            return;
        } else if (opKind == BinaryOperatorKind.LT) {
            // LT
            sqlInfo.getSqlBuilder().append("(");
            expand(impl.getLeftOperand());
            sqlInfo.getSqlBuilder().append(" < ");
            expand(impl.getRightOperand());
            sqlInfo.getSqlBuilder().append(")");
            return;
        } else if (opKind == BinaryOperatorKind.LE) {
            // LE
            sqlInfo.getSqlBuilder().append("(");
            expand(impl.getLeftOperand());
            sqlInfo.getSqlBuilder().append(" <= ");
            expand(impl.getRightOperand());
            sqlInfo.getSqlBuilder().append(")");
            return;
        } else if (opKind == BinaryOperatorKind.EQ) {
            // EQ
            sqlInfo.getSqlBuilder().append("(");
            expand(impl.getLeftOperand());
            sqlInfo.getSqlBuilder().append(" = ");
            expand(impl.getRightOperand());
            sqlInfo.getSqlBuilder().append(")");
            return;
        } else if (opKind == BinaryOperatorKind.NE) {
            // NE
            sqlInfo.getSqlBuilder().append("(");
            expand(impl.getLeftOperand());
            sqlInfo.getSqlBuilder().append(" <> ");
            expand(impl.getRightOperand());
            sqlInfo.getSqlBuilder().append(")");
            return;
        } else if (opKind == BinaryOperatorKind.AND) {
            // AND
            sqlInfo.getSqlBuilder().append("(");
            expand(impl.getLeftOperand());
            sqlInfo.getSqlBuilder().append(" AND ");
            expand(impl.getRightOperand());
            sqlInfo.getSqlBuilder().append(")");
            return;
        } else if (opKind == BinaryOperatorKind.OR) {
            // OR
            sqlInfo.getSqlBuilder().append("(");
            expand(impl.getLeftOperand());
            sqlInfo.getSqlBuilder().append(" OR ");
            expand(impl.getRightOperand());
            sqlInfo.getSqlBuilder().append(")");
            return;
        }

        final String message = "Unexpected Case: Unsupported binary operator:" + opKind + "," + impl.toString() + "]";
        System.err.println(message);
        throw new IllegalArgumentException(message);
    }

    private void expandLiteral(LiteralImpl impl) {
        // SQLリテラルはパラメータ化
        sqlInfo.getSqlBuilder().append("?");
        String value = impl.toString();
        if (value.startsWith("'") && value.endsWith("'")) {
            // 文字列リテラルについては前後のオートを除去.
            value = value.substring(1, value.length() - 1);
        }
        sqlInfo.getSqlParamList().add(value);
    }

    private void expandMember(MemberImpl impl) {
        // そのままSQLのメンバーとする。
        sqlInfo.getSqlBuilder().append(impl.toString());
    }

    private void expandMethod(MethodImpl impl) {
        // CONTAINS
        if (impl.getMethod() == MethodKind.CONTAINS) {
            // h2 database の POSITION は 1 オリジンで発見せずが0 なので 1 を減らしています。
            sqlInfo.getSqlBuilder().append("(POSITION(");
            expand(impl.getParameters().get(1));
            sqlInfo.getSqlBuilder().append(",");
            expand(impl.getParameters().get(0));
            sqlInfo.getSqlBuilder().append(") > 0)");
            return;
        }

        // STARTSWITH
        if (impl.getMethod() == MethodKind.STARTSWITH) {
            // h2 database の POSITION は 1 オリジンで発見せずが0 なので 1 を減らしています。
            sqlInfo.getSqlBuilder().append("(POSITION(");
            expand(impl.getParameters().get(1));
            sqlInfo.getSqlBuilder().append(",");
            expand(impl.getParameters().get(0));
            sqlInfo.getSqlBuilder().append(") = 1)");
            return;
        }

        // ENDSWITH

        // LENGTH

        // INDEXOF
        if (impl.getMethod() == MethodKind.INDEXOF) {
            // h2 database の POSITION は 1 オリジンで発見せずが0 なので 1 を減らしています。
            sqlInfo.getSqlBuilder().append("(POSITION(");
            expand(impl.getParameters().get(1));
            sqlInfo.getSqlBuilder().append(",");
            expand(impl.getParameters().get(0));
            sqlInfo.getSqlBuilder().append(") - 1)");
            return;
        }

        // SUBSTRING
        if (impl.getMethod() == MethodKind.SUBSTRING) {
            sqlInfo.getSqlBuilder().append("(SUBSTRING(");
            expand(impl.getParameters().get(0));
            sqlInfo.getSqlBuilder().append(",");
            expand(impl.getParameters().get(1));
            if (impl.getParameters().size() > 1) {
                sqlInfo.getSqlBuilder().append(",");
                expand(impl.getParameters().get(2));
            }
            sqlInfo.getSqlBuilder().append("))");
            return;
        }
        // $top=20&$filter=(substring(Description,1,2) eq '増殖')

        // TOLOWER
        if (impl.getMethod() == MethodKind.TOLOWER) {
            sqlInfo.getSqlBuilder().append("LOWER(");
            expand(impl.getParameters().get(0));
            sqlInfo.getSqlBuilder().append(")");
            return;
        }
        // チェックのパターン.
        // $top=20&$filter=(substringof(%27poptablet5%27,tolower(Name)))

        // TOUPPER
        if (impl.getMethod() == MethodKind.TOUPPER) {
            sqlInfo.getSqlBuilder().append("UPPER(");
            expand(impl.getParameters().get(0));
            sqlInfo.getSqlBuilder().append(")");
            return;
        }
        // チェックのパターン.
        // $top=20&$filter=(substringof(%27POPTABLET5%27,toupper(Name)))

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
        if (impl.getMethod() == MethodKind.SUBSTRINGOF) {
            sqlInfo.getSqlBuilder().append("(POSITION(");
            expand(impl.getParameters().get(0));
            sqlInfo.getSqlBuilder().append(",");
            expand(impl.getParameters().get(1));
            sqlInfo.getSqlBuilder().append(") > 0)");
            return;
        }

        final String message = "Unexpected Case: NOT SUPPORTED MethodKind:" + impl.getMethod() + "," + impl.toString()
                + "]";
        System.err.println(message);
        throw new IllegalArgumentException(message);
    }

    private void expandUnary(UnaryImpl impl) {
        if (impl.getOperator() == UnaryOperatorKind.NOT) {
            sqlInfo.getSqlBuilder().append("(NOT (");
            expand(impl.getOperand());
            sqlInfo.getSqlBuilder().append("))");
            return;
        } else if (impl.getOperator() == UnaryOperatorKind.MINUS) {
            sqlInfo.getSqlBuilder().append("(-(");
            expand(impl.getOperand());
            sqlInfo.getSqlBuilder().append("))");
            return;
        }

        final String message = "Unexpected Case: Unsupported UnaryOperatorKind:" + impl.getOperator() + ","
                + impl.toString() + "]";
        System.err.println(message);
        throw new IllegalArgumentException(message);
    }
}
