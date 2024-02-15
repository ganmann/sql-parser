package com.sql.util.sqlparser;

import com.sql.util.sqlparser.model.factory.AbstractQueryElementFactory;
import com.sql.util.sqlparser.model.factory.QueryElementFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SqlParserApplication {

	public static void main(String[] args) {
		SpringApplication.run(SqlParserApplication.class, args);
	}

}
