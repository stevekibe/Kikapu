package com.example.kikapu;

public class Product {
    private String productName;
    private double productPrice;
    private double discount;
    private String imageUrl;
    private long currentStock;
    private long minStock;

    // Required empty constructor for Firestore
    public Product() {}

    public Product(String productName, double productPrice, double discount, String imageUrl, long currentStock, long minStock) {
        this.productName = productName;
        this.productPrice = productPrice;
        this.discount = discount;
        this.imageUrl = imageUrl;
        this.currentStock = currentStock;
        this.minStock = minStock;
    }

    public String getProductName() {
        return productName;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public double getDiscount() {
        return discount;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public long getCurrentStock() {
        return currentStock;
    }

    public long getMinStock() {
        return minStock;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setCurrentStock(long currentStock) {
        this.currentStock = currentStock;
    }

    public void setMinStock(long minStock) {
        this.minStock = minStock;
    }
}