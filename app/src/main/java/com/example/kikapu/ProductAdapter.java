package com.example.kikapu;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;
    private OnAddToCartClickListener addToCartClickListener; // Add listener field

    public interface OnAddToCartClickListener { // Add interface
        void onAddToCartClick(Product product);
    }

    public ProductAdapter(Context context, List<Product> productList, OnAddToCartClickListener listener) { // Modify constructor
        this.context = context;
        this.productList = productList;
        this.addToCartClickListener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.productNameTextView.setText(product.getProductName());

        if (product.getDiscount() > 0) {
            double discountedPrice = product.getProductPrice() * (1 - (product.getDiscount() / 100.0));
            holder.productPriceTextView.setText("Ksh " + String.valueOf(discountedPrice));
            holder.originalPriceTextView.setText("Ksh " + String.valueOf(product.getProductPrice()));
            holder.originalPriceTextView.setPaintFlags(holder.originalPriceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG); // Strike through
            holder.originalPriceTextView.setVisibility(View.VISIBLE);
        } else {
            holder.productPriceTextView.setText("Ksh " + String.valueOf(product.getProductPrice()));
            holder.originalPriceTextView.setVisibility(View.GONE);
        }

        Glide.with(context).load(product.getImageUrl()).into(holder.productImageView);

        holder.addToCartButton.setOnClickListener(v -> { // Set click listener
            if (addToCartClickListener != null) {
                addToCartClickListener.onAddToCartClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void setFilteredList(List<Product> filteredList) {
        this.productList = filteredList;
        notifyDataSetChanged();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImageView;
        TextView productNameTextView, productPriceTextView, originalPriceTextView;
        Button addToCartButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.productImageView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            productPriceTextView = itemView.findViewById(R.id.productPriceTextView);
            originalPriceTextView = itemView.findViewById(R.id.originalPriceTextView);
            addToCartButton = itemView.findViewById(R.id.addToCartButton);
        }
    }
}