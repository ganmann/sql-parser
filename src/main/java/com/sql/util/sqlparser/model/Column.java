package com.sql.util.sqlparser.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Column extends QueryComponent {

    public Column(String initialStatement) {
        super(initialStatement);
    }

    private String table;
    private String columnName;

}
