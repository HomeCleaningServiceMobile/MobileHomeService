package com.example.prm_project.data.model;

import java.util.List;

public class AdminStaffListResponse {

    public boolean isSucceeded;
    public Data data;

    public static class Data {
        public List<StaffManagement> data;
        public int totalCount;
        public int pageNumber;
        public int pageSize;
        public int totalPages;
        public boolean hasNextPage;
        public boolean hasPreviousPage;
    }
}
