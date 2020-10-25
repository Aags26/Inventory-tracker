package com.bphc.oops_project.models;

import java.util.ArrayList;
import java.util.List;

public class ItemGroup {

    public String group;
    public ArrayList<Item> items;

    public ItemGroup(String group, ArrayList<Item> items) {
        this.group = group;
        this.items = items;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }
}
