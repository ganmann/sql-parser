package com.sql.util.sqlparser.errorHandling.exceptions;

public class SqlValidationException extends RuntimeException{
    public SqlValidationException() {
        super("Unsupported SQL Format");
    }
}