package com.jane.texttosql.validation;

import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.util.TablesNamesFinder;

import java.util.HashSet;
import java.util.Set;

public class SqlAstUtils {


    public static Long extractLimit(Select select) {

        Select body = select.getSelectBody();

        if (body instanceof PlainSelect plainSelect) {
            Limit limit = plainSelect.getLimit();
            if (limit != null && limit.getRowCount() != null) {
                return Long.parseLong(limit.getRowCount().toString());
            }
        }

        return null;
    }

    public static Set<String> extractTables(Select select) {
        TablesNamesFinder finder = new TablesNamesFinder();
        return new HashSet<>(finder.getTableList((Statement) select));
    }

    public static Set<String> extractColumns(Select select) {
        Set<String> columns = new HashSet<>();

        SelectVisitorAdapter<Void> selectVisitor =
                new SelectVisitorAdapter<>() {

                    @Override
                    public <S> Void visit(PlainSelect plainSelect, S context) {

                        for (SelectItem<?> item : plainSelect.getSelectItems()) {

                            item.getExpression().accept(
                                    new ExpressionVisitorAdapter<Void>() {

                                        @Override
                                        public <S2> Void visit(
                                                Column column,
                                                S2 context
                                        ) {
                                            columns.add(column.getColumnName());
                                            return null;
                                        }
                                    },
                                    null
                            );
                        }

                        return null;
                    }
                };

        select.getSelectBody().accept(selectVisitor, null);

        return columns;
    }
}
