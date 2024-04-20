package com.sql.util.sqlparser.utils;

import com.sql.util.sqlparser.model.*;
import com.sql.util.sqlparser.model.factory.AbstractQueryElementFactory;
import com.sql.util.sqlparser.model.factory.QueryElementFactory;

public class QueryBuilder {

    public QueryBuilder(String initialQuery) {
        this.query = new Query(initialQuery);
    }

    private static final AbstractQueryElementFactory queryElementFactory = new QueryElementFactory();

    private final Query query;

    public Query getQuery() {
        return query;
    }


    public void setQueryElement(String statement, String partKey) {

        if (partKey.equals("limit")) {
            query.setLimit(SQLUtils.parseInteger(statement));
            return;
        }
        if (partKey.equals("offset")) {
            query.setOffset(SQLUtils.parseInteger(statement));
            return;
        }
        setQueryElement(queryElementFactory.createQueryElement(partKey, statement));
    }

    public void setQueryElement(QueryComponent queryComponent) {
        if (queryComponent instanceof Select) {
            query.setSelect((Select) queryComponent);
        } else if (queryComponent instanceof From) {
            query.setFrom((From) queryComponent);
        } else if (queryComponent instanceof Join) {
            query.setJoin((Join) queryComponent);
        } else if (queryComponent instanceof WhereClause) {
            query.setWhereClause((WhereClause) queryComponent);
        } else if (queryComponent instanceof Having) {
            query.setHaving((Having) queryComponent);
        } else if (queryComponent instanceof GroupBy) {
            query.setGroupBy((GroupBy) queryComponent);
        } else if (queryComponent instanceof Sort) {
            query.setSortColumns((Sort) queryComponent);
        }
    }

    public static SelectExpression parseColumn(String queryPart) {
        SelectExpression selectExpression = new SelectExpression(queryPart);
        if (SQLUtils.checkSelectFromAll(queryPart)) {
            Column column = new Column();
            column.setColumnName("*");
            selectExpression.setColumn(column);
            return selectExpression;
        }
        if (SQLUtils.isNestedQuery(queryPart)) {
            selectExpression.setNestedQuery(SQLUtils.parseNestedQuery(queryPart));
            selectExpression.setAlias(SQLUtils.parseAlias(queryPart));
            return selectExpression;
        }

        queryPart = queryPart.trim();
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

        // check function
        if (queryPart.matches("\\b\\w+\\([^)].+\\)")) {
            Column column = new Column();
            String[] parts = queryPart.trim().split("\\.");
            column.setTable(parts[0]);
            column.setColumnName(parts[1]);
            selectExpression.setColumn(column);
            return selectExpression;
        }

        // 3. check Literals pattern
        return selectExpression;
    }

    public static Table parseFrom(String queryPart) {
        Table table = new Table(queryPart);

        // check nested query
        if (SQLUtils.isNestedQuery(queryPart)) {
            table.setNestedQuery(SQLUtils.parseNestedQuery(queryPart));
            table.setAlias(SQLUtils.parseAlias(queryPart));
            return table;
        }

        queryPart = queryPart.trim();
        // check 'table'
        if (queryPart.matches("^\\b\\w+\\b$")) {
            table.setTableName(queryPart.trim());
            return table;
        }

        // check 'table t1'
        if (queryPart.matches("^\\b\\w+\\s+\\w+\\b$")) {
            String[] parts = queryPart.split("\\s+");
            table.setTableName(parts[0]);
            table.setAlias(parts[1]);
            return table;
        }

        return table;
    }
}
