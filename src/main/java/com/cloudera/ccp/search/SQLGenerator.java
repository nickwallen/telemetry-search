package com.cloudera.ccp.search;

public interface SQLGenerator {

    String whereClause(SearchRequest request);

    String orderClause(SearchRequest request);
}
