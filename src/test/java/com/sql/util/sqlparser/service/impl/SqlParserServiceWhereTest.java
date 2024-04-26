package com.sql.util.sqlparser.service.impl;


import com.sql.util.sqlparser.model.Predicate;
import com.sql.util.sqlparser.model.Query;
import com.sql.util.sqlparser.model.enums.LogicalOperator;
import com.sql.util.sqlparser.service.SqlParserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class SqlParserServiceWhereTest {

    @Autowired
    SqlParserService sqlParserService;

    @Test
    void testWhereWithoutWhere() {
        String queryStatement = """
            SELECT *
            FROM customers;
            """;

        Query query = sqlParserService.parseSelectStatement(queryStatement);

        assertNull(query.getWhereClause());

    }

    @Test
    void testWhereHasOnePredicate() {
        String queryStatement = """
            SELECT *
            FROM customers
            WHERE country = 'USA';
            """;

        Query query = sqlParserService.parseSelectStatement(queryStatement);
        Predicate predicate = query.getWhereClause().getPredicate();

        assertNotNull(predicate);
        assertFalse(predicate.isNestedPredicate());
        assertFalse(predicate.hasNextPredicate());
        assertEquals("=", predicate.getComparison());
        assertTrue(predicate.getLeftOperand().isColumn());
        assertEquals("country", predicate.getLeftOperand().getColumn().getColumnName());
        assertTrue(predicate.getRightOperand().isExpression());
        assertEquals("'USA'", predicate.getRightOperand().getExpression());

    }

    @Test
    void testWhereHasAndSecondNestedPredicateWithOr() {
        String queryStatement = """
                SELECT *
                FROM customers
                WHERE country = 'USA'
                  AND (city = 'New York' OR city like 'Los%');
            """;

        Query query = sqlParserService.parseSelectStatement(queryStatement);
        Predicate predicate = query.getWhereClause().getPredicate();

        assertNotNull(predicate);
        assertFalse(predicate.isNestedPredicate());
        assertTrue(predicate.hasNextPredicate());
        assertEquals(LogicalOperator.AND, predicate.getPredicateRelation().getLogicalOperator());
        assertTrue(predicate.getPredicateRelation().getNextPredicate().isNestedPredicate());

        Predicate nestedPredicate = predicate.getPredicateRelation().getNextPredicate().getNestedPredicate();

        assertTrue(nestedPredicate.hasNextPredicate());
        assertEquals(LogicalOperator.OR, nestedPredicate.getPredicateRelation().getLogicalOperator());

        Predicate nestedPredicateSecond = nestedPredicate.getPredicateRelation().getNextPredicate();

        assertEquals("like", nestedPredicateSecond.getComparison());
        assertEquals("city", nestedPredicateSecond.getLeftOperand().getColumn().getColumnName());
        assertTrue(nestedPredicateSecond.getRightOperand().isExpression());
        assertEquals("'Los%'", nestedPredicateSecond.getRightOperand().getExpression());

    }

    @Test
    void testWhereNotEquals() {
        String queryStatement = """
                SELECT *
                FROM customers
                WHERE country <>'USA';
            """;

        Query query = sqlParserService.parseSelectStatement(queryStatement);
        Predicate predicate = query.getWhereClause().getPredicate();

        assertFalse(predicate.hasNextPredicate());
        assertEquals("<>", predicate.getComparison());
        assertTrue(predicate.getRightOperand().isExpression());
        assertEquals("'USA'", predicate.getRightOperand().getExpression());

    }



    @Test
    void testWhereIsNull() {
        String queryStatement = """
                    SELECT *
                    FROM customers
                    WHERE country is null;
                """;

        Query query = sqlParserService.parseSelectStatement(queryStatement);
        Predicate predicate = query.getWhereClause().getPredicate();

        assertFalse(predicate.hasNextPredicate());
        assertEquals("is null", predicate.getComparison());
        assertTrue(predicate.getLeftOperand().isColumn());
        assertEquals("country", predicate.getLeftOperand().getColumn().getColumnName());
        assertNull(predicate.getRightOperand());
    }

    @Test
    void testWhereFourAndPredicates() {
        String queryStatement = """
                    SELECT *
                    FROM customers
                    WHERE country = 'USA'
                      and state = 'TEXAS'
                      and name like "Ivan%"
                      and age >=21;
                """;

        Query query = sqlParserService.parseSelectStatement(queryStatement);
        Predicate predicate = query.getWhereClause().getPredicate();

        assertTrue(predicate.hasNextPredicate());
        assertEquals(LogicalOperator.AND, predicate.getPredicateRelation().getLogicalOperator());
        assertTrue(predicate.getPredicateRelation().getNextPredicate().hasNextPredicate());
        assertEquals(LogicalOperator.AND, predicate.getPredicateRelation().getNextPredicate().getPredicateRelation().getLogicalOperator());

        Predicate predicate3 = predicate.getPredicateRelation().getNextPredicate().getPredicateRelation().getNextPredicate();

        assertTrue(predicate3.getLeftOperand().isColumn());
        assertEquals("name", predicate3.getLeftOperand().getColumn().getColumnName());
        assertTrue(predicate3.getRightOperand().isExpression());
        assertTrue(predicate3.hasNextPredicate());
        assertEquals(LogicalOperator.AND, predicate3.getPredicateRelation().getLogicalOperator());

        Predicate predicate4 = predicate3.getPredicateRelation().getNextPredicate();

        assertTrue(predicate4.getLeftOperand().isColumn());
        assertEquals(">=", predicate4.getComparison());
        assertTrue(predicate4.getRightOperand().isExpression());



    }


}
