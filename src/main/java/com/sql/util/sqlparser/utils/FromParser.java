package com.sql.util.sqlparser.utils;

import com.sql.util.sqlparser.model.From;
import com.sql.util.sqlparser.model.Query;
import com.sql.util.sqlparser.model.QueryComponent;
import com.sql.util.sqlparser.model.Table;

public class FromParser extends Parser {

    public FromParser() {
        lengthOfKeyword = 4;
    }

    @Override
    protected QueryComponent selectQueryComponent(Query query) {
        return query.getFrom();
    }

    @Override
    protected void parseQueryComponentElement(QueryComponent queryComponent, String statement) {
        ((From) queryComponent).addTable(parseFrom(statement));
    }

    public Table parseFrom(String queryPart) {
        Table table = new Table(queryPart);

        // check nested query
        if (SQLUtils.isNestedQuery(queryPart)) {
            table.setNestedQuery(parseNestedQuery(SQLUtils.substrNestedQuery(queryPart)));
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

        // check 'table as t1'
        if (queryPart.matches("^\\b\\w+\\s+(as|AS)\\s+\\w+\\b$")) {
            String[] parts = queryPart.split("as|AS");
            table.setTableName(parts[0].trim());
            table.setAlias(parts[1].trim());
            return table;
        }

        return table;
    }

}
