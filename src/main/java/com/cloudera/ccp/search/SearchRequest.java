package com.cloudera.ccp.search;

import java.util.List;

public class SearchRequest {
    private List<Filter> filters;
    private List<Sorter> sortBy;

    public List<Filter> getFilters() {
        return filters;
    }

    public void setFilters(List<Filter> filters) {
        this.filters = filters;
    }

    public List<Sorter> getSortBy() {
        return sortBy;
    }

    public void setSortBy(List<Sorter> sortBy) {
        this.sortBy = sortBy;
    }
}
