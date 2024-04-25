package com.sql.util.sqlparser.parsers;

import com.sql.util.sqlparser.model.From;
import com.sql.util.sqlparser.model.Query;
import com.sql.util.sqlparser.model.QueryComponent;
import com.sql.util.sqlparser.utils.SQLUtils;

import java.util.List;

public class FromParser extends Parser {

    public FromParser() {
        lengthOfKeyword = 4;
        startElementPointer = 4;
        tokens = List.of(",");
    }

    @Override
    protected QueryComponent selectQueryComponent(Query query) {
        return query.getFrom();
    }

    @Override
    protected void parseQueryComponentElement(QueryComponent queryComponent, String statement) {
        ((From) queryComponent).addTable(SQLUtils.parseTable(statement));
    }

}
