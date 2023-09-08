package com.santos.hci502.View.customer.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.santos.hci502.R;
import com.santos.hci502.View.customer.ViewProductIntent;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import static com.santos.hci502.Util.Constants.CART;
import static com.santos.hci502.Util.Constants.ITEM_QUANTITY;
import static com.santos.hci502.Util.Constants.PRODUCTS;
import static com.santos.hci502.Util.Constants.PRODUCT_DESC;
import static com.santos.hci502.Util.Constants.PRODUCT_NAME;
import static com.santos.hci502.Util.Constants.PRODUCT_PRICE;
import static com.santos.hci502.Util.Constants.PRODUCT_URL;
import static com.santos.hci502.Util.Constants.USERS;

public class QuantityDialogFragment extends AppCompatDialogFragment {

    Button btnMinus, btnPlus;
    TextView tvQuantity, tvPriceQuantity;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    Bundle bundle;

    Context context;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        context = getContext();
        final int[] quantity = {1};
        final int[] nPrice = {1};
        bundle = getArguments();
        firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference dataRef = firebaseDatabase.getReference(USERS + "/" + firebaseAuth.getCurrentUser().getUid());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogCustom);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View mview = inflater.inflate(R.layout.fragment_quantity_dialog, null);
        builder.setView(mview)
                .setTitle("Set Quantity")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String imageUrl = bundle.getString("imageUrl", "0");
                        String name = bundle.getString("name", "0");
                        String desc = bundle.getString("desc", "0");
                        String price = String.valueOf(nPrice[0]);
                        String stock = tvQuantity.getText().toString();

                        Query keyRef = firebaseDatabase.getReference(PRODUCTS).orderByChild(PRODUCT_URL).equalTo(imageUrl);
                        keyRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String key = "";
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    key = ds.getKey();
                                }
                                dataRef.child(CART).child(key).child(PRODUCT_URL).setValue(imageUrl);
                                dataRef.child(CART).child(key).child(PRODUCT_NAME).setValue(name);
                                dataRef.child(CART).child(key).child(PRODUCT_DESC).setValue(desc);
                                dataRef.child(CART).child(key).child(PRODUCT_PRICE).setValue(price);
                                dataRef.child(CART).child(key).child(ITEM_QUANTITY).setValue(stock)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                showAlertDone("Success", "Item added your cart");
                                            }
                                        });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });


        btnMinus = mview.findViewById(R.id.btnMinus);
        btnPlus = mview.findViewById(R.id.btnPlus);
        tvQuantity = mview.findViewById(R.id.tvQuantity);
        tvPriceQuantity = mview.findViewById(R.id.tvPriceQuantity);


        String stockLeft = bundle.getString("stock", "0");
        String price = bundle.getString("price", "0");
        tvQuantity.setText("1");
        tvPriceQuantity.setText("Price: " + price);

        nPrice[0] = Integer.parseInt(price);
        int oPrice = nPrice[0];
        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity[0] == Integer.parseInt(stockLeft)) {
                    Toast.makeText(getActivity(), "You are on the stock limit", Toast.LENGTH_SHORT).show();
                } else {
                    nPrice[0] += oPrice;
                    quantity[0] += 1;
                    tvPriceQuantity.setText("Price: " + String.valueOf(nPrice[0]));
                    tvQuantity.setText(String.valueOf(quantity[0]));
                }
            }
        });

        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity[0] != 1) {
                    nPrice[0] -= oPrice;
                    quantity[0] -= 1;
                    tvQuantity.setText(String.valueOf(quantity[0]));
                    tvPriceQuantity.setText("Price: " + String.valueOf(nPrice[0]));
                }
            }
        });


        return builder.create();
    }

    public void showAlertDone(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogCustom);
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

}
