package com.sql.util.sqlparser.utils;

import com.sql.util.sqlparser.model.*;

public class SelectParser extends Parser {

    public SelectParser() {
        lengthOfKeyword = 6;
    }

    @Override
    protected QueryComponent selectQueryComponent(Query query) {
        return query.getSelect();
    }

    @Override
    protected void parseQueryComponentElement(QueryComponent queryComponent, String statement) {
        ((Select) queryComponent).addSelectExpression(parseColumn(statement));
    }

    public SelectExpression parseColumn(String queryPart) {
        SelectExpression selectExpression = new SelectExpression(queryPart);
        if (SQLUtils.checkSelectFromAll(queryPart)) {
            Column column = new Column();
            column.setColumnName("*");
            selectExpression.setColumn(column);
            return selectExpression;
        }
        if (SQLUtils.isNestedQuery(queryPart)) {
            selectExpression.setNestedQuery(parseNestedQuery(SQLUtils.substrNestedQuery(queryPart)));
            selectExpression.setAlias(SQLUtils.parseAlias(queryPart));
            return selectExpression;
        }

        queryPart = queryPart.trim();

        // check function
        if (queryPart.matches("^\\b\\w+\\(.\\)$")) {
            // todo parse inner column
            selectExpression.setFunction(true);
            return selectExpression;
        }

        // check Literals pattern (Characters and Exact Numbers)
        if (queryPart.matches("^[0-9|.+-]*$") || queryPart.matches("^'[^']*'$") || queryPart.matches("^\"[^\"]*\"$")) {
            selectExpression.setLiteral(true);
            return selectExpression;
        }

        // check 'column' pattern
        if (queryPart.matches("^\\b\\w+\\b$")) {
            Column column = new Column();
            column.setColumnName(queryPart.trim());
            selectExpression.setColumn(column);
            return selectExpression;
        }

        // check 'tbl1.column' pattern
        if (queryPart.matches("^\\b\\w+\\.\\w+\\b$")) {
            Column column = new Column();
            String[] parts = queryPart.trim().split("\\.");
            column.setTable(parts[0]);
            column.setColumnName(parts[1]);
            selectExpression.setColumn(column);
            return selectExpression;
        }


        return selectExpression;
    }
}
