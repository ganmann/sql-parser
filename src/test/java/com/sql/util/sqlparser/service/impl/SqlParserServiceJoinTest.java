package com.sql.util.sqlparser.service.impl;

import com.sql.util.sqlparser.errorHandling.exceptions.SqlValidationException;
import com.sql.util.sqlparser.model.JoinClause;
import com.sql.util.sqlparser.model.Predicate;
import com.sql.util.sqlparser.model.Query;
import com.sql.util.sqlparser.model.enums.JoinType;
import com.sql.util.sqlparser.model.enums.LogicalOperator;
import com.sql.util.sqlparser.service.SqlParserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SqlParserServiceJoinTest {


    @Autowired
    SqlParserService sqlParserService;
    
    @Test
    void parseQuery_LeftJoin() {
        String statement = """
                SELECT author.name\s
                FROM author\s
                LEFT JOIN book ON author.id = book.author_id;""";

        Query query = sqlParserService.parseSelectStatement(statement);

        assertEquals(1, query.getJoins().getJoinClauses().size());

        JoinClause joinClause = query.getJoins().getJoinClauses().getFirst();
        assertEquals(JoinType.LEFT_JOIN, joinClause.getJoinType());
        assertEquals("book", joinClause.getTable().getTableName());
        assertFalse(joinClause.getJoinKeys().hasNextPredicate());
        assertEquals("author_id", joinClause.getJoinKeys().getRightOperand().getColumn().getColumnName());

    }

    @Test
    void parseQuery_leftAndRightJoins() {

        String statement = """
                SELECT author.name, count(book.id), sum(book.cost)\s
                FROM author\s
                LEFT  JOIN book ON (author.id = book.author_id)
                RIGHT JOIN course ON (course.id = book.course_id)\s
                LIMIT 10;""";

        Query query = sqlParserService.parseSelectStatement(statement);
        assertEquals(2, query.getJoins().getJoinClauses().size());

        JoinClause joinClause1 = query.getJoins().getJoinClauses().get(0);
        JoinClause joinClause2 = query.getJoins().getJoinClauses().get(1);

        assertEquals(JoinType.LEFT_JOIN, joinClause1.getJoinType());
        assertEquals(JoinType.RIGHT_JOIN, joinClause2.getJoinType());

        assertEquals("book", joinClause1.getTable().getTableName());
        assertEquals("course", joinClause2.getTable().getTableName());

        assertTrue(joinClause1.getJoinKeys().isNestedPredicate());
        assertTrue(joinClause2.getJoinKeys().isNestedPredicate());

        assertFalse(joinClause1.getJoinKeys().hasNextPredicate());
        assertFalse(joinClause2.getJoinKeys().hasNextPredicate());

        Predicate predicate1 = joinClause1.getJoinKeys().getNestedPredicate();
        Predicate predicate2 = joinClause2.getJoinKeys().getNestedPredicate();

        assertFalse(predicate1.isNestedPredicate());
        assertFalse(predicate2.isNestedPredicate());

        assertFalse(predicate1.hasNextPredicate());
        assertFalse(predicate2.hasNextPredicate());

        assertTrue(predicate1.getLeftOperand().isColumn());
        assertTrue(predicate2.getLeftOperand().isColumn());

        assertEquals("author", predicate1.getLeftOperand().getColumn().getTable());
        assertEquals("id", predicate1.getLeftOperand().getColumn().getColumnName());
        assertEquals("book", predicate1.getRightOperand().getColumn().getTable());
        assertEquals("author_id", predicate1.getRightOperand().getColumn().getColumnName());
        assertEquals("=", predicate1.getComparison());

        assertEquals("course", predicate2.getLeftOperand().getColumn().getTable());
        assertEquals("id", predicate2.getLeftOperand().getColumn().getColumnName());
        assertEquals("book", predicate2.getRightOperand().getColumn().getTable());
        assertEquals("course_id", predicate2.getRightOperand().getColumn().getColumnName());
        assertEquals("=", predicate2.getComparison());



    }

    @Test
    void parseQuery_innerJoinAndTwoPredicates() {

        String statement = """
                        SELECT *\s
                        FROM orders\s
                        INNER JOIN order_items ON orders.order_id = order_items.order_id\s
                        AND orders.product_id = order_items.product_id;\s
                        """;

        Query query = sqlParserService.parseSelectStatement(statement);
        assertEquals(1, query.getJoins().getJoinClauses().size());

        JoinClause joinClause = query.getJoins().getJoinClauses().getFirst();

        assertEquals(JoinType.JOIN, joinClause.getJoinType());
        assertEquals("order_items", joinClause.getTable().getTableName());
        Predicate predicate = joinClause.getJoinKeys();
        assertFalse(predicate.isNestedPredicate());
        assertEquals("=", predicate.getComparison());
        assertTrue(predicate.getLeftOperand().isColumn());
        assertTrue(predicate.getRightOperand().isColumn());

        assertTrue(predicate.hasNextPredicate());
        assertEquals(LogicalOperator.AND, predicate.getPredicateRelation().getLogicalOperator());

        Predicate predicate2 = predicate.getPredicateRelation().getPredicate();
        assertFalse(predicate2.isNestedPredicate());
        assertEquals("=", predicate2.getComparison());
        assertTrue(predicate2.getLeftOperand().isColumn());
        assertTrue(predicate2.getRightOperand().isColumn());

    }


    @Test
    void parseQuery_fullOuterJoinPredicateAndNestedOrWithLiteralsXor() {

        String statement = """
                          SELECT *
                          FROM orders
                          full outer join customers ON orders.customer_id = customers.customer_id and (customers.city = 'New York' OR customers.city = 'Los Angeles')
                             XOR orders.amount > 1000;
                        """;

        Query query = sqlParserService.parseSelectStatement(statement);
        assertEquals(1, query.getJoins().getJoinClauses().size());

        JoinClause joinClause = query.getJoins().getJoinClauses().getFirst();

        assertEquals(JoinType.FULL_JOIN, joinClause.getJoinType());
        assertEquals("customers", joinClause.getTable().getTableName());

        Predicate predicate1 = joinClause.getJoinKeys();
        assertTrue(predicate1.hasNextPredicate());
        assertEquals(LogicalOperator.AND, predicate1.getPredicateRelation().getLogicalOperator());

        Predicate predicate2 = predicate1.getPredicateRelation().getPredicate();
        assertTrue(predicate2.isNestedPredicate());
        Predicate predicate2Nested = predicate2.getNestedPredicate();
        assertTrue(predicate2Nested.getLeftOperand().isColumn());
        assertEquals("customers", predicate2Nested.getLeftOperand().getColumn().getTable());
        assertEquals("city", predicate2Nested.getLeftOperand().getColumn().getColumnName());
        assertTrue(predicate2Nested.getRightOperand().isExpression());
        assertTrue(predicate2Nested.hasNextPredicate());
        assertEquals(LogicalOperator.OR, predicate2Nested.getPredicateRelation().getLogicalOperator());
        assertTrue(predicate2Nested.getPredicateRelation().getPredicate().getLeftOperand().isColumn());
        assertTrue(predicate2Nested.getPredicateRelation().getPredicate().getRightOperand().isExpression());

        assertTrue(predicate2.hasNextPredicate());
        assertEquals(LogicalOperator.XOR, predicate2.getPredicateRelation().getLogicalOperator());

        Predicate predicate3 = predicate2.getPredicateRelation().getPredicate();
        assertTrue(predicate3.getLeftOperand().isColumn());
        assertEquals("orders", predicate3.getLeftOperand().getColumn().getTable());
        assertEquals("amount", predicate3.getLeftOperand().getColumn().getColumnName());
        assertEquals(">", predicate3.getComparison());
        assertTrue(predicate3.getRightOperand().isExpression());
        assertEquals("1000", predicate3.getRightOperand().getInitialStatement());

    }

    @Test
    void parseQuery_incorrectJoin_trowSqlValidateException() {

        String statement = """
                SELECT author.name, count(book.id), sum(book.cost)\s
                FROM author\s
                LEFT  JOIN book (author.id = book.author_id)\s
                RIGHT JOIN course ON (course.id = book.course_id)\s
                LIMIT 10;""";

        assertThrows(SqlValidationException.class, () -> sqlParserService.parseSelectStatement(statement));

    }

    @Test
    void parseQuery_rightOuterJoinTwoPredicatesWithLike() {

        String statement = """
                SELECT author.name, count(book.id), sum(book.cost)\s
                FROM author a\s
                right outer join course c ON c.author_id = a.id and c.name like 'math%'
                """;


        Query query = sqlParserService.parseSelectStatement(statement);
        assertEquals(1, query.getJoins().getJoinClauses().size());

        JoinClause joinClause = query.getJoins().getJoinClauses().getFirst();

        assertEquals(JoinType.RIGHT_JOIN, joinClause.getJoinType());
        assertEquals("c", joinClause.getTable().getAlias());

        Predicate predicate1 = joinClause.getJoinKeys();
        assertTrue(predicate1.hasNextPredicate());
        assertEquals(LogicalOperator.AND, predicate1.getPredicateRelation().getLogicalOperator());

        Predicate predicate2 = predicate1.getPredicateRelation().getPredicate();
        assertEquals("like", predicate2.getComparison());
        assertEquals("c", predicate2.getLeftOperand().getColumn().getTable());
        assertEquals("name", predicate2.getLeftOperand().getColumn().getColumnName());
        assertTrue(predicate2.getRightOperand().isExpression());
        assertEquals("'math%'", predicate2.getRightOperand().getExpression());

    }

}
