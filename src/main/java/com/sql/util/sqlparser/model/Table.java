package com.sql.util.sqlparser.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Table extends QueryComponent{

    public Table(String initialStatement) {
        super(initialStatement);
    }

    private String tableName;
    private String alias;
    private Query nestedQuery;

    public boolean isNestedQuery() {
        return nestedQuery != null;
    }
}
