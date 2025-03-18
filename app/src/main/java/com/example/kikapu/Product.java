package com.example.kikapu;

public class Product {
    private String productName;
    private double productPrice;
    private double discount; // Add this line
    private String imageUrl;

    // Required empty constructor for Firestore
    public Product() {}

    public Product(String productName, double productPrice, double discount, String imageUrl) {
        this.productName = productName;
        this.productPrice = productPrice;
        this.discount = discount;
        this.imageUrl = imageUrl;
    }

    public String getProductName() {
        return productName;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public double getDiscount() { return discount; } // Add this getter

    public String getImageUrl() {
        return imageUrl;
    }
}