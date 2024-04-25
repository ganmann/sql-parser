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
public class Join extends QueryComponent implements IterableComponent {

    public Join(String initialStatement) {
        super(initialStatement);
    }

    List<JoinClause> joinClauses;

    public void addJoinClause(JoinClause joinClause) {
        if (joinClauses == null) {
            joinClauses = new ArrayList<>();
        }
        joinClauses.add(joinClause);
    }
}
