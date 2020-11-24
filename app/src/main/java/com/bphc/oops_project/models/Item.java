package com.bphc.oops_project.models;

import android.content.Context;

import com.bphc.oops_project.prefs.SharedPrefs;
import com.google.gson.annotations.SerializedName;

public class Item {

    public String image;

    @SerializedName("name")
    public String name;

    @SerializedName("itemId")
    public int itemId;

    @SerializedName("quantity")
    public String quantity;

    @SerializedName("roQuantity")
    public int roQuantity;

    @SerializedName("category")
    public String category;

    @SerializedName("unit")
    public String unit;

    public Item (String image, String name, String quantity, int roQuantity, String category, String unit) {
        this.image = image;
        this.name = name;
        this.quantity = quantity;
        this.roQuantity = roQuantity;
        this.category = category;
        this.unit = unit;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getRoQuantity() {
        return roQuantity;
    }

    public void setRoQuantity(int roQuantity) {
        this.roQuantity = roQuantity;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getPositionParent(Context context, String category) {
        return SharedPrefs.getIntParams(context, category);
    }

}
