package com.sql.util.sqlparser.constants;

import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@UtilityClass
public class SQLConstants {

    final List<String> JOIN_TYPES = Arrays.asList("inner join", "left join", "left outer join", "right join", "right outer join", "full join", "full outer join");

}
