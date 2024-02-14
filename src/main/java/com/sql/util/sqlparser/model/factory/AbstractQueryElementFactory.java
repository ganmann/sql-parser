package com.sql.util.sqlparser.model.factory;

import com.sql.util.sqlparser.model.QueryComponent;

public abstract class AbstractQueryElementFactory {

    public abstract QueryComponent createQueryElement(String type, String statement);
}
