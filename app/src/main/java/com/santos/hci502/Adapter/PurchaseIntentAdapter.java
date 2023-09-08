package com.santos.hci502.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.santos.hci502.Model.CartModel;
import com.santos.hci502.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PurchaseIntentAdapter extends RecyclerView.Adapter<PurchaseIntentAdapter.PurchaseIntentHolder> {
    Context context;
    ArrayList<CartModel> inboxList;

    public PurchaseIntentAdapter(Context context) {
        this.context = context;
        this.inboxList = new ArrayList<>();
    }

    public void addAll(List<CartModel> newInbox) {
        int initSize = inboxList.size();
        inboxList.addAll(newInbox);
        notifyItemRangeChanged(initSize, newInbox.size());
    }

    @NonNull
    @Override
    public PurchaseIntentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_purchase_intent, parent, false);
        PurchaseIntentHolder purchaseIntentHolder = new PurchaseIntentHolder(itemView, context, inboxList);
        return purchaseIntentHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PurchaseIntentHolder holder, int position) {
        int pricePerItem = Integer.parseInt(inboxList.get(position).getProductPrice());
        int itemQuantity = Integer.parseInt(inboxList.get(position).getItemQuantity());
        int itemPrice = pricePerItem/itemQuantity;
        holder.tvProdName.setText(inboxList.get(position).getProductName());
        holder.tvPrice.setText(String.valueOf(itemPrice));
        holder.tvQnty.setText(inboxList.get(position).getItemQuantity());
    }


    @Override
    public int getItemCount() {
        return inboxList.size();
    }

    public class PurchaseIntentHolder extends RecyclerView.ViewHolder {
        ArrayList<CartModel> inboxList = new ArrayList<>();
        Context ctx;
        TextView tvProdName, tvPrice, tvQnty;

        public PurchaseIntentHolder(@NonNull View itemView, Context ctx, ArrayList<CartModel> inboxList) {
            super(itemView);
            this.inboxList = inboxList;
            this.ctx = ctx;

            tvProdName = itemView.findViewById(R.id.tvProdName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQnty = itemView.findViewById(R.id.tvQnty);
        }
    }


}
