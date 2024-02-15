package com.sql.util.sqlparser.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Column extends QueryComponent {

    public Column(String initialStatement) {
        super(initialStatement);
    }

    private String table;
    private String columnName;
    private String alias;
    private NestedQuery nestedQuery;

    public boolean isNestedQuery() {
        return nestedQuery != null;
    }
}
