package com.sql.util.sqlparser.service.impl;

import com.sql.util.sqlparser.model.Query;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SqlParserServiceJoinTest {

    @Test
    void parseQuery_leftAndRightJoins() {

        String statement = """
                SELECT author.name, count(book.id), sum(book.cost)\s
                FROM author\s
                LEFT  JOIN book ON (author.id = book.author_id)
                RIGHT JOIN course ON (course.id = book.course_id)\s
                LIMIT 10;""";

        Query query = new SqlParserServiceImpl().parse(statement);
        assertEquals(2, query.getJoins().getJoinClauses().size());
    }
}
