package com.sql.util.sqlparser.service;

import com.sql.util.sqlparser.model.Query;

public interface SqlParserService {

    Query parseSelectStatement(String selectStatement);
}
