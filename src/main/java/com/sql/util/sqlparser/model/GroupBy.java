package com.sql.util.sqlparser.model;

import lombok.ToString;

@ToString
public class GroupBy extends QueryComponent {
    public GroupBy(String initialStatement) {
        super(initialStatement);
    }
}
