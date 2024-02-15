package com.sql.util.sqlparser.controller;


import com.sql.util.sqlparser.model.Query;
import com.sql.util.sqlparser.model.dto.QueryDTO;
import com.sql.util.sqlparser.service.SqlParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "sql-parser/api/v1")
public class SqlParserController {

    SqlParserService sqlParserService;

    @PostMapping(value = "select")
    public Query parseSqLSelectStatement(@RequestBody QueryDTO query) {
        return sqlParserService.parseSelectStatement(query.getQuery());
    }

    @Autowired
    public void setSqlParserService(SqlParserService sqlParserService) {
        this.sqlParserService = sqlParserService;
    }
}
