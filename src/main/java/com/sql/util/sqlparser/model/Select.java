package com.sql.util.sqlparser.model;

import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
public class Select extends QueryComponent {

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

    public List<SelectExpression> getSelectExpressions() {
        return selectExpressions;
    }

}
