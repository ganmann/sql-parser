package com.sql.util.sqlparser.model;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SelectExpression extends QueryComponent {

    public SelectExpression(String initialStatement) {
        super(initialStatement);
    }

    private Column column;
    private Query nestedQuery;
    private String alias;
    private boolean isFunction;
    private boolean isLiteral;

    public boolean isNestedQuery() {
        return nestedQuery != null;
    }
}
