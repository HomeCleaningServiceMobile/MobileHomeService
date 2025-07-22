package com.example.prm_project.data.model;

public class PaginationInfo {
    private int pageNumber;
    private int pageSize;
    private int totalCount;
    private int totalPages;

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public boolean hasPreviousPage() {
        return pageNumber > 1;
    }

    public boolean hasNextPage() {
        return pageNumber < totalPages;
    }
}

