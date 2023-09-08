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
import com.santos.hci502.Model.ProductsModel;
import com.santos.hci502.R;
import com.santos.hci502.View.admin.activity.EditProductIntent;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import static com.santos.hci502.Util.Constants.PRODUCT_DESC;
import static com.santos.hci502.Util.Constants.PRODUCT_NAME;
import static com.santos.hci502.Util.Constants.PRODUCT_PRICE;
import static com.santos.hci502.Util.Constants.PRODUCT_STOCK;
import static com.santos.hci502.Util.Constants.PRODUCT_URL;

public class ProductsAdminAdapter extends RecyclerView.Adapter<ProductsAdminAdapter.ProductsViewHolder> {
    Context context;
    ArrayList<ProductsModel> inboxList;

    public ProductsAdminAdapter(Context context) {
        this.inboxList = new ArrayList<>();
        this.context = context;
    }

    public void addAll(List<ProductsModel> newInbox) {
        int initSize = inboxList.size();
        inboxList.addAll(newInbox);
        notifyItemRangeChanged(initSize, newInbox.size());
    }

    public void removeAllItems() {
        inboxList.clear();
    }

    @NonNull
    @Override
    public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_products, parent, false);
        ProductsViewHolder productsViewHolder = new ProductsViewHolder(itemView, context, inboxList);
        return productsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsViewHolder holder, int position) {
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.ic_launcher_background);
        Glide.with(context)
                .load(inboxList.get(position).getProductUrl())
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
        holder.tvProdName.setText(inboxList.get(position).getProductName());
        holder.tvProdPrice.setText(inboxList.get(position).getProductPrice());
        if (inboxList.get(position).getProductStock().equals("0")) {
            holder.tvStock.setText("OUT OF STOCK");
            holder.tvStock.setTextColor(Color.parseColor("#e50000"));
            holder.tvStock.setTypeface(Typeface.DEFAULT_BOLD);
        } else {
            holder.tvStock.setText("Stock Left: " + inboxList.get(position).getProductStock());
            holder.tvStock.setTypeface(Typeface.DEFAULT);
            holder.tvStock.setTextColor(Color.parseColor("#808080"));
        }
        holder.cvProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(context , inboxList.get(position).getProductName(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(context, EditProductIntent.class);
                intent.putExtra(PRODUCT_NAME, inboxList.get(position).getProductName());
                intent.putExtra(PRODUCT_DESC, inboxList.get(position).getProductDesc());
                intent.putExtra(PRODUCT_PRICE, inboxList.get(position).getProductPrice());
                intent.putExtra(PRODUCT_STOCK, inboxList.get(position).getProductStock());
                intent.putExtra(PRODUCT_URL, inboxList.get(position).getProductUrl());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return inboxList.size();
    }

    public class ProductsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvProdName, tvProdPrice, tvStock;
        ImageView ivProdPic;
        CardView cvProduct;
        ProgressBar progressBar;
        ArrayList<ProductsModel> inboxList = new ArrayList<>();
        Context ctx;

        public ProductsViewHolder(View itemView, Context ctx, ArrayList<ProductsModel> inboxList) {
            super(itemView);
            this.inboxList = inboxList;
            this.ctx = ctx;
            itemView.setOnClickListener(this);
            ivProdPic = itemView.findViewById(R.id.ivProductPic);
            tvProdName = itemView.findViewById(R.id.tvProdName);
            tvProdPrice = itemView.findViewById(R.id.tvQnty);
            tvStock = itemView.findViewById(R.id.tvStock);
            cvProduct = itemView.findViewById(R.id.cvProduct);
            progressBar = itemView.findViewById(R.id.progressBar);

        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            ProductsModel inboxModel = this.inboxList.get(position);
            //Intent intent = new Intent(this.ctx, TopUpIntentActivity.class);


        }
    }
}
