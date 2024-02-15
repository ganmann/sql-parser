package com.sql.util.sqlparser.model;

import lombok.ToString;

@ToString
public class Join extends QueryComponent {
    public Join(String initialStatement) {
        super(initialStatement);
    }
}
