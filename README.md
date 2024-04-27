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
    private Join joins;
    private WhereClause whereClause;
    private GroupBy groupBy;
    private Having having;
    private OrderBy orderBy;
    private Integer limit;
    private Integer offset;
}
```

### Power

- Parse any level of subquery in Select, From and Join and conditions
- No matter about whitespaces

#### Select
- Parse 'table', 'table.col', 'table.col col1', 'table.col as col1' into Column object
- Identify functions and literals

#### From
- Parse any level of subquery
- Parse into List of Tables with table name and alias

#### Join 
- Parse into List of Join Clauses with Join Type and Table
- Parse join conditions into Predicate object
  - Predicate has either PredicateRelation with logical operator and next Predicate or/and nested predicate for parenthesis conditions  
  - Predicate has a comparison type (>,<, =, <>, <=, >=, like, is null)
  - Predicate has left and right operand. Operand is parsed to column, expression, function, or nested query 

#### Where
- Parse to Predicate object with full hierarchy

#### Group by
- Parse into columns objects and function is used for grouping

#### Having
- Parse to Predicate object with full hierarchy

#### Order By 
- Parse sorting columns, column numbers and sorting order 

#### Additional
- parse limit and offset
- each query component save initial part of query for any construction cannot be parsed

## Example
Here's an example of how to use the API endpoint:

### Request

```sql
SELECT o.order_id, c.customer_name, SUM(oi.quantity * oi.price) AS total_order_amount,
       (
           SELECT AVG(total_order_amount)
           FROM (
               SELECT o2.order_id, SUM(oi2.quantity * oi2.price) AS total_order_amount
               FROM orders o2
               JOIN order_items oi2 ON o2.order_id = oi2.order_id
               GROUP BY o2.order_id
           ) AS avg_order
       ) AS average_order_value
