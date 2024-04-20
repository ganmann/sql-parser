package com.sql.util.sqlparser.utils;

import com.sql.util.sqlparser.errorHandling.exceptions.SqlValidationException;
import com.sql.util.sqlparser.model.Query;
import lombok.experimental.UtilityClass;


import java.util.function.BiPredicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class SQLUtils {

    public final BiPredicate<String, String> checkTokenMatch = (string, token) -> {
        string = string.toLowerCase();
        token = token.toLowerCase();
        StringBuilder patternBuilder = new StringBuilder("\\s*");
        for (String patternWord : token.split("\\s+")) {
            patternBuilder.append(patternWord).append("\\s+");
        }
        patternBuilder.append(".*");
        Pattern pattern = Pattern.compile(patternBuilder.toString(), Pattern.DOTALL);
        Matcher matcher = pattern.matcher(string);

        return matcher.matches();
    };

    public Integer parseInteger(String queryPart) {
        Matcher matcher = Pattern.compile("\\d+").matcher(queryPart);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        }
        throw new SqlValidationException();
    }

    public boolean checkStatementHasSelectFrom(String queryPart) {
        queryPart = queryPart.trim().toLowerCase();
        return queryPart.matches("^\\s*\\bselect\\b[\\s\\S]*?\\bfrom\\b[\\s\\S]*$");
    }

    public boolean checkSelectFromAll(String queryPart) {
        queryPart = queryPart.trim().toLowerCase();
        return queryPart.matches("^select\\s*\\*$");
    }

    public boolean isNestedQuery(String queryPart) {
        queryPart = queryPart.trim().toLowerCase();
        return queryPart.matches("^\\s*\\(\\s*select\\b[\\s\\S]*?\\bfrom\\b[\\s\\S]*$");
    }

    public String parseAlias(String queryPart) {
        int closeSymbol = queryPart.lastIndexOf(")");
        return queryPart.substring(closeSymbol + 1).trim();
    }

    public Query parseNestedQuery(String queryPart) {
        int openSymbol = queryPart.indexOf("(");
        int closeSymbol = queryPart.lastIndexOf(")");
        return new Query(queryPart.substring(openSymbol + 1, closeSymbol));

    }

}
