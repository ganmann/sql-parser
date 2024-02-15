package com.sql.util.sqlparser.utils;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SQLUtilsTest {

    @Test
    void checkTokenMatch_LeftJoinOnNewLineAnd1SpaceBetween_True() {

        String statementPart = """
                LEFT JOIN book ON (author.id = book.author_id)\s
                GROUP BY author.name\s
                HAVING COUNT(*) > 1 AND SUM(book.cost) > 500
                LIMIT 10;""";
        String token = "left join";

        assertTrue(SQLUtils.checkTokenMatch.test(statementPart, token));

    }

    @Test
    void checkTokenMatch_LeftOuterJoinJoinOnNewLineAndSpaceAnd1SpaceBetween_True() {

        String statementPart = """
                  
                LEFT outer  JOIN book ON (author.id = book.author_id)\s
                GROUP BY author.name\s
                HAVING COUNT(*) > 1 AND SUM(book.cost) > 500
                LIMIT 10;""";
        String token = "left outer join";

        assertTrue(SQLUtils.checkTokenMatch.test(statementPart, token));

    }

    @Test
    void checkTokenMatch_RightJoinOnNewLineAndWithoutSpaceBetween_false() {

        String statementPart = """
                  
                rightJOIN book ON (author.id = book.author_id)\s
                GROUP BY author.name\s
                HAVING COUNT(*) > 1 AND SUM(book.cost) > 500
                LIMIT 10;""";
        String token = "right join";

        assertFalse(SQLUtils.checkTokenMatch.test(statementPart, token));

    }

    @Test
    void checkTokenMatch_RightJoinConnectedWithNextWord_false() {

        String statementPart = """
                right JOINbook ON (author.id = book.author_id)\s
                GROUP BY author.name\s
                HAVING COUNT(*) > 1 AND SUM(book.cost) > 500
                LIMIT 10;""";
        String token = "right join";

        assertFalse(SQLUtils.checkTokenMatch.test(statementPart, token));

    }

    @Test
    void checkTokenMatch_EmptyLine_false() {

        String statementPart = "";
        String token = "from";

        assertFalse(SQLUtils.checkTokenMatch.test(statementPart, token));

    }


    @Test
    void checkTokenMatch_FromStartOfStr_true() {

        String statementPart = """
                FROM author, book \s
                GROUP BY author.name\s
                HAVING COUNT(*) > 1 AND SUM(book.cost) > 500
                LIMIT 10;""";
        String token = "from";

        assertTrue(SQLUtils.checkTokenMatch.test(statementPart, token));

    }

    @Test
    void checkTokenMatch_TokenInMiddle_false() {

        String statementPart = """
                FROM author, book \s
                GROUP BY author.name\s
                HAVING COUNT(*) > 1 AND SUM(book.cost) > 500
                LIMIT 10;""";
        String token = "group by";

        assertFalse(SQLUtils.checkTokenMatch.test(statementPart, token));

    }

}