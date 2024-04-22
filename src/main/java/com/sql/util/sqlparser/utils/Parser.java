package com.sql.util.sqlparser.utils;

import com.sql.util.sqlparser.constants.SQLConstants;
import com.sql.util.sqlparser.model.Query;
import com.sql.util.sqlparser.model.QueryComponent;

public abstract class Parser {

    int lengthOfKeyword;

    protected abstract QueryComponent selectQueryComponent(Query query);

    protected abstract void parseQueryComponentElement(QueryComponent queryComponent, String statement);

    final void parse(Query query) {
        QueryComponent queryComponent = selectQueryComponent(query);

        if (queryComponent == null || queryComponent.getInitialStatement() == null || queryComponent.getInitialStatement().isEmpty()) {
            return;
        }

        String statement = queryComponent.getInitialStatement().trim();

        int i = lengthOfKeyword;
        int pointer = i;
        char currentChar;

        while (i < statement.length()) {
            currentChar = statement.charAt(i);

            if (currentChar == ',') {
                parseQueryComponentElement(queryComponent, statement.substring(pointer, i));
                pointer = i + 1;
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
