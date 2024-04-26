package com.sql.util.sqlparser.parsers;

import com.sql.util.sqlparser.model.*;
import com.sql.util.sqlparser.utils.SQLUtils;

import java.util.List;

public class SelectParser extends Parser {

    public SelectParser() {
        lengthOfKeyword = 6;
        startElementPointer = 6;
        tokens = List.of(",");
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

        if (SQLUtils.isNestedQuery(queryPart)) {
            selectExpression.setNestedQuery(SQLUtils.parseQuery(SQLUtils.substrNestedQuery(queryPart)));
            selectExpression.setAlias(SQLUtils.parseAlias(queryPart));
            return selectExpression;
        }

        queryPart = queryPart.trim();

        // check function
        if (SQLUtils.isFunction(queryPart)) {
            selectExpression.setFunction(true);
            return selectExpression;
        }

        // check Literals pattern (Characters and Exact Numbers)
        if (queryPart.matches("^[0-9|.+-]*$") || queryPart.matches("^'[^']*'$") || queryPart.matches("^\"[^\"]*\"$")) {
            selectExpression.setLiteral(true);
            return selectExpression;
        }

        Column column = SQLUtils.parseColumn(queryPart);

        if (column != null) {
            selectExpression.setColumn(column);
        }

        return selectExpression;
    }
}
