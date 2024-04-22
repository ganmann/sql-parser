package com.sql.util.sqlparser.service.impl;

import com.sql.util.sqlparser.model.Query;
import com.sql.util.sqlparser.service.SqlParserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class SqlParserServiceFromTest {

    @Autowired
    SqlParserService sqlParserService;

    @Test
    void parseStatement_SelectFrom_TableAuthor() {

        String statement = """
                SELECT author.name, count(book.id), sum(book.cost)\s
                FROM author
                """;

        Query query = new SqlParserServiceImpl().parse(statement);

        assertEquals("author", query.getFrom().getTables().iterator().next().getTableName());
    }

    // fix case with ;
    @Test
    void parseStatement_SelectFromWithComma_TableAuthor() {

        String statement = """
                SELECT author.name, count(book.id), sum(book.cost)\s
                FROM author;
                """;

        Query query = new SqlParserServiceImpl().parse(statement);

        assertEquals("author", query.getFrom().getTables().iterator().next().getTableName());
    }

    @Test
    void parseStatement_SelectFrom_TableAuthorAliasAuth() {

        String statement = """
                SELECT author.name, count(book.id), sum(book.cost)\s
                FROM author auth
                """;

        Query query = new SqlParserServiceImpl().parse(statement);

        assertEquals("author", query.getFrom().getTables().iterator().next().getTableName());
        assertEquals("auth", query.getFrom().getTables().iterator().next().getAlias());
    }

    @Test
    void parseStatement_SelectFromNested_WithAlias() {

        String statement = """
                select a_alias.id, (select count(*) from b where b.id=a_alias.id) t1 from (select * from A) a_alias
                """;

        Query query = new SqlParserServiceImpl().parse(statement);

        assertTrue(query.getFrom().getTables().iterator().next().isNestedQuery());
        assertEquals("a_alias", query.getFrom().getTables().iterator().next().getAlias());
        assertEquals("A", query.getFrom().getTables().iterator().next().getNestedQuery().getFrom().getTables().iterator().next().getTableName());

    }

    @Test
    void parseStatementFromWithAsAlias() {

        String statement = """
                SELECT author.name, count(book.id), sum(book.cost)\s
                FROM author as auth
                """;

        Query query = new SqlParserServiceImpl().parse(statement);

        assertEquals("author", query.getFrom().getTables().iterator().next().getTableName());
        assertEquals("auth", query.getFrom().getTables().iterator().next().getAlias());

    }
}
