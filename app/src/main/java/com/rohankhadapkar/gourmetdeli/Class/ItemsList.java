package com.rohankhadapkar.gourmetdeli.Class;

public class ItemsList {

    private String Name;
    private String Image;
    private String Cost;
    private String CategoryId;
    private String Type;

    public ItemsList() {

    }

    public ItemsList(String name, String image, String cost, String categoryId, String type) {
        Name = name;
        Image = image;
        Cost = cost;
        CategoryId = categoryId;
        Type = type;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getCost() {
        return Cost;
    }

    public void setCost(String cost) {
        Cost = cost;
    }

    public String getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(String categoryId) {
        CategoryId = categoryId;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }
}
