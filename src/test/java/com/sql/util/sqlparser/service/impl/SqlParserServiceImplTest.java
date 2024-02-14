package com.sql.util.sqlparser.service.impl;

import com.sql.util.sqlparser.errorHandling.exceptions.SqlValidationException;
import com.sql.util.sqlparser.model.Query;
import com.sql.util.sqlparser.service.SqlParserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SqlParserServiceImplTest {


    @Autowired
    SqlParserService sqlParserService;

    @Test
    void parseSelectStatementTest_SelectAllFromOneTable_returnOneTableAndOneSelect() {

        String statement = "SELECT * FROM book";

        Query query = sqlParserService.parseSelectStatement(statement);

        assertEquals(1, query.getFromSources().size());
        assertEquals("FROM book", query.getFromSources().iterator().next());
        assertEquals(1, query.getColumns().size());
        assertEquals("SELECT *", query.getColumns().iterator().next());

    }

    @Test
    void parseSelectStatementTest_EmptyString_ThrowSqlValidationException() {

        String statement = "";

        assertThrows(SqlValidationException.class, () -> sqlParserService.parseSelectStatement(statement));

    }

    @Test
    void parseSelectStatementTest_ComplexStatement() {

        String statement = """
                SELECT author.name, count(book.id), sum(book.cost)\s
                FROM author\s
                LEFT JOIN book ON (author.id = book.author_id)\s
                GROUP BY author.name\s
                HAVING COUNT(*) > 1 AND SUM(book.cost) > 500
                LIMIT 10;""";

        Query query = sqlParserService.parseSelectStatement(statement);


    }

    @Test
    void divideStatement_SelectFromLeftJoinGroupByHavingLimit_6parts() {

        String statement = """
                SELECT author.name, count(book.id), sum(book.cost)\s
                FROM author\s
                LEFT JOIN book ON (author.id = book.author_id)\s
                GROUP BY author.name\s
                HAVING COUNT(*) > 1 AND SUM(book.cost) > 500
                LIMIT 10;""";

        List<String> parts = new SqlParserServiceImpl().divideStatement(statement);

        parts.forEach((str) -> System.out.println("[part]: " + str));

        assertEquals(6, parts.size());
    }

    @Test
    void divideStatement_SelectFromLeft2SpaceJoinGroupByHavingLimit_6parts() {

        String statement = """
                SELECT author.name, count(book.id), sum(book.cost)\s
                FROM author\s
                LEFT  JOIN book ON (author.id = book.author_id)\s
                GROUP BY author.name\s
                HAVING COUNT(*) > 1 AND SUM(book.cost) > 500
                LIMIT 10;""";

        List<String> parts = new SqlParserServiceImpl().divideStatement(statement);

        parts.forEach((str) -> System.out.println("[part]: " + str));

        assertEquals(6, parts.size());
    }

}