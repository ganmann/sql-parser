package com.sql.util.sqlparser.utils;

import com.sql.util.sqlparser.model.Query;
import com.sql.util.sqlparser.model.factory.AbstractQueryElementFactory;
import com.sql.util.sqlparser.model.factory.QueryElementFactory;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SQLBuilder {

    public AbstractQueryElementFactory queryElementFactory = new QueryElementFactory();

    public void setQueryElement(String statement, String partKey, Query result) {

        if (partKey.equals("limit")) {
            result.setLimit(SQLUtils.parseInteger(statement));
            return;
        }
        if (partKey.equals("offset")) {
            result.setOffset(SQLUtils.parseInteger(statement));
            return;
        }
        result.setQueryElement(queryElementFactory.createQueryElement(partKey, statement));
    }
}
