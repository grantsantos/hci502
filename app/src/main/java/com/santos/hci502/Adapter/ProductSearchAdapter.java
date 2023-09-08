package com.santos.hci502.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.santos.hci502.R;
import com.santos.hci502.View.admin.activity.EditProductIntent;
import com.santos.hci502.View.customer.ViewProductIntent;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import static com.santos.hci502.Util.Constants.ADMIN_EMAIL;
import static com.santos.hci502.Util.Constants.PRODUCT_DESC;
import static com.santos.hci502.Util.Constants.PRODUCT_NAME;
import static com.santos.hci502.Util.Constants.PRODUCT_PRICE;
import static com.santos.hci502.Util.Constants.PRODUCT_STOCK;
import static com.santos.hci502.Util.Constants.PRODUCT_URL;

public class ProductSearchAdapter extends RecyclerView.Adapter<ProductSearchAdapter.SearchViewHolder> {
    Context context;
    ArrayList<String> productNameList;
    ArrayList<String> priceList;
    ArrayList<String> productUrlList;
    ArrayList<String> productDescList;
    ArrayList<String> productStockList;


    public ProductSearchAdapter(Context context, ArrayList<String> productNameList,
                                ArrayList<String> priceList, ArrayList<String> productUrlList,
                                ArrayList<String> productDescList, ArrayList<String> productStockList) {
        this.context = context;
        this.productNameList = productNameList;
        this.priceList = priceList;
        this.productUrlList = productUrlList;
        this.productDescList = productDescList;
        this.productStockList = productStockList;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_products, parent, false);
        return new ProductSearchAdapter.SearchViewHolder(itemView, context);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.ic_launcher_background);
        Glide.with(context)
                .load(productUrlList.get(position))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .apply(requestOptions)
                .into(holder.ivProdPic);
        holder.productName.setText(productNameList.get(position));
        holder.productPrice.setText(priceList.get(position));
        if (productStockList.get(position).equals("0")) {
            holder.tvStock.setText("OUT OF STOCK");
            holder.tvStock.setTextColor(Color.parseColor("#e50000"));
            holder.tvStock.setTypeface(Typeface.DEFAULT_BOLD);
        } else {
            holder.tvStock.setText("Stock Left: " + productStockList.get(position));
            holder.tvStock.setTypeface(Typeface.DEFAULT);
            holder.tvStock.setTextColor(Color.parseColor("#808080"));
        }
        holder.cvProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.firebaseAuth.getCurrentUser().getEmail().equals(ADMIN_EMAIL)) {
                    Intent intent = new Intent(context, EditProductIntent.class);
                    intent.putExtra(PRODUCT_NAME, productNameList.get(position));
                    intent.putExtra(PRODUCT_DESC, productDescList.get(position));
                    intent.putExtra(PRODUCT_PRICE, priceList.get(position));
                    intent.putExtra(PRODUCT_STOCK, productStockList.get(position));
                    intent.putExtra(PRODUCT_URL, productUrlList.get(position));
                    context.startActivity(intent);
                }else{
                    Intent intent = new Intent(context, ViewProductIntent.class);
                    intent.putExtra(PRODUCT_NAME, productNameList.get(position));
                    intent.putExtra(PRODUCT_DESC, productDescList.get(position));
                    intent.putExtra(PRODUCT_PRICE, priceList.get(position));
                    intent.putExtra(PRODUCT_STOCK, productStockList.get(position));
                    intent.putExtra(PRODUCT_URL, productUrlList.get(position));
                    context.startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return productNameList.size();
    }

    class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Context ctx;
        TextView productName, productPrice, tvStock;
        ImageView ivProdPic;
        CardView cvProduct;
        ProgressBar progressBar;
        FirebaseAuth firebaseAuth;


        public SearchViewHolder(View itemView, Context ctx) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.ctx = ctx;
            ivProdPic = itemView.findViewById(R.id.ivProductPic);
            productName = itemView.findViewById(R.id.tvProdName);
            productPrice = itemView.findViewById(R.id.tvQnty);
            tvStock = itemView.findViewById(R.id.tvStock);
            cvProduct = itemView.findViewById(R.id.cvProduct);
            progressBar = itemView.findViewById(R.id.progressBar);
            firebaseAuth = FirebaseAuth.getInstance();
        }

        @Override
        public void onClick(View v) {

        }
    }


}
