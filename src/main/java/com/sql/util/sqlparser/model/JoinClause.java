package com.sql.util.sqlparser.model;

import com.sql.util.sqlparser.model.enums.JoinType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class JoinClause extends QueryComponent{

    public JoinClause(String statement) {
        super(statement);
    }

    JoinType joinType;

    Table table;

    Predicate joinKeys;
}
