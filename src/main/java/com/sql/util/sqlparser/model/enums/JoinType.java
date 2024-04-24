package com.sql.util.sqlparser.model.enums;

public enum JoinType {
    JOIN,
    LEFT_JOIN,
    RIGHT_JOIN,
    FULL_JOIN;


    public static JoinType getJoinType(String joinType) {
        switch (joinType.trim().toLowerCase()) {
            case "join", "inner join" -> {
                return JOIN;
            }
            case "left join", "left outer join" -> {
                return LEFT_JOIN;
            }
            case "right join", "right outer join" -> {
                return RIGHT_JOIN;
            }
            case "full join", "full outer join" -> {
                return FULL_JOIN;
            }
            default -> {
                return null;
            }
        }
    }
}
