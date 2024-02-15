package com.sql.util.sqlparser.errorHandling.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class SqlValidationAdvice {
    @ResponseBody
    @ExceptionHandler(SqlValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String sqlValidationAdvice(SqlValidationException ex) {
        return ex.getMessage();
    }
}
