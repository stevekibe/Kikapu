package com.example.kikapu;

import com.google.firebase.Timestamp;

public class Order {
    private double totalPrice;
    private Timestamp timestamp;

    public Order() {} // Required empty constructor for Firestore

    public Order(double totalPrice, Timestamp timestamp) {
        this.totalPrice = totalPrice;
        this.timestamp = timestamp;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}