package com.sql.util.sqlparser.service.impl;

import com.sql.util.sqlparser.errorHandling.exceptions.SqlValidationException;
import com.sql.util.sqlparser.model.Column;
import com.sql.util.sqlparser.model.Query;
import com.sql.util.sqlparser.service.SqlParserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SqlParserServiceImplTest {


    @Autowired
    SqlParserService sqlParserService;

    @Test
    void parseSelectStatementTest_SelectAllFromOneTable_returnOneTableAndOneSelect() {

        String statement = "SELECT * FROM book";

        Query query = sqlParserService.parseSelectStatement(statement);

//        assertEquals(1, query.getSelect().size());
        assertEquals("FROM book", query.getFrom().getInitialStatement());
//        assertEquals(1, query.getColumns().size());
        assertEquals("SELECT *", query.getSelect().getInitialStatement());

    }

    @Test
    void parseSelectStatementTest_EmptyString_ThrowSqlValidationException() {

        String statement = "";

        assertThrows(SqlValidationException.class, () -> sqlParserService.parseSelectStatement(statement));

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

        Query query = new SqlParserServiceImpl().parse(statement);

//        query.forEach((str) -> System.out.println("[part]: " + str));

        assertEquals(6, query.sizeOfStatementParts());
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

        Query query = new SqlParserServiceImpl().parse(statement);

//        parts.forEach((str) -> System.out.println("[part]: " + str));

        assertEquals(6, query.sizeOfStatementParts());
    }


    @Test
    void divideStatement_2parts() {

        String statement = """
                select a_alias.id, (select count(*) from b where b.id=a_alias.id) t1 from (select * from A) a_alias
                """;

        Query query = new SqlParserServiceImpl().parse(statement);

        assertEquals(2, query.sizeOfStatementParts());
    }

    @Test
    void divideStatement_nestedColumn_hasNestedColumn() {

        String statement = """
                select a_alias.id, (select count(*) from b where b.id=a_alias.id) t1 from (select * from A) a_alias
                """;

        Query query = new SqlParserServiceImpl().parse(statement);

        assertEquals(2, query.sizeOfStatementParts());
        assertEquals(2, query.getSelect().getColumns().size());
        assertEquals(1, query.getSelect().getColumns().stream().filter(Column::isNestedQuery).count());
        assertEquals(1, query.getSelect().getColumns().stream().filter(Column::isNestedQuery).findFirst().get().getNestedQuery().getQuery().getSelect().getColumns().size());
    }

    @Test
    void divideStatement_nestedColumnHas2Columns_hasNestedColumn() {

        String statement = """
                select a_alias.id, (select b.id,b.name from b where b.id=a_alias.id) t1 from (select * from A) a_alias
                """;

        Query query = new SqlParserServiceImpl().parse(statement);

        assertEquals(2, query.sizeOfStatementParts());
        assertEquals(2, query.getSelect().getColumns().size());
        assertEquals(1, query.getSelect().getColumns().stream().filter(Column::isNestedQuery).count());
        assertEquals(2, query.getSelect().getColumns().stream().filter(Column::isNestedQuery).findFirst().get().getNestedQuery().getQuery().getSelect().getColumns().size());
    }

    @Test
    void divideStatement_nestedColumnAliasT1_true() {

        String statement = """
                select a_alias.id, (select count(*) from b where b.id=a_alias.id) t1 from (select * from A) a_alias
                """;

        Query query = new SqlParserServiceImpl().parse(statement);

        assertEquals(2, query.sizeOfStatementParts());
        assertEquals(2, query.getSelect().getColumns().size());
        assertEquals(1, query.getSelect().getColumns().stream().filter(Column::isNestedQuery).count());
        assertEquals("t1", query.getSelect().getColumns().stream().filter(Column::isNestedQuery).findFirst().get().getAlias());
    }

}