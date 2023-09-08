package com.santos.hci502.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.santos.hci502.Model.CartModel;
import com.santos.hci502.R;
import com.santos.hci502.View.customer.PurchaseHistory;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import static com.santos.hci502.Util.Constants.CART;
import static com.santos.hci502.Util.Constants.ITEM_QUANTITY;
import static com.santos.hci502.Util.Constants.PRODUCTS;
import static com.santos.hci502.Util.Constants.PRODUCT_PRICE;
import static com.santos.hci502.Util.Constants.PRODUCT_STOCK;
import static com.santos.hci502.Util.Constants.PRODUCT_URL;
import static com.santos.hci502.Util.Constants.USERS;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    Context context;
    ArrayList<CartModel> inboxList;

    public CartAdapter(Context context) {
        this.context = context;
        this.inboxList = new ArrayList<>();
    }

    public void addAll(List<CartModel> newInbox) {
        int initSize = inboxList.size();
        inboxList.addAll(newInbox);
        notifyItemRangeChanged(initSize, newInbox.size());
    }

    public void removeAllItems() {
        inboxList.clear();
    }

    private void deleteItem(int position) {
        inboxList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, inboxList.size());
    }

    public int grandTotal() {
        int totalPrice = 0;
        for (int i = 0; i < inboxList.size(); i++) {
            totalPrice += Integer.parseInt(inboxList.get(i).getProductPrice());
        }
        return totalPrice;
    }


    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_cart, parent, false);
        CartViewHolder cartViewHolder = new CartViewHolder(itemView, context, inboxList);
        return cartViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
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
        holder.tvQuantity.setText("Quantity: " + inboxList.get(position).getItemQuantity());


        final int[] getQuantity = {Integer.parseInt(inboxList.get(position).getItemQuantity())};
        final int[] getPrice = {Integer.parseInt(inboxList.get(position).getProductPrice())};
        final int[] newPrice = {0};
        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //deleteItem(position);
                holder.showAlertConfirmation("Confirmation",
                        "Are you sure you want to remove " +
                                inboxList.get(position).getProductName() +
                                " from your cart?"
                        , position);

            }
        });
        holder.btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getQuantity[0] > 1) {
                    newPrice[0] = getPrice[0] / getQuantity[0];
                    getPrice[0] = getPrice[0] - newPrice[0];
                    getQuantity[0] -= 1;
                    holder.decreaseQuantity(getQuantity[0], position);
                    holder.tvQuantity.setText("Quantity: " + String.valueOf(getQuantity[0]));
                    holder.tvProdPrice.setText(String.valueOf(getPrice[0]));
                }
            }
        });
        holder.btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (holder.stockChecker(position, getQuantity[0]).equals("true")) {
                    Toast.makeText(context, "You have reached the stock limit for this product", Toast.LENGTH_SHORT).show();
                } else if(holder.stockChecker(position, getQuantity[0]).equals("false")){
                    newPrice[0] = getPrice[0] / getQuantity[0];
                    getPrice[0] = getPrice[0] + newPrice[0];
                    getQuantity[0] += 1;
                    holder.addQuantity(getQuantity[0], position);
                    holder.tvQuantity.setText("Quantity: " + String.valueOf(getQuantity[0]));
                    holder.tvProdPrice.setText(String.valueOf(getPrice[0]));
                }*/
                holder.firebaseDatabase.getReference(USERS)
                        .child(holder.firebaseAuth.getCurrentUser().getUid())
                        .child(CART)
                        .orderByChild(PRODUCT_URL)
                        .equalTo(inboxList.get(position).getProductUrl())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String key = "";
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        key = ds.getKey();
                                    }
                                    holder.firebaseDatabase.getReference(PRODUCTS)
                                            .child(key)
                                            .child(PRODUCT_STOCK)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    int stockLeft = Integer.parseInt(dataSnapshot.getValue().toString());
                                                    if (stockLeft == getQuantity[0]) {
                                                        Toast.makeText(context, "You have reached the stock limit for this product", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        newPrice[0] = getPrice[0] / getQuantity[0];
                                                        getPrice[0] = getPrice[0] + newPrice[0];
                                                        getQuantity[0] += 1;
                                                        holder.addQuantity(getQuantity[0], position);
                                                        holder.tvQuantity.setText("Quantity: " + String.valueOf(getQuantity[0]));
                                                        holder.tvProdPrice.setText(String.valueOf(getPrice[0]));
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return inboxList.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        FirebaseAuth firebaseAuth;
        FirebaseDatabase firebaseDatabase;

        TextView tvProdName, tvProdPrice, tvQuantity;
        ImageView ivProdPic;
        Button btnRemove;
        Button btnMinus, btnPlus;

        ProgressBar progressBar;
        ArrayList<CartModel> inboxList = new ArrayList<>();
        Context ctx;


        public CartViewHolder(View itemView, Context ctx, ArrayList<CartModel> inboxList) {
            super(itemView);
            this.inboxList = inboxList;
            this.ctx = ctx;
            itemView.setOnClickListener(this);
            ivProdPic = itemView.findViewById(R.id.ivProductPic);
            tvProdName = itemView.findViewById(R.id.tvProdName);
            tvProdPrice = itemView.findViewById(R.id.tvQnty);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnRemove = itemView.findViewById(R.id.btnRemoveItem);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            progressBar = itemView.findViewById(R.id.progressBar);
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseDatabase = FirebaseDatabase.getInstance();
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            CartModel inboxModel = this.inboxList.get(position);
            //Intent intent = new Intent(this.ctx, TopUpIntentActivity.class);


        }

        private void showAlertConfirmation(String title, String message, int position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setCancelable(false);
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DatabaseReference dataRef = firebaseDatabase.getReference(USERS);
                    dataRef.child(firebaseAuth.getCurrentUser().getUid())
                            .child(CART)
                            .orderByChild(PRODUCT_URL)
                            .equalTo(inboxList.get(position).getProductUrl())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        String key = "";
                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                            key = ds.getKey();
                                        }
                                        dataRef.child(firebaseAuth.getCurrentUser().getUid())
                                                .child(CART)
                                                .child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                deleteItem(position);
                                                showAlertDone("Success", "Item removed");
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                }
            }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();

        }

        private void showAlertDone(String title, String message) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
        }

        private void addQuantity(int newQuantity, int position) {
            DatabaseReference addRef = firebaseDatabase.getReference(USERS).child(firebaseAuth.getCurrentUser().getUid());
            addRef.child(CART)
                    .orderByChild(PRODUCT_URL)
                    .equalTo(inboxList.get(position).getProductUrl())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String key = "";
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    key = ds.getKey();
                                }
                                addRef.child(CART)
                                        .child(key)
                                        .child(ITEM_QUANTITY)
                                        .setValue(String.valueOf(newQuantity));
                                String finalKey = key;
                                firebaseDatabase.getReference(PRODUCTS)
                                        .child(key)
                                        .child(PRODUCT_PRICE)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                int price = Integer.parseInt(dataSnapshot.getValue().toString());
                                                price *= newQuantity;
                                                addRef.child(CART)
                                                        .child(finalKey)
                                                        .child(PRODUCT_PRICE)
                                                        .setValue(String.valueOf(price));

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }

        private void decreaseQuantity(int newQuantity, int position) {
            DatabaseReference minusRef = firebaseDatabase.getReference(USERS).child(firebaseAuth.getCurrentUser().getUid());
            minusRef.child(CART)
                    .orderByChild(PRODUCT_URL)
                    .equalTo(inboxList.get(position).getProductUrl())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String key = "";
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    key = ds.getKey();
                                }
                                minusRef.child(CART)
                                        .child(key)
                                        .child(ITEM_QUANTITY)
                                        .setValue(String.valueOf(newQuantity));
                                String finalKey = key;
                                firebaseDatabase.getReference(PRODUCTS)
                                        .child(key)
                                        .child(PRODUCT_PRICE)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                int price = Integer.parseInt(dataSnapshot.getValue().toString());
                                                price *= newQuantity;
                                                minusRef.child(CART)
                                                        .child(finalKey)
                                                        .child(PRODUCT_PRICE)
                                                        .setValue(String.valueOf(price));

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }

        private void stockChecker(int position, int quantity) {


            firebaseDatabase.getReference(USERS)
                    .child(firebaseAuth.getCurrentUser().getUid())
                    .child(CART)
                    .orderByChild(PRODUCT_URL)
                    .equalTo(inboxList.get(position).getProductUrl())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String key = "";
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    key = ds.getKey();
                                }
                                firebaseDatabase.getReference(PRODUCTS)
                                        .child(key)
                                        .child(PRODUCT_STOCK)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                int stockLeft = Integer.parseInt(dataSnapshot.getValue().toString());
                                                if (stockLeft == quantity) {

                                                } else {

                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


        }

    }
}
