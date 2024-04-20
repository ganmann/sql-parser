package com.sql.util.sqlparser.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
@Getter
@Setter
public class From extends QueryComponent {

    public From(String initialStatement) {
        super(initialStatement);
    }

    List<Table> tables;

    public void addTable(Table table) {
        if (tables == null) {
            tables = new ArrayList<>();
        }
        tables.add(table);
    }

}
