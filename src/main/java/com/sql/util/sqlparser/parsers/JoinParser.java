package com.sql.util.sqlparser.parsers;


import com.sql.util.sqlparser.constants.SQLConstants;
import com.sql.util.sqlparser.model.Join;
import com.sql.util.sqlparser.model.JoinClause;
import com.sql.util.sqlparser.model.Query;
import com.sql.util.sqlparser.model.QueryComponent;
import com.sql.util.sqlparser.utils.SQLUtils;
import lombok.ToString;

@ToString
public class JoinParser extends Parser{

    public JoinParser(Join join) {
        tokens = SQLConstants.JOIN_TYPES;
        lengthOfKeyword = SQLConstants.JOIN_TYPES.stream()
                .filter(token -> SQLUtils.checkTokenMatch.test(join.getInitialStatement(), token))
                .findFirst().orElse("").length();
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
        return new JoinClause(statement);
    }
}
