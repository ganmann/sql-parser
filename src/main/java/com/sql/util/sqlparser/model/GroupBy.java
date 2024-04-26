package com.sql.util.sqlparser.model;

import com.sql.util.sqlparser.parsers.IterableComponent;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class GroupBy extends QueryComponent implements IterableComponent {
    public GroupBy(String initialStatement) {
        super(initialStatement);
    }

    List<Column> columns;
    String function;

    public boolean isFunction() {
        return function != null;
    };

    public void addColumn(Column column) {
        if (columns == null) {
            columns = new ArrayList<>();
        }
        columns.add(column);
    }
}
