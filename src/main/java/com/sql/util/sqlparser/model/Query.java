package com.sql.util.sqlparser.model;

import lombok.*;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Query {
    private String initialStatement;
    private Select select;
    private From from;
    private Join join;
    private WhereClause whereClause;
    private GroupBy groupBy;
    private Having having;
    private Sort sortColumns;
    private Integer limit;
    private Integer offset;

    public Query(String initialQuery) {
        this.initialStatement = initialQuery;
    }

    public int sizeOfStatementParts() {
        int size = 0;
        if (select != null) size++;
        if (from != null) size++;
        if (join != null) size++;
        if (whereClause != null) size++;
        if (groupBy != null) size++;
        if (having != null) size++;
        if (sortColumns != null) size++;
        if (limit != null) size++;
        if (offset != null) size++;
        return size;
    }
}
