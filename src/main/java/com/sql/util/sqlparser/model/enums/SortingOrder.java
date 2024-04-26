package com.sql.util.sqlparser.model.enums;

public enum SortingOrder {
    ASC,
    DESC;

    public static SortingOrder getSortingOrder(String value) {
        switch (value.trim().toLowerCase()) {
            case "asc" -> {
                return ASC;
            }
            case "desc" -> {
                return DESC;
            }
            default -> {
                return null;
            }
        }
    }
}
