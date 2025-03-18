package com.example.kikapu;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ManageProductActivity extends AppCompatActivity {

    private EditText productNameEditText, productPriceEditText, discountEditText, currentStockEditText;
    private Button updateProductButton;
    private FirebaseFirestore fStore;
    private String productId; // Pass this from the previous activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_product);

        productNameEditText = findViewById(R.id.productNameEditText);
        productPriceEditText = findViewById(R.id.productPriceEditText);
        discountEditText = findViewById(R.id.discountEditText);
        currentStockEditText = findViewById(R.id.currentStockEditText);
        updateProductButton = findViewById(R.id.updateProductButton);

        fStore = FirebaseFirestore.getInstance();

        // Get product ID from the intent
        productId = getIntent().getStringExtra("productId");
        if (productId == null) {
            Toast.makeText(this, "Product ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load product details
        loadProductDetails(productId);

        updateProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProductDetails(productId);
            }
        });
    }

    private void loadProductDetails(String productId) {
        DocumentReference docRef = fStore.collection("Products").document(productId);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                productNameEditText.setText(documentSnapshot.getString("productName"));
                productPriceEditText.setText(String.valueOf(documentSnapshot.getDouble("productPrice")));
                discountEditText.setText(String.valueOf(documentSnapshot.getLong("discount")));
                currentStockEditText.setText(String.valueOf(documentSnapshot.getLong("currentStock")));
            } else {
                Toast.makeText(ManageProductActivity.this, "Product not found", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(e -> {
            Log.e("ManageProduct", "Error loading product details: " + e.getMessage());
            Toast.makeText(ManageProductActivity.this, "Error loading product details", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void updateProductDetails(String productId) {
        String productName = productNameEditText.getText().toString().trim();
        double productPrice = Double.parseDouble(productPriceEditText.getText().toString().trim());
        long discount = Long.parseLong(discountEditText.getText().toString().trim());
        long currentStock = Long.parseLong(currentStockEditText.getText().toString().trim());

        Map<String, Object> product = new HashMap<>();
        product.put("productName", productName);
        product.put("productPrice", productPrice);
        product.put("discount", discount);
        product.put("currentStock", currentStock);

        DocumentReference docRef = fStore.collection("Products").document(productId);
        docRef.update(product).addOnSuccessListener(aVoid -> {
            Toast.makeText(ManageProductActivity.this, "Product updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e -> {
            Log.e("ManageProduct", "Error updating product: " + e.getMessage());
            Toast.makeText(ManageProductActivity.this, "Error updating product", Toast.LENGTH_SHORT).show();
        });
    }
}