package com.example.kikapu;

import java.io.Serializable;

public class CartItem implements Serializable {

    private String productId;
    private String productName;
    private double productPrice;
    private double discount;
    private String imageUrl;
    private int quantity;

    public CartItem() {
        // Required empty constructor for Firestore or data persistence
    }

    public CartItem(String productId, String productName, double productPrice, double discount, String imageUrl, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.discount = discount;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
