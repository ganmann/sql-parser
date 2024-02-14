package com.sql.util.sqlparser.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class QueryComponent {
    String initialStatement;

    public QueryComponent(String initialStatement) {
        this.initialStatement = initialStatement;
    }
}
