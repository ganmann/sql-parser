package com.sql.util.sqlparser.model.enums;

public enum LogicalOperator {
    AND,
    OR,
    XOR;

    public static LogicalOperator getLogicalOperator(String value) {
        switch (value.trim().toLowerCase()) {
            case "and" -> {
                return AND;
            }
            case "or" -> {
                return OR;
            }
            case "xor" -> {
                return XOR;
            }
            default -> {
                return null;
            }
        }
    }
}
