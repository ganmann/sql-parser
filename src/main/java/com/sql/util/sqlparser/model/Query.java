package com.sql.util.sqlparser.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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

    public void setQueryElement(QueryComponent queryComponent) {
        if (queryComponent instanceof Select) {
            setSelect((Select) queryComponent);
        } else if (queryComponent instanceof From) {
            setFrom((From) queryComponent);
        } else if (queryComponent instanceof Join) {
            setJoin((Join) queryComponent);
        } else if (queryComponent instanceof WhereClause) {
            setWhereClause((WhereClause) queryComponent);
        } else if (queryComponent instanceof Having) {
            setHaving((Having) queryComponent);
        } else if (queryComponent instanceof GroupBy) {
            setGroupBy((GroupBy) queryComponent);
        } else if (queryComponent instanceof Sort) {
            setSortColumns((Sort) queryComponent);
        }
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
