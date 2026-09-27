package com.rbh.resto;

import android.graphics.Bitmap;

public class Dish {
    private int id;
    private String name;
    private String description;
    private double price;
    private int categoryId;
    private String categoryName;
    private byte[] image;

    public Dish() {}

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public byte[] getImage() { return image; }
    public void setImage(byte[] image) { this.image = image; }
}