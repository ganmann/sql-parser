package com.sql.util.sqlparser.model;

import lombok.ToString;

@ToString
public class Sort extends QueryComponent {
    public Sort(String initialStatement) {
        super(initialStatement);
    }
}
