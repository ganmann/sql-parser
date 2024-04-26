package com.sql.util.sqlparser.utils;

import com.sql.util.sqlparser.constants.SQLConstants;
import com.sql.util.sqlparser.errorHandling.exceptions.SqlValidationException;
import com.sql.util.sqlparser.model.*;
import com.sql.util.sqlparser.parsers.QueryParser;
import lombok.experimental.UtilityClass;


import java.util.Optional;
import java.util.Stack;
import java.util.function.BiPredicate;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class SQLUtils {

    public final BiPredicate<String, String> checkTokenMatch = (string, token) -> {
        string = string.toLowerCase();
        token = token.toLowerCase();
        StringBuilder patternBuilder = new StringBuilder("\\s*");
        for (String patternWord : token.split("\\s+")) {
            patternBuilder.append(patternWord);

            // complex logic - rewrite
            if (token.length() > 1) {
                patternBuilder.append("\\s+");
            }
        }
        patternBuilder.append(".*");
        Pattern pattern = Pattern.compile(patternBuilder.toString(), Pattern.DOTALL);
        Matcher matcher = pattern.matcher(string);

        return matcher.matches();
    };

    public Query parseQuery(String query) {
        QueryParser parser = new QueryParser(query);
        parser.parseHighLevel();
        parser.parseQueryComponents();
        return parser.getQuery();
    }

    // skip constructions like (...)  "..."
    public static int goToCloseCharacter(String statement, int i) {
        Stack<Character> openCharactersStack = new Stack<>();
        char currentChar = statement.charAt(i);
        char lastOpenChar = currentChar;
        openCharactersStack.push(currentChar);
        while (!openCharactersStack.isEmpty() && i < statement.length() - 1) {
            currentChar = statement.charAt(i+1);
            if (!SQLConstants.OPEN_CLOSED_SAME_CHARACTERS.contains(lastOpenChar) && SQLConstants.OPEN_CHARACTERS.contains(currentChar)) {
                openCharactersStack.push(currentChar);
                lastOpenChar = currentChar;
            }
            if (SQLConstants.OPEN_CLOSED_CHARACTER_MAP.get(openCharactersStack.peek()).equals(currentChar)) {
                openCharactersStack.pop();
                lastOpenChar = openCharactersStack.empty() ? Character.MIN_VALUE : openCharactersStack.peek();
            }
            i++;
        }
        return i;
    }

    public Table parseTable(String queryPart) {

        Table table = new Table(queryPart);

        // check nested query
        if (SQLUtils.isNestedQuery(queryPart)) {
            table.setNestedQuery(parseQuery(SQLUtils.substrNestedQuery(queryPart)));
            table.setAlias(SQLUtils.parseAlias(queryPart));
            return table;
        }

        queryPart = queryPart.trim();
        // check 'table'
        if (queryPart.matches("^\\b\\w+\\b$")) {
            table.setTableName(queryPart.trim());
            return table;
        }

        // check 'table t1'
        if (queryPart.matches("^\\b\\w+\\s+\\w+\\b$")) {
            String[] parts = queryPart.split("\\s+");
            table.setTableName(parts[0]);
            table.setAlias(parts[1]);
            return table;
        }

        // check 'table as t1'
        if (queryPart.matches("^\\b\\w+\\s+(as|AS)\\s+\\w+\\b$")) {
            String[] parts = queryPart.split("as|AS");
            table.setTableName(parts[0].trim());
            table.setAlias(parts[1].trim());
            return table;
        }

        return table;
    }

    public Column parseColumn(String queryPart) {

        if (SQLUtils.checkKeywordAll(queryPart)) {
            Column column = new Column();
            column.setColumnName("*");
            return column;
        }

        // check 'column' pattern
        if (queryPart.matches("^[a-zA-Z0-9_]*[a-zA-Z][a-zA-Z0-9_]*$")) {
            Column column = new Column();
            column.setColumnName(queryPart.trim());
            return column;
        }

        // check 'tbl1.column' pattern
        if (queryPart.matches("^\\b\\w+\\.\\w+\\b$")) {
            Column column = new Column();
            String[] parts = queryPart.trim().split("\\.");
            column.setTable(parts[0]);
            column.setColumnName(parts[1]);
            return column;
        }

        return null;
    }

    public boolean isFunction(String queryPart) {
        return queryPart.matches("^\\b\\w+\\(.+\\)$");
    }

    public String substrFunctionName(String queryPart) {
        int openSymbol = queryPart.indexOf("(");
        return queryPart.substring(0, openSymbol);
    }

    public String substrFunctionArguments(String queryPart) {
        return substrNestedQuery(queryPart);
    }

    public boolean isInteger(String queryPart) {
        return Pattern.compile("\\d+").matcher(queryPart).matches();
    }

    public Integer parseInteger(String queryPart) {
        Matcher matcher = Pattern.compile("\\d+").matcher(queryPart);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        }
        throw new NumberFormatException();
    }

    public boolean checkStatementHasSelectFrom(String queryPart) {
        queryPart = queryPart.trim().toLowerCase();
        return queryPart.matches("^\\s*\\bselect\\b[\\s\\S]*?\\bfrom\\b[\\s\\S]+$");
    }

    public boolean checkKeywordAll(String queryPart) {
        queryPart = queryPart.trim().toLowerCase();
        return queryPart.matches("^\\s*\\*\\s*$");
    }

    public boolean isNestedQuery(String queryPart) {
        queryPart = queryPart.trim().toLowerCase();
        return queryPart.matches("^\\s*\\(\\s*select\\b[\\s\\S]*?\\bfrom\\b[\\s\\S]*$");
    }

    public String parseAlias(String queryPart) {
        int closeSymbol = queryPart.lastIndexOf(")");
        return queryPart.substring(closeSymbol + 1).trim();
    }

    public String substrNestedQuery(String queryPart) {
        int openSymbol = queryPart.indexOf("(");
        int closeSymbol = queryPart.lastIndexOf(")");
        return queryPart.substring(openSymbol + 1, closeSymbol);
    }

    public Predicate parsePredicatesGroup(String predicateGroup) {
        int i = 0;
        int pointer = 0;

        char currentChar;

        Predicate predicate;
        PredicateRelation predicateRelation = null;
        Predicate firstPredicate = null;

        while (i < predicateGroup.length()) {
            currentChar = predicateGroup.charAt(i);

            if (Character.isWhitespace(currentChar)) {
                int finalI = i + 1;
                String matchedPattern = SQLConstants.LOGICAL_OPERATORS_TOKENS.stream()
                        .filter(token -> SQLUtils.checkTokenMatch.test(predicateGroup.substring(finalI), token))
                        .findFirst().orElse("");

                if (!matchedPattern.isEmpty()) {
                    predicate = parsePredicate(predicateGroup.substring(pointer, finalI + matchedPattern.length()));
                    if (firstPredicate == null) {
                        firstPredicate = predicate;
                    }
                    if (predicateRelation != null) {
                        predicateRelation.setNextPredicate(predicate);
                    }
                    predicateRelation = predicate.getPredicateRelation();

                    pointer = finalI + matchedPattern.length();
                    i += matchedPattern.length();
                }
            } else if (SQLConstants.OPEN_CHARACTERS.contains(currentChar)) {
                i = SQLUtils.goToCloseCharacter(predicateGroup, i);
            }
            i++;
        }

        predicate = parsePredicate(predicateGroup.substring(pointer));
        if (firstPredicate == null) {
            firstPredicate = predicate;
        }
        if (predicateRelation != null) {
            predicateRelation.setNextPredicate(predicate);
        }

        return firstPredicate;
    }

    private Predicate parsePredicate(String predicateStringWithNextLogicalOperator) {
        predicateStringWithNextLogicalOperator = predicateStringWithNextLogicalOperator.trim();
        String logicalOperator = "";
        String predicateString = predicateStringWithNextLogicalOperator;

        MatchResult logicalOperatorMatchResult = Pattern.compile("(or|and|xor)$", Pattern.CASE_INSENSITIVE)
                .matcher(predicateStringWithNextLogicalOperator).results().findFirst().orElse(null);
        if (logicalOperatorMatchResult != null) {
            logicalOperator = predicateStringWithNextLogicalOperator.substring(logicalOperatorMatchResult.start()).trim();
            predicateString = predicateStringWithNextLogicalOperator.substring(0, logicalOperatorMatchResult.start()).trim();
        }

        Predicate predicate = new Predicate(predicateString);

        if (isNestedPredicate(predicateString)) {
            Predicate nestedPredicate = parsePredicatesGroup(SQLUtils.substrNestedPredicate(predicateString));
            predicate.setNestedPredicate(nestedPredicate);
        } else {
            Pattern comparisonPatterns = Pattern.compile(SQLConstants.REGEX_COMPARISONS, Pattern.CASE_INSENSITIVE);

            MatchResult comparisonPatternResult = comparisonPatterns.matcher(predicateString).results().findFirst().orElse(null);
            if (comparisonPatternResult != null) {
                String comparisonOperator = comparisonPatternResult.group().trim();
                predicate.setComparison(comparisonOperator);
                String[] predicateParts = comparisonPatterns.split(predicateString);
                PredicateOperand leftOperand = parsePredicateOperand(predicateParts[0]);
                predicate.setLeftOperand(leftOperand);
                if (predicateParts.length > 1) {
                    PredicateOperand rightOperand = parsePredicateOperand(predicateParts[1]);
                    predicate.setRightOperand(rightOperand);
                }
            }
        }

        if (!logicalOperator.isEmpty()) {
            predicate.setPredicateRelation(new PredicateRelation(logicalOperator));
        }

        return predicate;
    }

    private boolean isNestedPredicate(String predicate) {
        Matcher matcher = Pattern.compile("^\\(.+\\)\\s+(or|and|xor)$|^\\(.+\\)$", Pattern.CASE_INSENSITIVE).matcher(predicate);
        return matcher.matches();
    }

    public String substrNestedPredicate(String queryPart) {
        return substrNestedQuery(queryPart);
    }

    private PredicateOperand parsePredicateOperand(String operandPart) {
        operandPart = operandPart.trim();
        PredicateOperand predicateOperand = new PredicateOperand(operandPart);

        if (SQLUtils.isFunction(operandPart)) {
            predicateOperand.setFunction(SQLUtils.substrFunctionName(operandPart));
            operandPart = SQLUtils.substrFunctionArguments(operandPart).trim();
        }

        if (SQLUtils.isNestedQuery(operandPart)) {
            predicateOperand.setNestedQuery(SQLUtils.parseQuery(SQLUtils.substrNestedQuery(operandPart)));
        } else  {
            String finalOperandPart = operandPart;
            Optional.ofNullable(SQLUtils.parseColumn(operandPart))
                    .ifPresentOrElse(predicateOperand::setColumn, () -> predicateOperand.setExpression(finalOperandPart));
        }

        return predicateOperand;
    }

}
