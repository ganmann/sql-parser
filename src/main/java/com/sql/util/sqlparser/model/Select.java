package com.sql.util.sqlparser.model;

import java.util.List;

public class Select extends QueryComponent {

    private List<Column> columns;

    public Select(String initialStatement) {
        super(initialStatement);
    }

}
