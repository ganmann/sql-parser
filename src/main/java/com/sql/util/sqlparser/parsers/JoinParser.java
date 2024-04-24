package com.sql.util.sqlparser.parsers;


import com.sql.util.sqlparser.constants.SQLConstants;
import com.sql.util.sqlparser.errorHandling.exceptions.SqlValidationException;
import com.sql.util.sqlparser.model.*;
import com.sql.util.sqlparser.model.enums.JoinType;
import com.sql.util.sqlparser.utils.SQLUtils;
import lombok.ToString;

import java.util.regex.Pattern;

@ToString
public class JoinParser extends Parser {

    private JoinType joinType;

    public JoinParser(String statement) {
        tokens = SQLConstants.JOIN_TYPES;
        startElementPointer = 0;
        initiateClauseRelatedFields(statement);
    }

    private void initiateClauseRelatedFields(String statement) {
        String joinPattern = SQLConstants.JOIN_TYPES.stream()
                .filter(token -> SQLUtils.checkTokenMatch.test(statement, token))
                .findFirst().orElse("");
        lengthOfKeyword = joinPattern.length();
        joinType = JoinType.getJoinType(joinPattern);
    }

    @Override
    protected QueryComponent selectQueryComponent(Query query) {
        return query.getJoins();
    }

    @Override
    protected void parseQueryComponentElement(QueryComponent queryComponent, String statement) {
        ((Join) queryComponent).addJoinClause(parseJoinClause(statement));
    }

    private JoinClause parseJoinClause(String statement) {

        initiateClauseRelatedFields(statement);

        JoinClause joinClause = new JoinClause(statement);
        joinClause.setJoinType(joinType);

        statement = statement.substring(lengthOfKeyword).trim();
        Pattern joinOnPattern = Pattern.compile("\\s+on\\s+", Pattern.CASE_INSENSITIVE);
        String[] joinParts = joinOnPattern.split(statement);
        if (joinParts.length == 1) {
            throw new SqlValidationException("Wrong join clause: " + joinClause.getInitialStatement());
        }

        Table table = SQLUtils.parseTable(joinParts[0]);
        joinClause.setTable(table);

        String joinKeys = joinParts[1];

        Predicate predicate = SQLUtils.parsePredicatesGroup(joinKeys);
        joinClause.setJoinKeys(predicate);

        return joinClause;
    }
}
