package com.sql.util.sqlparser.utils;

import com.sql.util.sqlparser.constants.SQLConstants;
import com.sql.util.sqlparser.model.*;
import com.sql.util.sqlparser.model.factory.AbstractQueryElementFactory;
import com.sql.util.sqlparser.model.factory.QueryElementFactory;

import java.util.ArrayList;
import java.util.List;

public class QueryParser {

    public QueryParser(String initialQuery) {
        this.query = new Query(initialQuery);
    }

    private static final AbstractQueryElementFactory queryElementFactory = new QueryElementFactory();

    private final Query query;

    public Query getQuery() {
        return query;
    }

    private final List<Parser> subpartParsers = new ArrayList<>();


    public void setQueryElement(String statement, String partKey) {

        if (partKey.equals("limit")) {
            query.setLimit(SQLUtils.parseInteger(statement));
            return;
        }
        if (partKey.equals("offset")) {
            query.setOffset(SQLUtils.parseInteger(statement));
            return;
        }
        setQueryElement(queryElementFactory.createQueryElement(partKey, statement));
    }

    private void setQueryElement(QueryComponent queryComponent) {
        switch (queryComponent) {
            case Select select -> {
                query.setSelect(select);
                subpartParsers.add(new SelectParser());
            }
            case From from -> {
                query.setFrom(from);
                subpartParsers.add(new FromParser());
            }
            case Join join -> {
                query.setJoin(join);
                subpartParsers.add(new JoinParser());
            }
            case WhereClause whereClause -> {
                query.setWhereClause(whereClause);
            }
            case Having having -> {
                query.setHaving(having);
            }
            case GroupBy groupBy -> {
                query.setGroupBy(groupBy);
            }
            case Sort sort -> {
                query.setSortColumns(sort);
            }
            default -> {
            }
        }
    }

    public void parseHighLevel() {

        String statement = query.getInitialStatement();
        String partKey = "select";
        int i = 6;
        int pointer = 0;

        char currentChar;

        while (i < statement.length()) {
            currentChar = statement.charAt(i);

            if (Character.isWhitespace(currentChar)) {
                int finalI = i + 1;
                String matchedPattern = SQLConstants.SELECT_STATEMENT_TOKENS.get(partKey).stream()
                        .filter(token -> SQLUtils.checkTokenMatch.test(statement.substring(finalI), token))
                        .findFirst().orElse("");

                if (!matchedPattern.isEmpty()) {
                    setQueryElement(statement.substring(pointer, i), partKey);
                    pointer = i + 1;
                    i += matchedPattern.length();
                    partKey = matchedPattern.contains("join") ? "join" : matchedPattern;
                }
            } else if (SQLConstants.OPEN_CHARACTERS.contains(currentChar)) {
                i = SQLUtils.goToCloseCharacter(statement, i);
            }
            i++;
        }

        int endIndex = statement.stripTrailing().endsWith(";") ? statement.stripTrailing().length() - 1 : statement.stripTrailing().length();
        setQueryElement(statement.substring(pointer, endIndex), partKey);
    }

    public void parseQueryComponents() {
        for (Parser parser : subpartParsers) {
            parser.parse(query);
        }
    }

}
