package com.sql.util.sqlparser.utils;

import com.sql.util.sqlparser.model.*;
import com.sql.util.sqlparser.model.factory.AbstractQueryElementFactory;
import com.sql.util.sqlparser.model.factory.QueryElementFactory;

public class QueryBuilder {

    public QueryBuilder(String initialQuery) {
        this.query = new Query(initialQuery);
    }

    private static final AbstractQueryElementFactory queryElementFactory = new QueryElementFactory();

    private Query query;

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

    public static Column parseColumn(String queryPart) {
        Column column = new Column(queryPart);
        if (SQLUtils.checkSelectFromAll(queryPart)) {
            column.setColumnName("*");
            return column;
        }
        if (SQLUtils.isNestedQuery(queryPart)) {
            int openSymbol = queryPart.indexOf("(");
            int closeSymbol = queryPart.lastIndexOf(")");
            NestedQuery nestedQuery = new NestedQuery(queryPart.substring(openSymbol + 1, closeSymbol));
            column.setNestedQuery(nestedQuery);
            column.setAlias(queryPart.substring(closeSymbol + 1).trim());
            return column;
        }
        if (queryPart.trim().matches("^\\b\\w+\\b$")) {
            column.setColumnName(queryPart.trim());
            return column;
        }

        // todo
        // 1. check tbl.column pattern
        // 2. check SUM|AVG|COUNT|MIN|MAX patterns
        // 3. check Literals pattern
        return column;
    }
}
