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
public class OrderBy extends QueryComponent implements IterableComponent {
    public OrderBy(String initialStatement) {
        super(initialStatement);
    }

    List<OrderByColumn> orderByColumns;

    public void addOrderByColumn(OrderByColumn orderByColumn) {
        if (orderByColumns == null) {
            orderByColumns = new ArrayList<>();
        }

        orderByColumns.add(orderByColumn);
    }


}
