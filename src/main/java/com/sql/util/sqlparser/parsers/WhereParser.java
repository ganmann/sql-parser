package com.sql.util.sqlparser.parsers;

import com.sql.util.sqlparser.model.Query;
import com.sql.util.sqlparser.model.QueryComponent;
import com.sql.util.sqlparser.model.WhereClause;
import com.sql.util.sqlparser.utils.SQLUtils;

public class WhereParser extends Parser {

    public WhereParser() {
        lengthOfKeyword = 5;
        startElementPointer = 5;
    }

    @Override
    protected QueryComponent selectQueryComponent(Query query) {
        return query.getWhereClause();
    }

    @Override
    protected void parseQueryComponentElement(QueryComponent queryComponent, String statement) {
        ((WhereClause) queryComponent).setPredicate(SQLUtils.parsePredicatesGroup(statement));
    }
}
