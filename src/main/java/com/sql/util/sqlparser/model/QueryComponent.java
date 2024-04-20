package com.sql.util.sqlparser.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public abstract class QueryComponent {
    String initialStatement;

    public QueryComponent(String initialStatement) {
        this.initialStatement = initialStatement;
    }
}
