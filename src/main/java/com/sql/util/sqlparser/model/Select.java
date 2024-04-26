package com.sql.util.sqlparser.model;

import com.sql.util.sqlparser.parsers.IterableComponent;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
@Getter
public class Select extends QueryComponent implements IterableComponent {

    private List<SelectExpression> selectExpressions;

    public Select(String initialStatement) {
        super(initialStatement);
    }

    public void addSelectExpression(SelectExpression selectExpression) {
        if (selectExpressions == null) {
            selectExpressions = new ArrayList<>();
        }
        selectExpressions.add(selectExpression);
    }

}
