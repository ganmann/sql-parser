package com.sql.util.sqlparser.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NestedQuery extends QueryComponent {
    private Query query;

    public NestedQuery(String initialStatement) {
        super(initialStatement);
    }

    // Other properties and methods as needed
}
