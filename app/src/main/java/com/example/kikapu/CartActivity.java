package com.example.kikapu;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;
    private TextView totalPriceTextView;
    private Button makeOrderButton;
    private FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        totalPriceTextView = findViewById(R.id.totalPriceTextView);
        makeOrderButton = findViewById(R.id.makeOrderButton);
        fStore = FirebaseFirestore.getInstance();

        // Get cart items from intent
        cartItems = (ArrayList<CartItem>) getIntent().getSerializableExtra("cartItems");

        // Set up RecyclerView
        cartAdapter = new CartAdapter(this, cartItems);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartRecyclerView.setAdapter(cartAdapter);

        // Calculate and display total price
        calculateTotalPrice();

        makeOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeOrder();
            }
        });
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

    private void makeOrder() {
        if (cartItems == null || cartItems.isEmpty()) {
            Toast.makeText(this, "Cart is empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> order = new HashMap<>();
        order.put("items", cartItems);
        order.put("totalPrice", calculateTotal()); // Use a separate function for clarity.
        order.put("timestamp", com.google.firebase.Timestamp.now()); // Add timestamp

        fStore.collection("orders")
                .add(order)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(CartActivity.this, "Order placed successfully!", Toast.LENGTH_SHORT).show();
                        cartItems.clear(); // Clear the cart
                        cartAdapter.notifyDataSetChanged();
                        calculateTotalPrice(); //Update total price
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("CartActivity", "Error placing order: " + e.getMessage());
                        Toast.makeText(CartActivity.this, "Failed to place order.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private double calculateTotal() {
        double totalPrice = 0;
        for (CartItem item : cartItems) {
            double price = item.getProductPrice();
            if (item.getDiscount() > 0) {
                price = price * (1 - (item.getDiscount() / 100.0));
            }
            totalPrice += price * item.getQuantity();
        }
        return totalPrice;
    }
}