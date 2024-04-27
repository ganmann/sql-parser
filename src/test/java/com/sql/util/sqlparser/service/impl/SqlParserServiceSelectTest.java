package com.sql.util.sqlparser.service.impl;

import com.sql.util.sqlparser.model.*;
import com.sql.util.sqlparser.service.SqlParserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SqlParserServiceSelectTest {


    @Autowired
    SqlParserService sqlParserService;

    @Test
    void parseStatement_withNestedColumn_hasNestedColumn() {

        String statement = """
                select a_alias.id, (select count(*) from b where b.id=a_alias.id) t1 from (select * from A) a_alias
                """;

        Query query = new SqlParserServiceImpl().parse(statement);

        assertEquals(2, query.sizeOfStatementParts());
        assertEquals(2, query.getSelect().getSelectExpressions().size());
        assertEquals(1, query.getSelect().getSelectExpressions().stream().filter(SelectExpression::isNestedQuery).count());
        assertEquals(1, query.getSelect().getSelectExpressions().stream().filter(SelectExpression::isNestedQuery).findFirst()
                .map(SelectExpression::getNestedQuery).map(Query::getSelect).map(Select::getSelectExpressions).orElse(Collections.emptyList()).size());
    }

    @Test
    void parseStatement_withNestedQueryWith2Columns_hasNestedColumnWith2Columns() {

        String statement = """
                select a_alias.id, (select b.id,b.name from b where b.id=a_alias.id) t1 from (select * from A) a_alias
                """;

        Query query = new SqlParserServiceImpl().parse(statement);

        assertEquals(2, query.sizeOfStatementParts());
        assertEquals(2, query.getSelect().getSelectExpressions().size());
        assertEquals(1, query.getSelect().getSelectExpressions().stream().filter(SelectExpression::isNestedQuery).count());
        assertEquals(2, query.getSelect().getSelectExpressions().stream().filter(SelectExpression::isNestedQuery).findFirst()
                .map(SelectExpression::getNestedQuery).map(Query::getSelect).map(Select::getSelectExpressions).orElse(Collections.emptyList()).size());
    }

    @Test
    void parseStatement_withNestedQueryTableIsAliasT1_true() {

        String statement = """
                select a_alias.id, (select count(*) from b where b.id=a_alias.id) t1 from (select * from A) a_alias
                """;

        Query query = new SqlParserServiceImpl().parse(statement);

        assertEquals(2, query.sizeOfStatementParts());
        assertEquals(2, query.getSelect().getSelectExpressions().size());
        assertEquals(1, query.getSelect().getSelectExpressions().stream().filter(SelectExpression::isNestedQuery).count());
        assertEquals("t1", query.getSelect().getSelectExpressions().stream().filter(SelectExpression::isNestedQuery).findFirst()
                .map(SelectExpression::getAlias).orElse(""));
    }

    @Test
    void parseStatement_FunctionInSelect_true() {
        String statement = """
                select count(a.*) from A
                """;

        Query query = new SqlParserServiceImpl().parse(statement);
        assertTrue(query.getSelect().getSelectExpressions().stream().findFirst().map(SelectExpression::isFunction).orElse(false));

    }

    @Test
    void parseStatement_quoteLiteralInSelect_true() {

        String statement = """
                select 'first' from users t
                """;

        Query query = sqlParserService.parseSelectStatement(statement);

        assertEquals(1, query.getSelect().getSelectExpressions().size());
        assertTrue(query.getSelect().getSelectExpressions().stream().findFirst().map(SelectExpression::isLiteral).orElse(false));
    }

    @Test
    void parseStatement_doubleQuoteLiteralInSelect_true() {

        String statement = """
                select "first" from users t
                """;

        Query query = sqlParserService.parseSelectStatement(statement);

        assertEquals(1, query.getSelect().getSelectExpressions().size());
        assertTrue(query.getSelect().getSelectExpressions().stream().findFirst().map(SelectExpression::isLiteral).orElse(false));
    }

    @Test
    void parseStatement_quoteLiteralWithKeyword_literalSelectExpression() {

        String statement = """
                select "from" from users t
                """;

        Query query = sqlParserService.parseSelectStatement(statement);

        assertEquals(1, query.getSelect().getSelectExpressions().size());
        assertTrue(query.getSelect().getSelectExpressions().stream().findFirst().map(SelectExpression::isLiteral).orElse(false));
    }

    @Test
    void parseStatement_quoteLiteralWithKeyword_tableUsersWithAliasT() {

        String statement = """
                select "from" from users t
                """;

        Query query = sqlParserService.parseSelectStatement(statement);

        assertEquals(1, Optional.ofNullable(query.getFrom()).map(From::getTables).map(List::size).orElse(0));
        assertEquals("users", query.getFrom().getTables().stream().findFirst().map(Table::getTableName).orElse(""));
        assertEquals("t", query.getFrom().getTables().stream().findFirst().map(Table::getAlias).orElse(""));
    }

    @Test
    void parseStatement_LiteralNumberInSelect_true() {
        String statement = """
                select 0, +78, 88.99 from users t
                """;

        Query query = new SqlParserServiceImpl().parse(statement);

        assertEquals(3, query.getSelect().getSelectExpressions().size());
        query.getSelect().getSelectExpressions().forEach(selectExpression -> assertTrue(selectExpression.isLiteral()));
    }

    @Test
    void parseStatement_columnWithAlias_true() {
        String statement = """
                select SUM(oi.quantity * oi.price) AS total_order_amount
                from order_items oi
                """;

        Query query = new SqlParserServiceImpl().parse(statement);

        assertEquals(1, query.getSelect().getSelectExpressions().size());
        SelectExpression selectExpression = query.getSelect().getSelectExpressions().getFirst();
        assertTrue(selectExpression.isFunction());
        assertEquals("total_order_amount", selectExpression.getAlias());

        assertEquals(1, query.getFrom().getTables().size());
        Table table = query.getFrom().getTables().getFirst();
        assertEquals("oi", table.getAlias());
    }

}