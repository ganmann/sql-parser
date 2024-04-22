package com.sql.util.sqlparser.service.impl;

import com.sql.util.sqlparser.errorHandling.exceptions.SqlValidationException;
import com.sql.util.sqlparser.model.*;
import com.sql.util.sqlparser.service.SqlParserService;
import com.sql.util.sqlparser.utils.QueryParser;
import com.sql.util.sqlparser.utils.SQLUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;


@Service
public class SqlParserServiceImpl implements SqlParserService {


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

        QueryParser queryParser = new QueryParser(statement);

        queryParser.parseHighLevel();
        queryParser.parseQueryComponents();

        return queryParser.getQuery();
    }

}
