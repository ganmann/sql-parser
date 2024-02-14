package com.sql.util.sqlparser.service.impl;

import com.sql.util.sqlparser.errorHandling.exceptions.SqlValidationException;
import com.sql.util.sqlparser.model.Query;
import com.sql.util.sqlparser.service.SqlParserService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SqlParserServiceImpl implements SqlParserService {

    final HashMap<String, List<String>> tokens;
    final HashMap<Character, Character> openClosedCharacter;

    {
        tokens = new HashMap<>();
        tokens.put("select", List.of("from"));
        tokens.put("from", List.of("where", "join", "inner join", "left join", "left outer join", "right join",
                "right outer join", "full join", "full outer join", "group", "having", "order", "limit", "offset"));
        tokens.put("join", List.of("where", "join", "inner join", "left join", "left outer join", "right join",
                "right outer join", "full join", "full outer join", "group", "having", "order", "limit", "offset"));
        tokens.put("where", List.of("group", "having", "order", "limit", "offset"));
        tokens.put("group", List.of("having", "order", "limit", "offset"));
        tokens.put("having", List.of("order", "limit", "offset"));
        tokens.put("order", List.of("limit", "offset"));
        tokens.put("limit", List.of("offset"));

        openClosedCharacter = new HashMap<>();
        openClosedCharacter.put('(',')');
        openClosedCharacter.put('"','"');
    }

    @Override
    public Query parseSelectStatement(String selectStatement) {

        validateSelectStatement(selectStatement);

        Query query = parse(selectStatement);

        return query;
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

    private Query parse(String selectStatement) {

        List<String> parts =  divideStatement(selectStatement);

        Query result = new Query();

        String selectPart = parts.get(0);
        String fromPart = parts.get(1);

        result.setColumns(Arrays.stream(selectPart.split(",")).map(String::trim).toList());
        result.setFromSources(Arrays.stream(fromPart.split(",")).map(String::trim).toList());

        return result;
    }

    protected List<String> divideStatement(String statement) {

//        BiPredicate<String, String> checkTokenMatch = (string, patternString) -> {
////
////            String regex = "^\\border\\s*by\\b";
////            StringBuilder patternBuilder = new StringBuilder("\\s*\\b");
////            for (String patternWord : patternString.split("\\S")) {
////                patternBuilder.append(patternWord)
////            }
////            Pattern pattern = Pattern.compile(regex);
////            Matcher matcher = pattern.matcher(input);
////
////            return matcher.matches();
////        };

        List<String> parts = new ArrayList<>();
        String partKey = "select";
        int i = 6;
        int pointer = 0;

        char currentChar;
        Set<Character> openCharacters = openClosedCharacter.keySet();

        while (i < statement.length()) {
            currentChar = statement.charAt(i);
            if (Character.isWhitespace(currentChar)) {
                int leftSize = statement.length() - i - 1;
                int finalI = i + 1;
                String matchedPattern = tokens.get(partKey).stream()
                        .filter(token -> token.equals(statement.substring(finalI, finalI + Math.min(token.length(), leftSize)).toLowerCase()))
//                        .filter(token -> checkTokenMatch.test(statement.substring(finalI), token))
                        .findFirst().orElse("");

                if (!matchedPattern.isEmpty()) {
                    parts.add(statement.substring(pointer, i));
                    pointer = i + 1;
                    i += matchedPattern.length();
                    partKey = matchedPattern.contains("join") ? "join" : matchedPattern;
                }
            }

            if (openCharacters.contains(currentChar)) {
                do {
                    i++;
                } while (i < statement.length() && !openClosedCharacter.get(currentChar).equals(statement.charAt(i)));
            }

            i++;
        }

        parts.add(statement.substring(pointer));

        return parts;
    }

}
