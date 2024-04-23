package com.sql.util.sqlparser.constants;

import lombok.experimental.UtilityClass;

import java.util.*;

@UtilityClass
public class SQLConstants {

    public final List<String> JOIN_TYPES = Arrays.asList("inner join", "left join", "left outer join", "right join", "right outer join", "full join", "full outer join");


    public final Map<String, List<String>> SELECT_STATEMENT_TOKENS = Map.of(
            "select", List.of("from"),
            "from", List.of("where", "join", "inner join", "left join", "left outer join", "right join",
                    "right outer join", "full join", "full outer join", "group by", "having", "order by", "limit", "offset"),
            "join", List.of("where", "group by", "having", "order by", "limit", "offset"),
            "where", List.of("group by", "having", "order by", "limit", "offset"),
            "group by", List.of("having", "order by", "limit", "offset"),
            "having", List.of("order by", "limit", "offset"),
            "order by", List.of("limit", "offset"),
            "limit", List.of("offset")
    );
    public final Map<Character, Character> OPEN_CLOSED_CHARACTER_MAP = Map.of(
            '(', ')',
            '"','"',
            '\'','\''
    );


    public final Set<Character> OPEN_CHARACTERS = OPEN_CLOSED_CHARACTER_MAP.keySet();
    public final Set<Character> OPEN_CLOSED_SAME_CHARACTERS = Set.of('"','\'');
}