FROM customers c
JOIN orders o ON c.customer_id = o.customer_id
JOIN order_items oi ON o.order_id = oi.order_id
WHERE o.order_date >= '2023-01-01' and (c.city = 'New York' OR c.city = 'Los Angeles')
GROUP BY o.order_id, c.customer_name
HAVING SUM(oi.quantity * oi.price) > 100
ORDER BY total_order_amount DESC
LIMIT 10;
```


```
POST /sql-parser/api/v1/select
{
   "query": "SELECT o.order_id, c.customer_name, SUM(oi.quantity * oi.price) AS total_order_amount, ( SELECT AVG(total_order_amount) FROM ( SELECT o2.order_id, SUM(oi2.quantity * oi2.price) AS total_order_amount FROM orders o2 JOIN order_items oi2 ON o2.order_id = oi2.order_id GROUP BY o2.order_id ) AS avg_order ) AS average_order_value FROM customers c JOIN orders o ON c.customer_id = o.customer_id JOIN order_items oi ON o.order_id = oi.order_id WHERE o.order_date >= '2023-01-01' and (c.city = 'New York' OR c.city = 'Los Angeles') GROUP BY o.order_id, c.customer_name HAVING SUM(oi.quantity * oi.price) > 100 ORDER BY total_order_amount DESC LIMIT 10;"
}
```

### Response

```json
{
  "initialStatement": "SELECT o.order_id, c.customer_name, SUM(oi.quantity * oi.price) AS total_order_amount, ( SELECT AVG(total_order_amount) FROM ( SELECT o2.order_id, SUM(oi2.quantity * oi2.price) AS total_order_amount FROM orders o2 JOIN order_items oi2 ON o2.order_id = oi2.order_id GROUP BY o2.order_id ) AS avg_order ) AS average_order_value FROM customers c JOIN orders o ON c.customer_id = o.customer_id JOIN order_items oi ON o.order_id = oi.order_id WHERE o.order_date >= '2023-01-01' and (c.city = 'New York' OR c.city = 'Los Angeles') GROUP BY o.order_id, c.customer_name HAVING SUM(oi.quantity * oi.price) > 100 ORDER BY total_order_amount DESC LIMIT 10;",
  "select": {
    "initialStatement": "SELECT o.order_id, c.customer_name, SUM(oi.quantity * oi.price) AS total_order_amount, ( SELECT AVG(total_order_amount) FROM ( SELECT o2.order_id, SUM(oi2.quantity * oi2.price) AS total_order_amount FROM orders o2 JOIN order_items oi2 ON o2.order_id = oi2.order_id GROUP BY o2.order_id ) AS avg_order ) AS average_order_value",
    "selectExpressions": [{
      "initialStatement": " o.order_id",
      "column": {
        "initialStatement": null,
        "table": "o",
        "columnName": "order_id"
      },
      "nestedQuery": null,
      "alias": null,
      "literal": false,
      "function": false
    }, {
      "initialStatement": " c.customer_name",
      "column": {
        "initialStatement": null,
        "table": "c",
        "columnName": "customer_name"
      },
      "nestedQuery": null,
      "alias": null,
      "literal": false,
      "function": false
    }, {
      "initialStatement": " SUM(oi.quantity * oi.price) AS total_order_amount",
      "column": null,
      "nestedQuery": null,
      "alias": "total_order_amount",
      "literal": false,
      "function": true
    }, {
      "initialStatement": " ( SELECT AVG(total_order_amount) FROM ( SELECT o2.order_id, SUM(oi2.quantity * oi2.price) AS total_order_amount FROM orders o2 JOIN order_items oi2 ON o2.order_id = oi2.order_id GROUP BY o2.order_id ) AS avg_order ) AS average_order_value",
      "column": null,
      "nestedQuery": {
        "initialStatement": " SELECT AVG(total_order_amount) FROM ( SELECT o2.order_id, SUM(oi2.quantity * oi2.price) AS total_order_amount FROM orders o2 JOIN order_items oi2 ON o2.order_id = oi2.order_id GROUP BY o2.order_id ) AS avg_order ",
        "select": {
          "initialStatement": " SELECT AVG(total_order_amount)",
          "selectExpressions": [{
            "initialStatement": " AVG(total_order_amount)",
            "column": null,
            "nestedQuery": null,
            "alias": null,
            "literal": false,
            "function": true
          }]
        },
        "from": {
          "initialStatement": "FROM ( SELECT o2.order_id, SUM(oi2.quantity * oi2.price) AS total_order_amount FROM orders o2 JOIN order_items oi2 ON o2.order_id = oi2.order_id GROUP BY o2.order_id ) AS avg_order",
          "tables": [{
            "initialStatement": " ( SELECT o2.order_id, SUM(oi2.quantity * oi2.price) AS total_order_amount FROM orders o2 JOIN order_items oi2 ON o2.order_id = oi2.order_id GROUP BY o2.order_id ) AS avg_order",
            "tableName": null,
            "alias": "AS avg_order",
            "nestedQuery": {
              "initialStatement": " SELECT o2.order_id, SUM(oi2.quantity * oi2.price) AS total_order_amount FROM orders o2 JOIN order_items oi2 ON o2.order_id = oi2.order_id GROUP BY o2.order_id ",
              "select": {
                "initialStatement": " SELECT o2.order_id, SUM(oi2.quantity * oi2.price) AS total_order_amount",
                "selectExpressions": [{
                  "initialStatement": " o2.order_id",
                  "column": {
                    "initialStatement": null,
                    "table": "o2",
                    "columnName": "order_id"
                  },
                  "nestedQuery": null,
                  "alias": null,
                  "literal": false,
                  "function": false
                }, {
                  "initialStatement": " SUM(oi2.quantity * oi2.price) AS total_order_amount",
                  "column": null,
                  "nestedQuery": null,
                  "alias": "total_order_amount",
                  "literal": false,
                  "function": true
                }]
              },
              "from": {
                "initialStatement": "FROM orders o2",
                "tables": [{
                  "initialStatement": " orders o2",
                  "tableName": "orders",
                  "alias": "o2",
                  "nestedQuery": null
                }]
              },
              "joins": {
                "initialStatement": "JOIN order_items oi2 ON o2.order_id = oi2.order_id",
                "joinClauses": [{
                  "initialStatement": "JOIN order_items oi2 ON o2.order_id = oi2.order_id",
                  "joinType": "JOIN",
                  "table": {
                    "initialStatement": "order_items oi2",
                    "tableName": "order_items",
                    "alias": "oi2",
                    "nestedQuery": null
                  },
                  "joinKeys": {
                    "initialStatement": "o2.order_id = oi2.order_id",
                    "leftOperand": {
                      "initialStatement": "o2.order_id",
                      "column": {
                        "initialStatement": null,
                        "table": "o2",
                        "columnName": "order_id"
                      },
                      "expression": null,
                      "function": null,
                      "nestedQuery": null
                    },
                    "rightOperand": {
                      "initialStatement": "oi2.order_id",
                      "column": {
                        "initialStatement": null,
                        "table": "oi2",
                        "columnName": "order_id"
                      },
                      "expression": null,
                      "function": null,
                      "nestedQuery": null
                    },
                    "comparison": "=",
                    "nestedPredicate": null,
                    "predicateRelation": null
                  }
                }]
              },
              "whereClause": null,
              "groupBy": {
                "initialStatement": "GROUP BY o2.order_id",
                "columns": [{
                  "initialStatement": null,
                  "table": "o2",
                  "columnName": "order_id"
                }],
                "function": null
              },
              "having": null,
              "orderBy": null,
              "limit": null,
              "offset": null
            }
          }]
        },
        "joins": null,
        "whereClause": null,
        "groupBy": null,
        "having": null,
        "orderBy": null,
        "limit": null,
        "offset": null
      },
      "alias": "AS average_order_value",
      "literal": false,
      "function": false
    }]
  },
  "from": {
    "initialStatement": "FROM customers c",
    "tables": [{
      "initialStatement": " customers c",
      "tableName": "customers",
      "alias": "c",
      "nestedQuery": null
    }]
  },
  "joins": {
    "initialStatement": "JOIN orders o ON c.customer_id = o.customer_id JOIN order_items oi ON o.order_id = oi.order_id",
    "joinClauses": [{
      "initialStatement": "JOIN orders o ON c.customer_id = o.customer_id",
      "joinType": "JOIN",
      "table": {
        "initialStatement": "orders o",
        "tableName": "orders",
        "alias": "o",
        "nestedQuery": null
      },
      "joinKeys": {
        "initialStatement": "c.customer_id = o.customer_id",
        "leftOperand": {
          "initialStatement": "c.customer_id",
          "column": {
            "initialStatement": null,
            "table": "c",
            "columnName": "customer_id"
          },
          "expression": null,
          "function": null,
          "nestedQuery": null
        },
        "rightOperand": {
          "initialStatement": "o.customer_id",
          "column": {
            "initialStatement": null,
            "table": "o",
            "columnName": "customer_id"
          },
          "expression": null,
          "function": null,
          "nestedQuery": null
        },
        "comparison": "=",
        "nestedPredicate": null,
        "predicateRelation": null
      }
    }, {
      "initialStatement": "JOIN order_items oi ON o.order_id = oi.order_id",
      "joinType": "JOIN",
      "table": {
        "initialStatement": "order_items oi",
        "tableName": "order_items",
        "alias": "oi",
        "nestedQuery": null
      },
      "joinKeys": {
        "initialStatement": "o.order_id = oi.order_id",
        "leftOperand": {
          "initialStatement": "o.order_id",
          "column": {
            "initialStatement": null,
            "table": "o",
            "columnName": "order_id"
          },
          "expression": null,
          "function": null,
          "nestedQuery": null
        },
        "rightOperand": {
          "initialStatement": "oi.order_id",
          "column": {
            "initialStatement": null,
            "table": "oi",
            "columnName": "order_id"
          },
          "expression": null,
          "function": null,
          "nestedQuery": null
        },
        "comparison": "=",
        "nestedPredicate": null,
        "predicateRelation": null
      }
    }]
  },
  "whereClause": {
    "initialStatement": "WHERE o.order_date >= '2023-01-01' and (c.city = 'New York' OR c.city = 'Los Angeles')",
    "predicate": {
      "initialStatement": "o.order_date >= '2023-01-01'",
      "leftOperand": {
        "initialStatement": "o.order_date",
        "column": {
          "initialStatement": null,
          "table": "o",
          "columnName": "order_date"
        },
        "expression": null,
        "function": null,
        "nestedQuery": null
      },
      "rightOperand": {
        "initialStatement": "'2023-01-01'",
        "column": null,
        "expression": "'2023-01-01'",
        "function": null,
        "nestedQuery": null
      },
      "comparison": ">=",
      "nestedPredicate": null,
      "predicateRelation": {
        "logicalOperator": "AND",
        "nextPredicate": {
          "initialStatement": "(c.city = 'New York' OR c.city = 'Los Angeles')",
          "leftOperand": null,
          "rightOperand": null,
          "comparison": null,
          "nestedPredicate": {
            "initialStatement": "c.city = 'New York'",
            "leftOperand": {
              "initialStatement": "c.city",
              "column": {
                "initialStatement": null,
                "table": "c",
                "columnName": "city"
              },
              "expression": null,
              "function": null,
              "nestedQuery": null
            },
            "rightOperand": {
              "initialStatement": "'New York'",
              "column": null,
              "expression": "'New York'",
              "function": null,
              "nestedQuery": null
            },
            "comparison": "=",
            "nestedPredicate": null,
            "predicateRelation": {
              "logicalOperator": "OR",
              "nextPredicate": {
                "initialStatement": "c.city = 'Los Angeles'",
                "leftOperand": {
                  "initialStatement": "c.city",
                  "column": {
                    "initialStatement": null,
                    "table": "c",
                    "columnName": "city"
                  },
                  "expression": null,
                  "function": null,
                  "nestedQuery": null
                },
                "rightOperand": {
                  "initialStatement": "'Los Angeles'",
                  "column": null,
                  "expression": "'Los Angeles'",
                  "function": null,
                  "nestedQuery": null
                },
                "comparison": "=",
                "nestedPredicate": null,
                "predicateRelation": null
              }
            }
          },
          "predicateRelation": null
        }
      }
    }
  },
  "groupBy": {
    "initialStatement": "GROUP BY o.order_id, c.customer_name",
    "columns": [{
      "initialStatement": null,
      "table": "o",
      "columnName": "order_id"
    }, {
      "initialStatement": null,
      "table": "c",
      "columnName": "customer_name"
    }],
    "function": null
  },
  "having": {
    "initialStatement": "HAVING SUM(oi.quantity * oi.price) > 100",
    "predicate": {
      "initialStatement": "SUM(oi.quantity * oi.price) > 100",
      "leftOperand": {
        "initialStatement": "SUM(oi.quantity * oi.price)",
        "column": null,
        "expression": "oi.quantity * oi.price",
        "function": "SUM",
        "nestedQuery": null
      },
      "rightOperand": {
        "initialStatement": "100",
        "column": null,
        "expression": "100",
        "function": null,
        "nestedQuery": null
      },
      "comparison": ">",
      "nestedPredicate": null,
      "predicateRelation": null
    }
  },
  "orderBy": {
    "initialStatement": "ORDER BY total_order_amount DESC",
    "orderByColumns": [{
      "initialStatement": "total_order_amount DESC",
      "column": {
        "initialStatement": null,
        "table": null,
        "columnName": "total_order_amount"
      },
      "columnNumber": null,
      "sortingOrder": "DESC",
      "nestedQuery": null
    }]
  },
  "limit": 10,
  "offset": null
}
```

