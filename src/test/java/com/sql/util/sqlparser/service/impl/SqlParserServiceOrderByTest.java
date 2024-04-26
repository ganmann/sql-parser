package com.sql.util.sqlparser.service.impl;


import com.sql.util.sqlparser.model.OrderByColumn;
import com.sql.util.sqlparser.model.Query;
import com.sql.util.sqlparser.model.enums.SortingOrder;
import com.sql.util.sqlparser.service.SqlParserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SqlParserServiceOrderByTest {

    @Autowired
    SqlParserService sqlParserService;

    @Test
    void parseOrderByWithoutOrderBy() {
        String sql = """
                SELECT *
                FROM customers
                """;

        Query query = sqlParserService.parseSelectStatement(sql);

        assertNull(query.getOrderBy());
    }

    @Test
    void parseOrderByColumnTest() {
        String sql = """
                SELECT *
                FROM customers
                ORDER BY name;
                """;

        Query query = sqlParserService.parseSelectStatement(sql);

        assertNotNull(query.getOrderBy());
        assertNotNull(query.getOrderBy().getOrderByColumns());
        assertEquals(1, query.getOrderBy().getOrderByColumns().size());

        OrderByColumn orderByColumn = query.getOrderBy().getOrderByColumns().getFirst();
        assertEquals(SortingOrder.ASC, orderByColumn.getSortingOrder());
        assertNotNull(orderByColumn.getColumn());
        assertEquals("name", orderByColumn.getColumn().getColumnName());

    }

    @Test
    void parseOrderByColumnWithCommentTest() {
        String sql = """
                SELECT *
                FROM customers
                ORDER BY name;  -- Sorts by customer name in ascending order (default)
                """;


        Query query = sqlParserService.parseSelectStatement(sql);

        assertNotNull(query.getOrderBy());
        assertNotNull(query.getOrderBy().getOrderByColumns());
        assertEquals(1, query.getOrderBy().getOrderByColumns().size());

        OrderByColumn orderByColumn = query.getOrderBy().getOrderByColumns().getFirst();
        assertEquals(SortingOrder.ASC, orderByColumn.getSortingOrder());
        assertNotNull(orderByColumn.getColumn());
        assertEquals("name", orderByColumn.getColumn().getColumnName());
    }

    @Test
    void parseOrderByMultipleColumnsTest() {
        String sql = """
                SELECT *
                FROM orders
                ORDER BY order_date DESC, amount ASC;
                """;

        Query query = sqlParserService.parseSelectStatement(sql);

        assertNotNull(query.getOrderBy());
        assertNotNull(query.getOrderBy().getOrderByColumns());
        assertEquals(2, query.getOrderBy().getOrderByColumns().size());

        OrderByColumn orderByColumn = query.getOrderBy().getOrderByColumns().getFirst();
        assertEquals(SortingOrder.DESC, orderByColumn.getSortingOrder());
        assertNotNull(orderByColumn.getColumn());
        assertEquals("order_date", orderByColumn.getColumn().getColumnName());

        OrderByColumn orderByColumn2 = query.getOrderBy().getOrderByColumns().getLast();
        assertEquals(SortingOrder.ASC, orderByColumn2.getSortingOrder());
        assertNotNull(orderByColumn2.getColumn());
        assertEquals("amount", orderByColumn2.getColumn().getColumnName());
    }

    @Test
    void parseOrderByMultipleColumnsDifferentOrderTest() {
        String sql = """
                SELECT product_name, category, price, stock
                FROM products
                ORDER BY category DESC, price asc, stock DESC;
                """;

        Query query = sqlParserService.parseSelectStatement(sql);

        assertNotNull(query.getOrderBy());
        assertNotNull(query.getOrderBy().getOrderByColumns());
        assertEquals(3, query.getOrderBy().getOrderByColumns().size());

        OrderByColumn orderByColumn = query.getOrderBy().getOrderByColumns().getFirst();
        assertEquals(SortingOrder.DESC, orderByColumn.getSortingOrder());
        assertEquals("category", orderByColumn.getColumn().getColumnName());

        OrderByColumn orderByColumn2 = query.getOrderBy().getOrderByColumns().get(1);
        assertEquals(SortingOrder.ASC, orderByColumn2.getSortingOrder());
        assertEquals("price", orderByColumn2.getColumn().getColumnName());

        OrderByColumn orderByColumn3 = query.getOrderBy().getOrderByColumns().getLast();
        assertEquals(SortingOrder.DESC, orderByColumn3.getSortingOrder());
        assertEquals("stock", orderByColumn3.getColumn().getColumnName());
    }


    @Test
    void parseOrderByColumnPositionTest() {
        String sql = """
                SELECT product_name, category, price
                FROM products
                ORDER BY 2, 3 DESC;
                """;

        Query query = sqlParserService.parseSelectStatement(sql);

        assertNotNull(query.getOrderBy());
        assertNotNull(query.getOrderBy().getOrderByColumns());
        assertEquals(2, query.getOrderBy().getOrderByColumns().size());

        OrderByColumn orderByColumn = query.getOrderBy().getOrderByColumns().getFirst();
        assertEquals(SortingOrder.ASC, orderByColumn.getSortingOrder());
        assertTrue(orderByColumn.isColumnNumber());
        assertFalse(orderByColumn.isNestedQuery());
        assertEquals(2, orderByColumn.getColumnNumber());

        OrderByColumn orderByColumn2 = query.getOrderBy().getOrderByColumns().getLast();
        assertEquals(SortingOrder.DESC, orderByColumn2.getSortingOrder());
        assertTrue(orderByColumn2.isColumnNumber());
        assertFalse(orderByColumn2.isNestedQuery());
        assertEquals(3, orderByColumn2.getColumnNumber());
    }


    @Test
    void parseOrderByAliasTest() {
        String sql = """
                SELECT first_name || ' ' || last_name AS full_name, email
                FROM customers
                ORDER BY full_name desc;
                """;

        Query query = sqlParserService.parseSelectStatement(sql);

        assertNotNull(query.getOrderBy());
        assertNotNull(query.getOrderBy().getOrderByColumns());
        assertEquals(1, query.getOrderBy().getOrderByColumns().size());

        OrderByColumn orderByColumn = query.getOrderBy().getOrderByColumns().getFirst();
        assertEquals(SortingOrder.DESC, orderByColumn.getSortingOrder());
        assertEquals("full_name", orderByColumn.getColumn().getColumnName());
    }

    @Test
    void parseOrderBySubquery() {
        String sql = """
                SELECT *
                FROM customers
                ORDER BY (SELECT COUNT(*) FROM orders WHERE customer_id = customers.customer_id) DESC;
                """;

        Query query = sqlParserService.parseSelectStatement(sql);

        assertNotNull(query.getOrderBy());
        assertNotNull(query.getOrderBy().getOrderByColumns());
        assertEquals(1, query.getOrderBy().getOrderByColumns().size());

        OrderByColumn orderByColumn = query.getOrderBy().getOrderByColumns().getFirst();
        assertEquals(SortingOrder.DESC, orderByColumn.getSortingOrder());
        assertTrue(orderByColumn.isNestedQuery());
        assertEquals(3, orderByColumn.getNestedQuery().sizeOfStatementParts());
    }
}
