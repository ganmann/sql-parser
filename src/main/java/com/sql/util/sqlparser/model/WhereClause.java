package com.sql.util.sqlparser.model;

import lombok.ToString;

@ToString
public class WhereClause extends QueryComponent {
    public WhereClause(String initialStatement) {
        super(initialStatement);
    }
}
