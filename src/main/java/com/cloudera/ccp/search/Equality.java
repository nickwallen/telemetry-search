package com.cloudera.ccp.search;

public class Equality implements FilterOperation {

    public String apply(Filter filter) {
        if(filter.getValue() == null || filter.getValue().size() != 1) {
            throw new IllegalArgumentException("Expected single value for equality operator");
        }
        return String.format("%s = %s", filter.getField(), filter.getValue().get(0));
    }

    public String apply(Sorter sorter) {
        return null;
    }
}
