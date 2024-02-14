package com.sql.util.sqlparser.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Column extends QueryComponent {

    public Column(String initialStatement) {
        super(initialStatement);
    }

    private String table;
    private String column;
    private String alias;
    private NestedQuery nestedQuery;

    public boolean isNestedQuery() {
        return nestedQuery != null;
    }
}
