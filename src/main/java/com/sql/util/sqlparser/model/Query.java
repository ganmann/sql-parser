package com.sql.util.sqlparser.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Query {
    private List<String> columns;
    private List<String> fromSources;
    private List<String> joins;
    private List<String> whereClauses;
    private List<String> groupByColumns;
    private List<String> sortColumns;
    private Integer limit;
    private Integer offset;
}
