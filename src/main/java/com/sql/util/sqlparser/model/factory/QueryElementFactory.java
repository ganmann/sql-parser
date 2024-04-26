package com.sql.util.sqlparser.model.factory;

import com.sql.util.sqlparser.model.*;

public class QueryElementFactory extends AbstractQueryElementFactory {


    @Override
    public QueryComponent createQueryElement(String type, String statement) {

        switch (type) {
            case "select":
                return new Select(statement);
            case "from":
                return new From(statement);
            case "join":
                return new Join(statement);
            case "where":
                return new WhereClause(statement);
            case "group by":
                return new GroupBy(statement);
            case "having":
                return new Having(statement);
            case "order by":
                return new OrderBy(statement);
            case "limit":

            case "offset":
        }

        return null;
    }
}
