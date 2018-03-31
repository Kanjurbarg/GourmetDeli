package com.rohankhadapkar.gourmetdeli.Class;

/**
 * Created by Rohan Khadapkar on 25-Mar-18.
 */

public class CategoryList {

    private String Name;
    private String Image;

    public CategoryList() {

    }

    public CategoryList(String name, String image) {
        Name = name;
        Image = image;
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
}
