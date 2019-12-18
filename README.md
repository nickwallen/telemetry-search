# Telemetry Search API

This document describes a set of endpoints used to search for telemetry that has been archived within Hive.

## Implementation Notes

We could have used either a completely declarative approach (as described in this document) or a more free-form, SQL-like, DSL.  Either approach has advantages and disadvantages, but we can revisit the approach taken if a more free-form, SQL-like, DSL is advantageous for the UI.

## Endpoints

### POST /api/v1/search

* Description: Searches the archived telemetry stored in Hive.
* Input: 
    * [searchRequest](#request) - Criteria for filtering and sorting the results.
* [Response](#response):
    * 200 - Returns the archived telemetry matching the search request.
    * 404 - The search request is invalid.   

#### Request

The search request contains two top-level fields.
* [`where`](#where)
* [`sort`](#orderBy)

For a detailed example, see the [Request Example](#request-example).

##### `where`

This field provides a list of criteria (predicates) to filter the search results. 

Each criterion (predicate) in the list contains the following fields.
* `field`: The name of a field whose value will be used to filter the results. 
    * The field can contain a wildcard character `*` to match multiple field names at once. See [Wildcards](#wildcards) for more information.
* `op`: The operation (a predicate) used to filter the telemetry. 
    * A basic set of predicates will be provided initially and can be expanded as needed.  See [Predicates](#predicates) for information on supported predicates that can be used here.
* `value`: A list of values used to filter the search results.

Each criterion in the list is combined logically using "and". To express more complex logic, the [`or`](#or) or [`and`](#and) predicates can be used.

```
{
    "where": [
        { 
          "field": "ip_src_addr", 
          "op": "=",
          "value": ["192.168.1.1"]
        },
        { 
          "field": "country",
          "op": "in",
          "value": ["US","GB"]
        }
    ],
    ...
}
```

##### `orderBy`

This field provides criteria to order the search results. 

Each criterion contains two fields.
* `field`: The name of the field to order the results by.
* `order`: The direction in which to sort; either `asc` for ascending or `desc` for descending.

The order of the criteria is important and affects how the results are sorted.


```
{ 
    ...,
    "orderBy": [
        {
          "field": "timestamp",
          "order": "desc"
        },
        {
          "field": "ip_src_addr",
          "order": "asc"
        }
    ]
}
```

##### Wildcards

The field name in the `where` clause can contain a wildcard character `*` to match multiple field names at once.  

For example, if the telemetry contains two fields named `ip_src_addr` and `ip_dst_addr`, the following condition would return a match if either field contains the value `192.168.1.1`.

```
{ 
  "field":"ip_*_addr", 
  "op": "=",
  "value": ["192.168.1.1"]    
}
```

Logically, this is the same as the following.
* `ip_src_addr` = `192.168.1.1` OR `ip_dst_addr` = `192.168.1.1`


##### Predicates

The following operations are supported.
* [`=`](#equals)
* [`<>`](#not-equals)
* [`in`](#in)
* [`not-in`](#not-in)
* [`before`](#before)
* [`after`](#after)
* [`between`](#between)
* [`and`](#and)
* [`or`](#or)

###### Equals

To express `ip_src_addr` is equal to `192.168.1.1`, the `=` operator can be used.
```
{ 
  "field": "ip_src_addr", 
  "op": "=",
  "value": ["192.168.1.1"]
}
```

###### Not Equals

To express `ip_src_addr` is not equal to `192.168.1.1`, the `<>` operator can be used.
```
{ 
  "field": "ip_src_addr", 
  "op": "<>",
  "value": ["192.168.1.1"]
}
```

###### In

To express that a value is part of a set, the `in` operator can be used.
```
{ 
  "field": "country",
  "op": "in",
  "value": ["US", "GB"]
}
```

###### Not In

To express that a value is not part of a set, the `not-in` operator can be used.
```
{ 
  "field": "country",
  "op": "not-in",
  "value": ["RU", "KP"]
}
```

###### Before

To express that a timestamp occurred before a point in time, the `before` operator can be used.
```
{ 
  "field": "timestamp",
  "op": "before",
  "value": ["2020-01-12 09:00:00"]
}
```

* The value of the field (`timestamp` in this example) must be a numeric value representing a timestamp as the number of milliseconds past the Unix epoch.
* The value must be a string representing a timestamp in the form `yyyy-MM-dd HH:mm:ss`.

###### After

To express that a timestamp occurred after a point in time, the `after` operator can be used.
```
{ 
  "field": "timestamp",
  "op": "after",
  "value": ["2019-12-31 03:30:00"]
}
```

* The value of the field (`timestamp` in this example) must be a numeric value representing a timestamp as the number of milliseconds past the Unix epoch.
* The value must be a string representing a timestamp in the form `yyyy-MM-dd HH:mm:ss`.


###### Between

To express that a timestamp occurred between two points in time, the `between` operator can be used.
```
{ 
  "field": "timestamp",
  "op": "between",
  "value": ["2019-12-31 03:30:00", "2020-01-12 09:00:00"]
}
```

* The value of the field (`timestamp` in this example) must be a numeric value representing a timestamp as the number of milliseconds past the Unix epoch.
* The `value` must contain two elements representing a begin and end timestamp.
* Each element of `value` must be a string representing a timestamp in the form `yyyy-MM-dd HH:mm:ss`.

###### And

To express that two predicates should be and'd together, the `and` operator can be used.

For example, the following can be used to express that `ip_src_addr` = `192.168.1.1` AND `country` in [`US`,`GB`].
```
{ 
  "op": "and",
  "value": [
    { 
      "field": "ip_src_addr", 
      "op": "=",
      "value": ["192.168.1.1"]
    },
    { 
      "field": "country",
      "op": "in",
      "value": ["US", "GB"]
    }    
  ]
}
```

The `field` clause is not used with the `and` operator.

The `value` should contain two or more predicates that should be logically and'd.

###### Or

To express that two predicates should be logically or'd together, the `or` operator can be used.

For example, the following can be used to express that `ip_src_addr` = `192.168.1.1` or `192.168.1.2`.
```
{ 
  "op": "or",
  "value": [
    { 
      "field": "ip_src_addr", 
      "op": "=",
      "value": ["192.168.1.1"]
    },
    { 
      "field": "ip_src_addr", 
      "op": "=",
      "value": ["192.168.1.2"]
    }   
  ]
}
```

The `field` clause is not used with the `or` operator.

The `value` should contain two or more predicates that should be logically or'd.

##### Request Example

This example expresses the following search criteria.
* `ip_src_addr` = `192.168.1.1` OR any field named `ip_*` = `10.0.0.1`
* AND `country` is either [`US`, `GB`]
* AND `timestamp` between `2019-12-31 03:30:00` and `2020-01-12 09:00:00`

Any results matching the search criteria are then sorted by:
* `timestamp` descending
* `country` ascending
```
{
  "where": [
    {
      "op": "or",
      "value": [
        { 
          "field": "ip_src_addr", 
          "op": "=",
          "value": ["192.168.0.1"]
        },
        { 
          "field": "ip_*", 
          "op": "=",
          "value": ["10.0.0.1"]
        }
      ]
    },
    { 
      "field": "country",
      "op": "in",
      "value": ["US", "GB"]
    }
    { 
      "field": "timestamp",
      "op": "between",
      "value": ["2019-12-31 03:30:00", "2020-01-12 09:00:00"]
    }   
  ],
  "orderBy": [
    {
      "field": "timestamp",
      "order": "desc"
    },
    {
      "field": "country",
      "order": "asc"
    }
  ]
}
```

#### Response 

The response object contains two top-level fields.
* [columns](#columns)
* [results](#results)

##### `columns`

This field describes all of the columns contained within the resulting data.

This field will be configured statically at application start-time by the user and will never change. The same columns and column names will always be returned. In later iterations, the columns and column names could be made dynamically configurable by the user.

Each column contains two fields.
* `name`: The name of the column.
* `label`: The user-friendly label of the column.

```
{ 
    ...,
    "columns": [
        {
          "name": "ip_src_addr",
          "label": "Source IP"
        },
        {
          "name": "ip_dst_addr",
          "label": "Destination IP"
        }
    ]
}
```

##### `results`

This field contains an array of all the telemetry matching the `where` clause and ordered by the `orderBy` clause.

```
{ 
    ...,
    "results": [
        {
          "ip_src_addr": "192.168.1.1",
          "country": "US",
          ...
        },
        {
          "ip_src_addr": "192.168.1.2",
          "country": "GB",
          ...
        },
        ...
    ]
}
```

##### Response Example

In this example, the archived telemetry only contains 3 fields (`ip_src_addr`, `ip_dst_addr`, `country`)  and the search matched only 2 records.

```
{ 
    "columns": [
        {
          "name": "ip_src_addr",
          "label": "Source IP"
        },
        {
          "name": "ip_dst_addr",
          "label": "Destination IP"
        },
        {
          "name": "country",
          "label": "Country Code"
        }
    ],
    "results": [
        {
          "ip_src_addr": "192.168.1.1",
          "ip_dst_addr": "192.168.1.2",
          "country": "US"
        },
        {
          "ip_src_addr": "192.168.1.2",
          "ip_dst_addr": "192.168.1.1",
          "country": "GB"        
        }
    ]
}
```