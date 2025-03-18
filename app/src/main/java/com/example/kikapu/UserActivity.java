package com.example.kikapu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserActivity extends AppCompatActivity implements ProductAdapter.OnAddToCartClickListener {

    private RecyclerView productRecyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private FirebaseFirestore db;
    private SearchView productSearchView;
    private List<CartItem> cartItems; // Add cartItems list
    private ImageButton cartButton; // Add cart button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user); // Make sure this matches your layout file name

        productRecyclerView = findViewById(R.id.productRecyclerView);
        productSearchView = findViewById(R.id.productSearchView);
        cartButton = findViewById(R.id.cartButton); // Initialize cartButton
        cartItems = new ArrayList<>(); // Initialize cartItems

        // Set up RecyclerView
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(this, productList, this); // Pass 'this' as listener
        productRecyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 columns grid
        productRecyclerView.setAdapter(productAdapter);

        db = FirebaseFirestore.getInstance();

        // Fetch products from Firestore
        fetchProducts();

        // Set up search functionality
        setupSearch();

        // Set up cart button click listener
        cartButton.setOnClickListener(v -> {
            // Open cart activity
            openCartActivity();
        });
    }

    private void fetchProducts() {
        db.collection("products")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        productList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Product product = document.toObject(Product.class);
                            if (product != null) { // Check for null product object
                                productList.add(product);
                            }
                        }
                        productAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Failed to fetch products: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupSearch() {
        productSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterProducts(query);
                return false; // Return false to not close the keyboard
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterProducts(newText);
                return true; // Return true to indicate that you handled the change
            }
        });
    }

    private void filterProducts(String query) {
        List<Product> filteredList = new ArrayList<>();
        for (Product product : productList) {
            if (product.getProductName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(product);
            }
        }
        productAdapter.setFilteredList(filteredList);
    }

    @Override
    public void onAddToCartClick(Product product) {
        // Handle add to cart logic here
        CartItem cartItem = new CartItem(
                "unique_id", // Replace with actual product ID
                product.getProductName(),
                product.getProductPrice(),
                product.getDiscount(),
                product.getImageUrl(),
                1 // Initial quantity
        );

        cartItems.add(cartItem);
        Toast.makeText(this, product.getProductName() + " added to cart", Toast.LENGTH_SHORT).show();
    }

    private void openCartActivity() {
        Intent intent = new Intent(this, CartActivity.class);
        intent.putExtra("cartItems", new ArrayList<>(cartItems));
        startActivity(intent);
    }
}