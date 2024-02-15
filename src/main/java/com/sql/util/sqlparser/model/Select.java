package com.sql.util.sqlparser.model;

import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
public class Select extends QueryComponent {

    private List<Column> columns;

    public Select(String initialStatement) {
        super(initialStatement);
    }

    public void addColumn(Column column) {
        if (columns == null) {
            columns = new ArrayList<Column>();
        }
        columns.add(column);
    }

    public List<Column> getColumns() {
        return columns;
    }

}
