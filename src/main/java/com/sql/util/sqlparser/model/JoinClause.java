package com.sql.util.sqlparser.model;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class JoinClause extends QueryComponent{

    public JoinClause(String statement) {
        super(statement);
    }

    String joinType;

    Table table;

    String joinKeys;
}
