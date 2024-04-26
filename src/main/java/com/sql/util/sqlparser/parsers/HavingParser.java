package com.sql.util.sqlparser.parsers;

import com.sql.util.sqlparser.model.Having;
import com.sql.util.sqlparser.model.Query;
import com.sql.util.sqlparser.model.QueryComponent;
import com.sql.util.sqlparser.utils.SQLUtils;


public class HavingParser extends Parser {

    public HavingParser() {
        lengthOfKeyword = 6;
        startElementPointer = 6;
    }

    @Override
    protected QueryComponent selectQueryComponent(Query query) {
        return query.getHaving();
    }

    @Override
    protected void parseQueryComponentElement(QueryComponent queryComponent, String statement) {
        ((Having) queryComponent).setPredicate(SQLUtils.parsePredicatesGroup(statement));
    }
}
