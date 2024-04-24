package com.sql.util.sqlparser.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PredicateRelation {

    public PredicateRelation(String logicalOperator) {
        this.logicalOperator = logicalOperator;
    }

    String logicalOperator;
    Predicate predicate;
}
