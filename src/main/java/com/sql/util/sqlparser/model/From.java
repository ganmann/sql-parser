package com.sql.util.sqlparser.model;

import com.sql.util.sqlparser.parsers.IterableComponent;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
@Getter
@Setter
public class From extends QueryComponent implements IterableComponent {

    public From(String initialStatement) {
        super(initialStatement);
    }

    // todo make private

    List<Table> tables;

    public void addTable(Table table) {
        if (tables == null) {
            tables = new ArrayList<>();
        }
        tables.add(table);
    }

}
