package com.sql.util.sqlparser.parsers;

import com.sql.util.sqlparser.model.Column;
import com.sql.util.sqlparser.model.GroupBy;
import com.sql.util.sqlparser.model.Query;
import com.sql.util.sqlparser.model.QueryComponent;
import com.sql.util.sqlparser.utils.SQLUtils;

import java.util.List;
import java.util.Optional;

public class GroupByParser extends Parser {

    public GroupByParser() {
        lengthOfKeyword = 8;
        startElementPointer = 8;
        tokens = List.of(",");
    }

    @Override
    protected QueryComponent selectQueryComponent(Query query) {
        return query.getGroupBy();
    }

    @Override
    protected void parseQueryComponentElement(QueryComponent queryComponent, String statement) {
        GroupBy groupBy = (GroupBy) queryComponent;
        statement = statement.trim();

        if (SQLUtils.isFunction(statement)) {
            groupBy.setFunction(SQLUtils.substrFunctionName(statement));
            String[] columns = SQLUtils.substrFunctionArguments(statement).trim().split(",");
            for (String columnPart: columns) {
                Optional.ofNullable(SQLUtils.parseColumn(columnPart.trim())).ifPresent(groupBy::addColumn);
            }
        } else {
            Optional.ofNullable(SQLUtils.parseColumn(statement)).ifPresent(groupBy::addColumn);
        }
    }
}
