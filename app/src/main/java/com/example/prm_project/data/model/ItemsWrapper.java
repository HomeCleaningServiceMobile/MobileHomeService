package com.example.prm_project.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ItemsWrapper<T> {
    @SerializedName("items")
    private List<T> items;

    public List<T> getItems() { return items; }
    public void setItems(List<T> items) { this.items = items; }
} 