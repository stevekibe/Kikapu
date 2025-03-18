package com.example.kikapu;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class DisplayProductActivity extends AppCompatActivity {

    private RecyclerView productRecyclerView;
    private FirebaseFirestore fStore;
    private FirestoreRecyclerAdapter<Product, ProductViewHolder> adapter;
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_product);

        productRecyclerView = findViewById(R.id.productRecyclerView);
        fStore = FirebaseFirestore.getInstance();
        emptyView = findViewById(R.id.emptyView);

        // Query to get products
        Query query = fStore.collection("Products");

        FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>()
                .setQuery(query, Product.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Product, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Product model) {
                holder.productName.setText(model.getProductName());
                holder.productPrice.setText(String.valueOf(model.getProductPrice()));
                holder.productStockTextView.setText("Stock: " + model.getCurrentStock());

                // Check for low stock
                if (model.getCurrentStock() <= model.getMinStock()) {
                    // Simulate notification for now
                    Toast.makeText(DisplayProductActivity.this, model.getProductName() + " is low on stock!", Toast.LENGTH_SHORT).show();
                    // Later, replace this with actual notification logic
                }

                holder.deleteButton.setOnClickListener(v -> {
                    deleteProduct(getSnapshots().getSnapshot(position).getId());
                });

                holder.editButton.setOnClickListener(v -> {
                    Intent intent = new Intent(DisplayProductActivity.this, ManageProductActivity.class);
                    intent.putExtra("productId", getSnapshots().getSnapshot(position).getId());
                    startActivity(intent);
                });
            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
                return new ProductViewHolder(view);
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if (getItemCount() == 0) {
                    emptyView.setVisibility(View.VISIBLE);
                    productRecyclerView.setVisibility(View.GONE);
                } else {
                    emptyView.setVisibility(View.GONE);
                    productRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        };

        productRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        productRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void deleteProduct(String productId) {
        fStore.collection("Products").document(productId).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(DisplayProductActivity.this, "Product deleted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("DisplayProduct", "Error deleting product: " + e.getMessage());
                    Toast.makeText(DisplayProductActivity.this, "Error deleting product", Toast.LENGTH_SHORT).show();
                });
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, productStockTextView;
        Button deleteButton, editButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productNameTextView);
            productPrice = itemView.findViewById(R.id.productPriceTextView);
            productStockTextView = itemView.findViewById(R.id.productStockTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            editButton = itemView.findViewById(R.id.editButton);
        }
    }

    // Product Model Class
    public static class Product {
        private String productName;
        private double productPrice;
        private long currentStock;
        private long minStock;

        public Product() {}

        public Product(String productName, double productPrice, long currentStock, long minStock) {
            this.productName = productName;
            this.productPrice = productPrice;
            this.currentStock = currentStock;
            this.minStock = minStock;
        }

        public String getProductName() {
            return productName;
        }

        public double getProductPrice() {
            return productPrice;
        }

        public long getCurrentStock() {
            return currentStock;
        }

        public long getMinStock() {
            return minStock;
        }
    }
}