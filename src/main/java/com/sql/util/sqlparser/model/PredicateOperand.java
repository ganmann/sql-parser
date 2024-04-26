package com.sql.util.sqlparser.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PredicateOperand extends QueryComponent {

    public PredicateOperand(String statement) {
        super(statement);
    }

    Column column;
    String expression;
    String function;
    Query nestedQuery;

    public boolean isColumn() {
        return column != null;
    }

    public boolean isExpression() {
        return expression != null;
    }

    public boolean isFunction() {
        return function != null;
    }

    public boolean isNestedQuery() {
        return nestedQuery != null;
    }
}
