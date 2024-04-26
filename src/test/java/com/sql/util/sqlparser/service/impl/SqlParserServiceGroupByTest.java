package com.sql.util.sqlparser.service.impl;

import com.sql.util.sqlparser.model.Query;
import com.sql.util.sqlparser.service.SqlParserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SqlParserServiceGroupByTest {

    @Autowired
    SqlParserService sqlParserService;

    @Test
    void testGroupByColumn() {
        String queryStatement = """
                SELECT category, COUNT(*) AS num_products
                FROM products
                GROUP BY category;
                """;

        Query query = sqlParserService.parseSelectStatement(queryStatement);

        assertNotNull(query.getGroupBy());
        assertNotNull(query.getGroupBy().getColumns());
        assertEquals(1, query.getGroupBy().getColumns().size());
        assertEquals("category", query.getGroupBy().getColumns().getFirst().getColumnName());
    }

    @Test
    void testGroupByColumns() {
        String queryStatement = """
                SELECT country, city, AVG(order_amount) AS average_order
                FROM orders
                GROUP BY country, city;
                """;

        Query query = sqlParserService.parseSelectStatement(queryStatement);

        assertNotNull(query.getGroupBy());
        assertNotNull(query.getGroupBy().getColumns());
        assertEquals(2, query.getGroupBy().getColumns().size());
        assertEquals("country", query.getGroupBy().getColumns().getFirst().getColumnName());
        assertEquals("city", query.getGroupBy().getColumns().getLast().getColumnName());
    }

    @Test
    void testGroupByTableColumn() {
        String queryStatement = """
                SELECT category, COUNT(*) AS num_products
                FROM products p
                GROUP BY p.category;
                """;

        Query query = sqlParserService.parseSelectStatement(queryStatement);

        assertNotNull(query.getGroupBy());
        assertNotNull(query.getGroupBy().getColumns());
        assertEquals(1, query.getGroupBy().getColumns().size());
        assertEquals("p", query.getGroupBy().getColumns().getFirst().getTable());
        assertEquals("category", query.getGroupBy().getColumns().getFirst().getColumnName());
    }

    @Test
    void testGroupByTableColumns() {
        String queryStatement = """
                SELECT country, city, AVG(order_amount) AS average_order
                FROM orders o
                GROUP BY o.country, o.city;
                """;

        Query query = sqlParserService.parseSelectStatement(queryStatement);

        assertNotNull(query.getGroupBy());
        assertNotNull(query.getGroupBy().getColumns());
        assertEquals(2, query.getGroupBy().getColumns().size());
        assertEquals("o", query.getGroupBy().getColumns().getFirst().getTable());
        assertEquals("country", query.getGroupBy().getColumns().getFirst().getColumnName());
        assertEquals("o", query.getGroupBy().getColumns().getLast().getTable());
        assertEquals("city", query.getGroupBy().getColumns().getLast().getColumnName());
    }

    @Test
    void testGroupByFunction() {
        String queryStatement = """
                SELECT YEAR(order_date) AS order_year, SUM(amount) AS total_revenue
                FROM orders
                GROUP BY YEAR(order_date);
                """;

        Query query = sqlParserService.parseSelectStatement(queryStatement);

        assertNotNull(query.getGroupBy());
        assertNotNull(query.getGroupBy().getColumns());
        assertTrue(query.getGroupBy().isFunction());
        assertEquals("YEAR", query.getGroupBy().getFunction());
        assertEquals(1, query.getGroupBy().getColumns().size());
        assertEquals("order_date", query.getGroupBy().getColumns().getFirst().getColumnName());
    }

    @Test
    void testGroupByFunctionWithTwoColumn() {
        String queryStatement = """
                SELECT category, brand, COUNT(*) AS num_products
                FROM products
                GROUP BY ROLLUP(category, brand);
                """;

        Query query = sqlParserService.parseSelectStatement(queryStatement);

        assertNotNull(query.getGroupBy());
        assertNotNull(query.getGroupBy().getColumns());
        assertTrue(query.getGroupBy().isFunction());
        assertEquals("ROLLUP", query.getGroupBy().getFunction());
        assertEquals(2, query.getGroupBy().getColumns().size());
        assertEquals("category", query.getGroupBy().getColumns().getFirst().getColumnName());
        assertEquals("brand", query.getGroupBy().getColumns().getLast().getColumnName());
    }

    @Test
    void testGroupByThreeColumns() {
        String queryStatement = """
                SELECT category, product_name, price,
                       COUNT(*) AS num_products
                FROM products
                GROUP BY category, product_name, price;
                """;

        Query query = sqlParserService.parseSelectStatement(queryStatement);

        assertNotNull(query.getGroupBy());
        assertNotNull(query.getGroupBy().getColumns());
        assertEquals(3, query.getGroupBy().getColumns().size());
        assertEquals("category", query.getGroupBy().getColumns().getFirst().getColumnName());
        assertEquals("product_name", query.getGroupBy().getColumns().get(1).getColumnName());
        assertEquals("price", query.getGroupBy().getColumns().getLast().getColumnName());
    }

}
