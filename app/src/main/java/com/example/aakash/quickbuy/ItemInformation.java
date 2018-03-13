package com.example.aakash.quickbuy;

/**
 * Created by Aakash on 12/12/2017.
 */

public class ItemInformation {
    private String name;
    private String price;
    private String description;
    private String contact;
    private String category;
    private String imagePath;

    public  ItemInformation() {


    }

    public ItemInformation(String name, String price, String description, String contact, String category, String imagePath) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.contact = contact;
        this.category = category;
        this.imagePath = imagePath;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getContact() {
        return contact;
    }

    public String getCategory() {
        return category;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}


