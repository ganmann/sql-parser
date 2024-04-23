package com.sql.util.sqlparser.parsers;

import com.sql.util.sqlparser.constants.SQLConstants;
import com.sql.util.sqlparser.model.Query;
import com.sql.util.sqlparser.model.QueryComponent;
import com.sql.util.sqlparser.utils.SQLUtils;


import java.util.List;
import java.util.stream.Stream;

public abstract class Parser {

    int lengthOfKeyword;

    List<String> tokens;

    protected abstract QueryComponent selectQueryComponent(Query query);

    protected abstract void parseQueryComponentElement(QueryComponent queryComponent, String statement);

    final void parse(Query query) {
        QueryComponent queryComponent = selectQueryComponent(query);

        if (queryComponent == null || queryComponent.getInitialStatement() == null || queryComponent.getInitialStatement().isEmpty()) {
            return;
        }

        String statement = queryComponent.getInitialStatement().trim();

        int i = lengthOfKeyword;

        // todo refactor bad Dependency Inversion
        int pointer = this instanceof JoinParser ? 0 : i ;
        char currentChar;

        while (i < statement.length()) {

            currentChar = statement.charAt(i);
            int finalI = i;
            String matchedPattern = tokens.stream()
                    .filter(token -> SQLUtils.checkTokenMatch.test(statement.substring(finalI), token))
                    .findFirst().orElse("");

            if (!matchedPattern.isEmpty()) {
                parseQueryComponentElement(queryComponent, statement.substring(pointer, i));
                pointer = i + 1;
                i += matchedPattern.length();
            } else if (SQLConstants.OPEN_CHARACTERS.contains(currentChar)) {
                i = SQLUtils.goToCloseCharacter(statement, i);
            }
            i++;
        }
        parseQueryComponentElement(queryComponent, statement.substring(pointer, i));
    }

    final Query parseNestedQuery(String nestedQuery) {
        QueryParser parser = new QueryParser(nestedQuery);
        parser.parseHighLevel();
        parser.parseQueryComponents();
        return parser.getQuery();
    }
}
