package com.sql.util.sqlparser.parsers;

import com.sql.util.sqlparser.model.*;
import com.sql.util.sqlparser.model.enums.SortingOrder;
import com.sql.util.sqlparser.utils.SQLUtils;

import java.util.List;

public class OrderByParser extends Parser {

    public OrderByParser() {
        lengthOfKeyword = 8;
        startElementPointer = 8;
        tokens = List.of(",");
    }

    @Override
    protected QueryComponent selectQueryComponent(Query query) {
        return query.getOrderBy();
    }

    @Override
    protected void parseQueryComponentElement(QueryComponent queryComponent, String statement) {
        ((OrderBy) queryComponent).addOrderByColumn(parseOrderByColumn(statement.trim()));
    }

    private OrderByColumn parseOrderByColumn(String statement) {

        OrderByColumn orderByColumn = new OrderByColumn(statement);

        if (statement.toUpperCase().endsWith("DESC")) {
            orderByColumn.setSortingOrder(SortingOrder.DESC);
            statement = substrUntilLastWord(statement, "DESC");
        }

        if (statement.toUpperCase().endsWith("ASC")) {
            statement = substrUntilLastWord(statement,"ASC");
        }

        statement = statement.trim();

        if (SQLUtils.isNestedQuery(statement)) {
            Query query = SQLUtils.parseQuery(SQLUtils.substrNestedQuery(statement));
            orderByColumn.setNestedQuery(query);
        } else {
            if (SQLUtils.isInteger(statement)) {
                orderByColumn.setColumnNumber(SQLUtils.parseInteger(statement));
            } else {
                orderByColumn.setColumn(SQLUtils.parseColumn(statement));
            }
        }

        return orderByColumn;
    }

    String substrUntilLastWord(String statement, String word) {
        return statement.substring(0, statement.toLowerCase().lastIndexOf(word.toLowerCase()));
    }
}
