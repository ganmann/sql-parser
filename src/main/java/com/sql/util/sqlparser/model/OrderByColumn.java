package com.sql.util.sqlparser.model;


import com.sql.util.sqlparser.model.enums.SortingOrder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OrderByColumn extends QueryComponent {
    public OrderByColumn(String statement) {
        super(statement);
        this.sortingOrder = SortingOrder.ASC;
    }

    Column column;
    Integer columnNumber;
    SortingOrder sortingOrder;
    Query nestedQuery;

    public boolean isColumnNumber() {
        return columnNumber != null;
    }

    public boolean isNestedQuery() {
        return nestedQuery != null;
    }
}
