package com.bphc.oops_project.models;

public class Item {

    public int image;
    public String name;
    public String quantity;

    public Item(int image, String name, String quantity) {
        this.image = image;
        this.name = name;
        this.quantity = quantity;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
