package com.example.prm_project.data.model;

import java.util.Date;
import java.util.Map;

public class AppResponse<T> {
    private boolean isSucceeded;
    private Date timestamp;
    private Map<String, String[]> messages;
    private T data;
    private PaginationInfo pagination;

    public boolean isSucceeded() {
        return isSucceeded;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public Map<String, String[]> getMessages() {
        return messages;
    }

    public T getData() {
        return data;
    }

    public PaginationInfo getPagination() {
        return pagination;
    }

    public boolean hasMessage(String key) {
        return messages != null && messages.containsKey(key);
    }

    public String getFirstMessage(String key) {
        if (messages != null && messages.containsKey(key)) {
            String[] arr = messages.get(key);
            return (arr != null && arr.length > 0) ? arr[0] : "";
        }
        return "";
    }
}

