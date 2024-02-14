package com.sql.util.sqlparser.service.impl;

import com.sql.util.sqlparser.errorHandling.exceptions.SqlValidationException;
import com.sql.util.sqlparser.model.Query;
import com.sql.util.sqlparser.service.SqlParserService;
import com.sql.util.sqlparser.utils.SQLBuilder;
import com.sql.util.sqlparser.utils.SQLUtils;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SqlParserServiceImpl implements SqlParserService {

    final HashMap<String, List<String>> tokens;
    final HashMap<Character, Character> openClosedCharacter;

    {
        tokens = new HashMap<>();
        tokens.put("select", List.of("from"));
        tokens.put("from", List.of("where", "join", "inner join", "left join", "left outer join", "right join",
                "right outer join", "full join", "full outer join", "group by", "having", "order by", "limit", "offset"));
        tokens.put("join", List.of("where", "join", "inner join", "left join", "left outer join", "right join",
                "right outer join", "full join", "full outer join", "group by", "having", "order by", "limit", "offset"));
        tokens.put("where", List.of("group by", "having", "order by", "limit", "offset"));
        tokens.put("group by", List.of("having", "order by", "limit", "offset"));
        tokens.put("having", List.of("order by", "limit", "offset"));
        tokens.put("order by", List.of("limit", "offset"));
        tokens.put("limit", List.of("offset"));

        openClosedCharacter = new HashMap<>();
        openClosedCharacter.put('(',')');
        openClosedCharacter.put('"','"');
    }


    @Override
    public Query parseSelectStatement(String selectStatement) {

        validateSelectStatement(selectStatement);

        return parseStatement(selectStatement);
    }

    private void validateSelectStatement(String selectStatement) {

        selectStatement = selectStatement.trim().toLowerCase();

        if (!selectStatement.matches("^select[\\s\\S]*?from[\\s\\S]*$")) {
            throw new SqlValidationException();
        }

        if (!selectStatement.startsWith("select")) {
            throw new SqlValidationException();
        }
    }

    protected Query parseStatement(String statement) {

        Query result = new Query();
        String partKey = "select";
        int i = 6;
        int pointer = 0;

        char currentChar;
        Set<Character> openCharacters = openClosedCharacter.keySet();
        Stack<Character> openCharactersStack = new Stack<>();

        while (i < statement.length()) {
            currentChar = statement.charAt(i);

            if (Character.isWhitespace(currentChar)) {
                int finalI = i + 1;
                String matchedPattern = tokens.get(partKey).stream()
                        .filter(token -> SQLUtils.checkTokenMatch.test(statement.substring(finalI), token))
                        .findFirst().orElse("");

                if (!matchedPattern.isEmpty()) {
                    SQLBuilder.setQueryElement(statement.substring(pointer, i), partKey, result);
                    pointer = i + 1;
                    i += matchedPattern.length();
                    partKey = matchedPattern.contains("join") ? "join" : matchedPattern;
                }
            } else if (openCharacters.contains(currentChar)) {
                openCharactersStack.push(currentChar);
            }

            i++;

            // skip constructions like (...)  "..."
            while (!openCharactersStack.isEmpty() && i < statement.length()) {
                currentChar = statement.charAt(i);
                if (openCharacters.contains(currentChar)) {
                    openCharactersStack.push(currentChar);
                }
                if (openClosedCharacter.get(openCharactersStack.peek()).equals(currentChar)) {
                    openCharactersStack.pop();
                }
                i++;
            }
        }

        SQLBuilder.setQueryElement(statement.substring(pointer), partKey, result);

        return result;
    }

}
