package com.sql.util.sqlparser.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Predicate extends QueryComponent {

    public Predicate(String statement) {
        super(statement);
    }

    PredicateOperand leftOperand;

    PredicateOperand rightOperand;

    String comparison;

    Predicate nestedPredicate;

    PredicateRelation predicateRelation;

    public boolean isNestedPredicate() {
        return nestedPredicate != null;
    }

    public boolean hasNextPredicate() {
        return predicateRelation != null;
    }

}
