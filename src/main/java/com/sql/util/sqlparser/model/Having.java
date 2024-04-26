package com.sql.util.sqlparser.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Having extends QueryComponent {
    public Having(String initialStatement) {
        super(initialStatement);
    }

    private Predicate predicate;
}
