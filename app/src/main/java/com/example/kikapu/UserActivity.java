package com.example.kikapu;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserActivity extends AppCompatActivity implements ProductAdapter.OnAddToCartClickListener {

    private RecyclerView productRecyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private FirebaseFirestore db;
    private SearchView productSearchView;
    private List<CartItem> cartItems;
    private ImageButton cartButton;
    private BottomNavigationView bottomNavigationView;
    private FirestoreRecyclerAdapter<Order, OrderViewHolder> orderAdapter;

    private boolean isProductAdapterActive = false;
    private boolean isOrderAdapterActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        productRecyclerView = findViewById(R.id.productRecyclerView);
        productSearchView = findViewById(R.id.productSearchView);
        cartButton = findViewById(R.id.cartButton);
        cartItems = new ArrayList<>();
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(this, productList, this);
        productRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        productRecyclerView.setAdapter(productAdapter);

        db = FirebaseFirestore.getInstance();

        fetchProducts();
        setupSearch();

        cartButton.setOnClickListener(v -> openCartActivity());

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_products) {
                setupProductRecyclerView(); // Method call
                return true;
            } else if (item.getItemId() == R.id.navigation_orders) {
                setupOrderRecyclerView(); // Method call
                return true;
            }
            return false;
        });

        setupProductRecyclerView();
    }

    private void setupProductRecyclerView() {
        if (isOrderAdapterActive && orderAdapter != null) {
            orderAdapter.stopListening();
            isOrderAdapterActive = false;
        }

        productRecyclerView.setAdapter(productAdapter);
        productRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        productAdapter.notifyDataSetChanged();
        isProductAdapterActive = true;
        fetchProducts(); //fetch the products again when view is switched.
    }

    private void fetchProducts() {
        db.collection("products")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        productList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Product product = document.toObject(Product.class);
                            if (product != null) {
                                productList.add(product);
                            }
                        }
                        productAdapter.notifyDataSetChanged(); // Notify adapter of data change
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
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterProducts(newText);
                return true;
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
        CartItem cartItem = new CartItem(
                "unique_id",
                product.getProductName(),
                product.getProductPrice(),
                product.getDiscount(),
                product.getImageUrl(),
                1
        );
        cartItems.add(cartItem);
        Toast.makeText(this, product.getProductName() + " added to cart", Toast.LENGTH_SHORT).show();
    }

    private void openCartActivity() {
        Intent intent = new Intent(this, CartActivity.class);
        intent.putExtra("cartItems", new ArrayList<>(cartItems));
        startActivity(intent);
    }

    private void setupOrderRecyclerView() {
        if (isProductAdapterActive) {
            isProductAdapterActive = false;
        }

        Query orderQuery = db.collection("orders");

        FirestoreRecyclerOptions<Order> orderOptions = new FirestoreRecyclerOptions.Builder<Order>()
                .setQuery(orderQuery, Order.class)
                .build();

        orderAdapter = new FirestoreRecyclerAdapter<Order, OrderViewHolder>(orderOptions) {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder holder, int position, @NonNull Order model) {
                holder.orderTotal.setText("Total: Ksh " + model.getTotalPrice());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                String formattedDate = sdf.format(model.getTimestamp().toDate());
                holder.orderDate.setText("Date: " + formattedDate);
            }

            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
                return new OrderViewHolder(view);
            }
        };

        productRecyclerView.setAdapter(orderAdapter);
        productRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (!isOrderAdapterActive) {
            orderAdapter.startListening();
        }
        isOrderAdapterActive = true;
    }
    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderTotal, orderDate;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderTotal = itemView.findViewById(R.id.orderTotal);
            orderDate = itemView.findViewById(R.id.orderDate);
        }
    }
}