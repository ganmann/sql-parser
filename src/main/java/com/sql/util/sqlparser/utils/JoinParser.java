package com.sql.util.sqlparser.utils;


import com.sql.util.sqlparser.model.Query;
import com.sql.util.sqlparser.model.QueryComponent;

public class JoinParser extends Parser{
    @Override
    protected QueryComponent selectQueryComponent(Query query) {
        return query.getJoin();
    }

    @Override
    protected void parseQueryComponentElement(QueryComponent queryComponent, String statement) {

    }
}
