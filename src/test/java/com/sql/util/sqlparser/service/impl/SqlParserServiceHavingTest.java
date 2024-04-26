package com.sql.util.sqlparser.service.impl;

import com.sql.util.sqlparser.model.Predicate;
import com.sql.util.sqlparser.model.Query;
import com.sql.util.sqlparser.model.SelectExpression;
import com.sql.util.sqlparser.model.enums.LogicalOperator;
import com.sql.util.sqlparser.service.SqlParserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SqlParserServiceHavingTest {

    @Autowired
    SqlParserService sqlParserService;

    @Test
    public void parseHavingWithoutHaving() {
        String sql = """
                SELECT department, AVG(salary) AS average_salary
                FROM employees
                GROUP BY department
                """;

        Query query = sqlParserService.parseSelectStatement(sql);

        assertNull(query.getHaving());
    }

    @Test
    public void parseHavingFilteringByAggregateFunctionTest() {
        String sql = """
                SELECT department, AVG(salary) AS average_salary
                FROM employees
                GROUP BY department
                HAVING AVG(salary) > 50000;
                """;

        Query query = sqlParserService.parseSelectStatement(sql);

        assertNotNull(query.getHaving());
        assertNotNull(query.getHaving().getPredicate());

        Predicate predicate = query.getHaving().getPredicate();

        assertTrue(predicate.getLeftOperand().isFunction());
        assertTrue(predicate.getRightOperand().isExpression());
        assertEquals(">", predicate.getComparison());
        assertEquals("AVG", predicate.getLeftOperand().getFunction());
        assertEquals("salary", predicate.getLeftOperand().getColumn().getColumnName());
        assertEquals("50000", predicate.getRightOperand().getExpression());
    }

    @Test
    public void parseHavingMultipleConditionsTest() {
        String sql = """
                SELECT category, COUNT(*) AS num_products
                FROM products
                GROUP BY category
                HAVING COUNT(*) > 10 AND AVG(price) < 50;
                """;

        Query query = sqlParserService.parseSelectStatement(sql);

        assertNotNull(query.getHaving());
        assertNotNull(query.getHaving().getPredicate());

        Predicate predicate1 = query.getHaving().getPredicate();

        assertTrue(predicate1.getLeftOperand().isFunction());
        assertTrue(predicate1.getRightOperand().isExpression());
        assertEquals(">", predicate1.getComparison());
        assertEquals("COUNT", predicate1.getLeftOperand().getFunction());
        assertEquals("*", predicate1.getLeftOperand().getColumn().getColumnName());
        assertEquals("10", predicate1.getRightOperand().getExpression());
        assertTrue(predicate1.hasNextPredicate());
        assertEquals(LogicalOperator.AND, predicate1.getPredicateRelation().getLogicalOperator());

        Predicate predicate2 = predicate1.getPredicateRelation().getNextPredicate();
        assertTrue(predicate2.getLeftOperand().isFunction());
        assertTrue(predicate2.getRightOperand().isExpression());
        assertEquals("<", predicate2.getComparison());
        assertEquals("AVG", predicate2.getLeftOperand().getFunction());
        assertEquals("price", predicate2.getLeftOperand().getColumn().getColumnName());
        assertEquals("50", predicate2.getRightOperand().getExpression());



    }

    @Test
    public void parseHavingWithSubquery() {
        String sql = """
                SELECT department, AVG(salary) AS average_salary
                FROM employees
                GROUP BY department
                HAVING AVG(salary) > (SELECT AVG(salary) FROM employees);
                """;

        Query query = sqlParserService.parseSelectStatement(sql);

        assertNotNull(query.getHaving());
        assertNotNull(query.getHaving().getPredicate());

        Predicate predicate1 = query.getHaving().getPredicate();

        assertTrue(predicate1.getLeftOperand().isFunction());
        assertTrue(predicate1.getRightOperand().isNestedQuery());
        assertEquals(">", predicate1.getComparison());
        assertEquals("AVG", predicate1.getLeftOperand().getFunction());
        assertEquals("salary", predicate1.getLeftOperand().getColumn().getColumnName());
        assertTrue(predicate1.getRightOperand().isNestedQuery());

        Query nestedQuery = predicate1.getRightOperand().getNestedQuery();

        assertEquals(2, nestedQuery.sizeOfStatementParts());
        assertEquals(1, nestedQuery.getSelect().getSelectExpressions().size());

        SelectExpression selectExpression = nestedQuery.getSelect().getSelectExpressions().getFirst();

        assertTrue(selectExpression.isFunction());
    }
}
