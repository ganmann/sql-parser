package com.sql.util.sqlparser;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
class SqlParserApplicationTests {

	@Test
	void contextLoads() {
	}


	@Test
	void testSplitting() {

		String sql = "select a_alias.id, (select count(*) from b where b.id=a_alias.id) from (select * from A) a_alias";

		for (char ch: sql.toCharArray()) {

		}
	}

	@Test
	@Disabled
	void testRegExp_SelectSelectFromLeftJoinGroupByHavingLimit() {

		String sql = "SELECT author.name, count(book.id), sum(book.cost) (SELECT * from b) " +
				"FROM author " +
				"LEFT JOIN book ON (author.id = book.author_id) " +
				"GROUP BY author.name " +
				"HAVING COUNT(*) > 1 AND SUM(book.cost) > 500 " +
				"LIMIT 10;";


		String pattern = "^SELECT\\s+(.*?)\\s+\\((.*?)\\)\\s+FROM\\s+(.*?)\\s+" +
				"(?:LEFT JOIN\\s+(.*?)\\s+ON\\s+\\((.*?)\\)\\s+)?" +
				"(?:GROUP BY\\s+(.*?)\\s+)?" +
				"(?:HAVING\\s+(.*?)\\s+)?" +
				"(?:LIMIT\\s+(\\d+))?;$";

		Pattern regex = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		Matcher matcher = regex.matcher(sql);

		if (matcher.find()) {
			String selectClause = matcher.group(1);
			String fromClause = matcher.group(2);
			String leftJoinTable = matcher.group(3);
			String joinCondition = matcher.group(4);
			String groupByClause = matcher.group(5);
			String havingClause = matcher.group(6);
			String limit = matcher.group(7);

			System.out.println("SELECT Clause: " + selectClause);
			System.out.println("FROM Clause: " + fromClause);
			System.out.println("LEFT JOIN Table: " + leftJoinTable);
			System.out.println("JOIN Condition: " + joinCondition);
			System.out.println("GROUP BY Clause: " + groupByClause);
			System.out.println("HAVING Clause: " + havingClause);
			System.out.println("LIMIT: " + limit);
		} else {
			System.out.println("Invalid SQL SELECT statement");
		}

	}


	@Test
	@Disabled
	void testRegExp_SelectSelectFromSelect() {
		String sql = "select a_alias.id, (select count(*) from b where b.id=a_alias.id) from (select * from A) a_alias";

// Regular expression pattern to find the "select" part
		String selectPattern = "^SELECT\\s+(.*?)\\s+FROM.*?(?=\\bFROM\\b)(?![^()]*\\))";
//		String pattern = "\\bSELECT\\b.*?(?=\\bFROM\\b(?![^(]*\\)))";

		String fromPattern = "^SELECT.*?\\s+FROM\\s+(.*?)\\s+(?=\\bWHERE\\b|\\bGROUP\\b|\\bHAVING\\b|\\bORDER\\b|;|$)(?![^()]*\\))";

		Pattern selectRegex = Pattern.compile(selectPattern, Pattern.CASE_INSENSITIVE);
		Pattern fromRegex = Pattern.compile(fromPattern, Pattern.CASE_INSENSITIVE);
		Matcher matcherSelect = selectRegex.matcher(sql);
		Matcher matcherFrom = fromRegex.matcher(sql);

		if (matcherSelect.find() || matcherFrom.find()) {
			String selectClause = matcherSelect.group(1);
//			String fromClause = matcherFrom.group(1);

			System.out.println("SELECT Clause: " + selectClause);
//			System.out.println("FROM Clause: " + fromClause);
		} else {
			System.out.println("Invalid SQL SELECT statement");
		}

	}

}
