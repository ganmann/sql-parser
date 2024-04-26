package com.sql.util.sqlparser.model;

import com.sql.util.sqlparser.model.enums.LogicalOperator;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PredicateRelation {

    public PredicateRelation(String logicalOperator) {
        this.logicalOperator = LogicalOperator.getLogicalOperator(logicalOperator);
    }

    LogicalOperator logicalOperator;
    Predicate nextPredicate;
}
