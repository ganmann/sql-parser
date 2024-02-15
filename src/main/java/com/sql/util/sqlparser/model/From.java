package com.sql.util.sqlparser.model;

import lombok.ToString;

@ToString
public class From extends QueryComponent {

    public From(String initialStatement) {
        super(initialStatement);
    }

}
