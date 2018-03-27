package com.rohankhadapkar.gourmetdeli;

public class ItemsList {

    private String Name;
    private String Image;
    private String Cost;
    private String CategoryId;

    public ItemsList()
    {

    }

    public ItemsList(String name, String image, String cost, String categoryId) {
        Name = name;
        Image = image;
        Cost = cost;
        CategoryId = categoryId;
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
}
