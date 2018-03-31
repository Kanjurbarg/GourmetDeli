package com.rohankhadapkar.gourmetdeli.Class;

public class CartList {

    private String Name;
    private String Price;
    private String Quantity;
    private String Image;
    private String ItemId;
    private String CategoryId;

    public CartList() {
    }

    public CartList(String name, String price, String quantity, String image, String itemId, String categoryId) {
        Name = name;
        Price = price;
        Quantity = quantity;
        Image = image;
        ItemId = itemId;
        CategoryId = categoryId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getItemId() {
        return ItemId;
    }

    public void setItemId(String itemId) {
        ItemId = itemId;
    }

    public String getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(String categoryId) {
        CategoryId = categoryId;
    }
}
