package com.example.kikapu;

import android.os.Bundle;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddProductActivity extends AppCompatActivity {

    private TextInputEditText productNameEditText, productPriceEditText, discountEditText,
            minStockEditText, maxStockEditText, currentStockEditText, imageUrlEditText;
    private Spinner categorySpinner;
    private MaterialButton addProductButton;
    private FirebaseFirestore db;
    private List<String> categoryList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        productNameEditText = findViewById(R.id.productNameEditText);
        productPriceEditText = findViewById(R.id.productPriceEditText);
        discountEditText = findViewById(R.id.discountEditText);
        minStockEditText = findViewById(R.id.minStockEditText);
        maxStockEditText = findViewById(R.id.maxStockEditText);
        currentStockEditText = findViewById(R.id.currentStockEditText);
        categorySpinner = findViewById(R.id.categorySpinner);
        imageUrlEditText = findViewById(R.id.imageUrlEditText);
        addProductButton = findViewById(R.id.addProductButton);

        db = FirebaseFirestore.getInstance();
        categoryList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        fetchCategories();

        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProductToFirestore();
            }
        });
    }

    private void fetchCategories() {
        db.collection("categories")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        categoryList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String categoryName = document.getString("categoryName");
                            if (categoryName != null) {
                                categoryList.add(categoryName);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Failed to fetch categories: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addProductToFirestore() {
        String productName = productNameEditText.getText().toString().trim();
        String productPriceStr = productPriceEditText.getText().toString().trim();
        String discountStr = discountEditText.getText().toString().trim();
        String minStockStr = minStockEditText.getText().toString().trim();
        String maxStockStr = maxStockEditText.getText().toString().trim();
        String currentStockStr = currentStockEditText.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();
        String imageUrl = imageUrlEditText.getText().toString().trim();

        if (TextUtils.isEmpty(productName) || TextUtils.isEmpty(productPriceStr) ||
                TextUtils.isEmpty(discountStr) || TextUtils.isEmpty(minStockStr) ||
                TextUtils.isEmpty(maxStockStr) || TextUtils.isEmpty(currentStockStr) ||
                TextUtils.isEmpty(category) || TextUtils.isEmpty(imageUrl)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double productPrice = Double.parseDouble(productPriceStr);
            double discount = Double.parseDouble(discountStr);
            int minStock = Integer.parseInt(minStockStr);
            int maxStock = Integer.parseInt(maxStockStr);
            int currentStock = Integer.parseInt(currentStockStr);

            Map<String, Object> product = new HashMap<>();
            product.put("productName", productName);
            product.put("productPrice", productPrice);
            product.put("discount", discount);
            product.put("minStock", minStock);
            product.put("maxStock", maxStock);
            product.put("currentStock", currentStock);
            product.put("category", category);
            product.put("imageUrl", imageUrl);

            db.collection("products")
                    .add(product)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Product added successfully", Toast.LENGTH_SHORT).show();
                        clearFields();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error adding product: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearFields() {
        productNameEditText.setText("");
        productPriceEditText.setText("");
        discountEditText.setText("");
        minStockEditText.setText("");
        maxStockEditText.setText("");
        currentStockEditText.setText("");
        imageUrlEditText.setText("");
        categorySpinner.setSelection(0);
    }
}