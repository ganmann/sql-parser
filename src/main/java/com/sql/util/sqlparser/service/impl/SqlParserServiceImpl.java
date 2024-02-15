package com.sql.util.sqlparser.service.impl;

import com.sql.util.sqlparser.errorHandling.exceptions.SqlValidationException;
import com.sql.util.sqlparser.model.Column;
import com.sql.util.sqlparser.model.Query;
import com.sql.util.sqlparser.model.Select;
import com.sql.util.sqlparser.service.SqlParserService;
import com.sql.util.sqlparser.utils.QueryBuilder;
import com.sql.util.sqlparser.utils.SQLUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SqlParserServiceImpl implements SqlParserService {

    final Map<String, List<String>> SELECT_STATEMENT_TOKENS = Map.of(
            "select", List.of("from"),
            "from", List.of("where", "join", "inner join", "left join", "left outer join", "right join",
                    "right outer join", "full join", "full outer join", "group by", "having", "order by", "limit", "offset"),
            "join", List.of("where", "join", "inner join", "left join", "left outer join", "right join",
                    "right outer join", "full join", "full outer join", "group by", "having", "order by", "limit", "offset"),
            "where", List.of("group by", "having", "order by", "limit", "offset"),
            "group by", List.of("having", "order by", "limit", "offset"),
            "having", List.of("order by", "limit", "offset"),
            "order by", List.of("limit", "offset"),
            "limit", List.of("offset")
    );
    private static final Map<Character, Character> OPEN_CLOSED_CHARACTER = Map.of(
            '(', ')',
            '"', '"'
    );

    private static final Set<Character> OPEN_CHARACTERS = OPEN_CLOSED_CHARACTER.keySet();


    @Override
    public Query parseSelectStatement(@NonNull String selectStatement) {

        validateSelectStatement(selectStatement);

        return parse(selectStatement);
    }

    private void validateSelectStatement(String  selectStatement) {

        if (!SQLUtils.checkStatementHasSelectFrom(selectStatement)) {
            throw new SqlValidationException();
        }
    }

    protected Query parse(String statement) {

        QueryBuilder queryBuilder = new QueryBuilder(statement);

        parseHighLevel(statement, queryBuilder);

        parseStatementComponents(queryBuilder);

        return queryBuilder.getQuery();
    }

    private void parseHighLevel(String statement, QueryBuilder queryBuilder) {
        String partKey = "select";
        int i = 6;
        int pointer = 0;

        char currentChar;

        while (i < statement.length()) {
            currentChar = statement.charAt(i);

            if (Character.isWhitespace(currentChar)) {
                int finalI = i + 1;
                String matchedPattern = SELECT_STATEMENT_TOKENS.get(partKey).stream()
                        .filter(token -> SQLUtils.checkTokenMatch.test(statement.substring(finalI), token))
                        .findFirst().orElse("");

                if (!matchedPattern.isEmpty()) {
                    queryBuilder.setQueryElement(statement.substring(pointer, i), partKey);
                    pointer = i + 1;
                    i += matchedPattern.length();
                    partKey = matchedPattern.contains("join") ? "join" : matchedPattern;
                }
            } else if (OPEN_CHARACTERS.contains(currentChar)) {
                i = goToCloseCharacter(statement, i);
            }
            i++;
        }
        queryBuilder.setQueryElement(statement.substring(pointer), partKey);
    }

    // skip constructions like (...)  "..."
    private static int goToCloseCharacter(String statement, int i) {
        Stack<Character> openCharactersStack = new Stack<>();
        char currentChar = statement.charAt(i);
        openCharactersStack.push(currentChar);
        while (openCharactersStack.size() > 0 && i < statement.length() - 1) {
            currentChar = statement.charAt(i+1);
            if (OPEN_CHARACTERS.contains(currentChar)) {
                openCharactersStack.push(currentChar);
            }
            if (OPEN_CLOSED_CHARACTER.get(openCharactersStack.peek()).equals(currentChar)) {
                openCharactersStack.pop();
            }
            i++;
        }
        return i;
    }

    private void parseStatementComponents(QueryBuilder queryBuilder) {
        parseSelect(queryBuilder.getQuery());
        // parseFromClause
        // parseJoinClauses
        // parseWhereClause
        // parseGroupClause
        // parseHavingClause
        // parseOrderClause
    }

    private void parseSelect(Query query) {

        if (query.getSelect() == null || query.getSelect().getInitialStatement() == null || query.getSelect().getInitialStatement().isEmpty()) {
            return;
        }

        Select select = query.getSelect();
        String statement = select.getInitialStatement().trim();

        int i = 6;
        int pointer = i;
        char currentChar;

        while (i < statement.length()) {
            currentChar = statement.charAt(i);

            if (currentChar == ',') {
                parseColumnAndAddToSelect(select, statement.substring(pointer, i));
                pointer = i + 1;
            } else if (OPEN_CHARACTERS.contains(currentChar)) {
                i = goToCloseCharacter(statement, i);
            }
            i++;
        }
        parseColumnAndAddToSelect(select, statement.substring(pointer, i));
    }

    private void parseColumnAndAddToSelect(Select select, String columnStatement) {
        Column column = QueryBuilder.parseColumn(columnStatement);
        if (column.isNestedQuery()) {
            Query nestedQuery = parse(column.getNestedQuery().getInitialStatement());
            column.getNestedQuery().setQuery(nestedQuery);
        }
        select.addColumn(column);
    }

}
