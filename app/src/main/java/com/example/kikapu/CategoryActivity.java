package com.example.kikapu;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CategoryActivity extends AppCompatActivity {

    private TextInputEditText categoryNameEditText;
    private MaterialButton addCategoryButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        categoryNameEditText = findViewById(R.id.categoryNameEditText);
        addCategoryButton = findViewById(R.id.addCategoryButton);

        db = FirebaseFirestore.getInstance();

        addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCategoryToFirestore();
            }
        });
    }

    private void addCategoryToFirestore() {
        String categoryName = categoryNameEditText.getText().toString().trim();

        if (TextUtils.isEmpty(categoryName)) {
            Toast.makeText(this, "Please enter a category name", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> category = new HashMap<>();
        category.put("categoryName", categoryName);

        db.collection("categories")
                .add(category)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Category added successfully", Toast.LENGTH_SHORT).show();
                    categoryNameEditText.setText(""); // Clear the field
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error adding category: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}