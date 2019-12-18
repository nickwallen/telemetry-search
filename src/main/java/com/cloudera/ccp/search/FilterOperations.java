package com.cloudera.ccp.search;

public interface FilterOperations {

    default FilterOperation map(Filter filter) {
        if("=".equals(filter.getOp())) {
            return new Equality();
        } else {
            throw new IllegalArgumentException("Unexpected operator: " + filter.getOp());
        }
    }

}
