package com.example.kikapu;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;
    private TextView totalPriceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        totalPriceTextView = findViewById(R.id.totalPriceTextView);

        // Get cart items from intent
        cartItems = (ArrayList<CartItem>) getIntent().getSerializableExtra("cartItems");

        // Set up RecyclerView
        cartAdapter = new CartAdapter(this, cartItems);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartRecyclerView.setAdapter(cartAdapter);

        // Calculate and display total price
        calculateTotalPrice();
    }

    private void calculateTotalPrice() {
        double totalPrice = 0;
        for (CartItem item : cartItems) {
            double price = item.getProductPrice();
            if (item.getDiscount() > 0) {
                price = price * (1 - (item.getDiscount() / 100.0));
            }
            totalPrice += price * item.getQuantity();
        }
        totalPriceTextView.setText("Total: Ksh " + String.valueOf(totalPrice));
    }
}