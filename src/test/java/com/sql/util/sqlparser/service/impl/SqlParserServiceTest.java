package com.sql.util.sqlparser.service.impl;

import com.sql.util.sqlparser.errorHandling.exceptions.SqlValidationException;
import com.sql.util.sqlparser.model.Query;
import com.sql.util.sqlparser.service.SqlParserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SqlParserServiceTest {

    @Autowired
    SqlParserService sqlParserService;

    @Test
    void parseSelectStatementTest_SelectAllFromOneTable_returnOneTableAndOneSelect() {

        String statement = "SELECT * FROM book";

        Query query = sqlParserService.parseSelectStatement(statement);

        assertEquals(1, query.getSelect().getSelectExpressions().size());
        assertEquals("FROM book", query.getFrom().getInitialStatement());
        assertEquals(1, query.getSelect().getSelectExpressions().size());
        assertEquals("SELECT *", query.getSelect().getInitialStatement());

    }

    @Test
    void parseSelectStatementTest_EmptyString_ThrowSqlValidationException() {

        assertThrows(SqlValidationException.class, () -> sqlParserService.parseSelectStatement(""));

    }

    @Test
    void parseSelectStatementTest_IncorrectSelect_ThrowSqlValidationException() {

        assertThrows(SqlValidationException.class, () -> sqlParserService.parseSelectStatement("Select 45"));

    }

    @Test
    void parseSelectStatementTest_IncorrectSelectFrom_ThrowSqlValidationException() {

        assertThrows(SqlValidationException.class, () -> sqlParserService.parseSelectStatement("Select 45 From"));

    }

    @Test
    void parseSelectStatementTest_IncorrectSelectFromConnected_ThrowSqlValidationException() {

        assertThrows(SqlValidationException.class, () -> sqlParserService.parseSelectStatement("Select45FromB"));

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

        Query query = sqlParserService.parseSelectStatement(statement);

        assertEquals(6, query.sizeOfStatementParts());
    }

    @Test
    void divideStatement_SelectFromLeft2SpaceJoinGroupByHavingLimit_6parts() {

        String sql = """
                SELECT author.name, count(book.id), sum(book.cost)\s
                FROM author\s
                LEFT  JOIN book ON (author.id = book.author_id)\s
                GROUP BY author.name\s
                HAVING COUNT(*) > 1 AND SUM(book.cost) > 500
                LIMIT 10;""";

        Query query = sqlParserService.parseSelectStatement(sql);

        assertEquals(6, query.sizeOfStatementParts());
    }


    @Test
    void divideStatement_2parts() {

        String sql = """
                select a_alias.id, (select count(*) from b where b.id=a_alias.id) t1 from (select * from A) a_alias
                """;

        Query query = sqlParserService.parseSelectStatement(sql);

        assertEquals(2, query.sizeOfStatementParts());
    }

    @Test
    void parseLimit() {

        String sql = """
                select a_alias.id, (select count(*) from b where b.id=a_alias.id) t1 from (select * from A) a_alias limit 10;
                """;

        Query query = sqlParserService.parseSelectStatement(sql);

        assertEquals(10, query.getLimit());
        assertNull(query.getOffset());

    }

    @Test
    void parseOffset() {

        String sql = """
                SELECT *
                FROM orders
                ORDER BY order_date
                OFFSET 3;
                """;

        Query query = sqlParserService.parseSelectStatement(sql);

        assertEquals(3, query.getOffset());
        assertNull(query.getLimit());
    }

    @Test
    void parseLimitOffset() {

        String sql = """
                SELECT *
                FROM employees
                ORDER BY salary DESC
                LIMIT 5 OFFSET 10;
                """;

        Query query = sqlParserService.parseSelectStatement(sql);

        assertEquals(10, query.getOffset());
        assertEquals(5, query.getLimit());
    }


}
