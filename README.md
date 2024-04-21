# sql-parser

## Overview
SQL parser is an application that provides an HTTP POST API endpoint for parsing SQL SELECT statements. It accepts a JSON request containing a SELECT statement and returns a parsed representation of the query.

## API Endpoint
HTTP POST `/sql-parser/api/v1/select`

### Request JSON
```json
{
  "query": "<Select Statement>"
}
```

### Response Object
```java
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
}
```

## Example
Here's an example of how to use the API endpoint:

### Request

```
POST /sql-parser/api/v1/select
{
   "query": "select a_alias.id, (select count(*) from b where b.id=a_alias.id) t1 from (select * from A) a_alias"
}
```

### Response

```json
{
    "initialStatement": "select a_alias.id, (select count(*) from b where b.id=a_alias.id) t1 from (select * from A) a_alias",
    "select": {
        "initialStatement": "select a_alias.id, (select count(*) from b where b.id=a_alias.id) t1",
        "columns": [
            {
                "initialStatement": " a_alias.id",
                "table": null,
                "columnName": null,
                "alias": null,
                "nestedQuery": null
            },
            {
                "initialStatement": " (select count(*) from b where b.id=a_alias.id) t1",
                "table": null,
                "columnName": null,
                "alias": "t1",
                "nestedQuery": {
                    "initialStatement": "select count(*) from b where b.id=a_alias.id",
                    "query": {
                        "initialStatement": "select count(*) from b where b.id=a_alias.id",
                        "select": {
                            "initialStatement": "select count(*)",
                            "columns": [
                                {
                                    "initialStatement": " count(*)",
                                    "table": null,
                                    "columnName": null,
                                    "alias": null,
                                    "nestedQuery": null
                                }
                            ]
                        },
                        "from": {
                            "initialStatement": "from b"
                        },
                        "join": null,
                        "whereClause": {
                            "initialStatement": "where b.id=a_alias.id"
                        },
                        "groupBy": null,
                        "having": null,
                        "sortColumns": null,
                        "limit": null,
                        "offset": null
                    }
                }
            }
        ]
    },
    "from": {
        "initialStatement": "from (select * from A) a_alias"
    },
    "join": null,
    "whereClause": null,
    "groupBy": null,
    "having": null,
    "sortColumns": null,
    "limit": null,
    "offset": null
}
```

