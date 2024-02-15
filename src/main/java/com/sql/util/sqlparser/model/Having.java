package com.sql.util.sqlparser.model;

import lombok.ToString;

@ToString
public class Having extends QueryComponent {
    public Having(String initialStatement) {
        super(initialStatement);
    }
}
