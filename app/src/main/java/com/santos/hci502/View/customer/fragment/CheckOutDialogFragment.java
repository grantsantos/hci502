package com.santos.hci502.View.customer.fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
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
import com.santos.hci502.View.customer.PurchaseHistory;
import com.santos.hci502.View.drawer.CustomerNavigationDrawer;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import static com.santos.hci502.Util.Constants.ADDRESS;
import static com.santos.hci502.Util.Constants.ADMIN;
import static com.santos.hci502.Util.Constants.ADMIN_UID;
import static com.santos.hci502.Util.Constants.BALANCE;
import static com.santos.hci502.Util.Constants.BALANCE_LEFT;
import static com.santos.hci502.Util.Constants.CART;
import static com.santos.hci502.Util.Constants.ITEM_QUANTITY;
import static com.santos.hci502.Util.Constants.PRODUCTS;
import static com.santos.hci502.Util.Constants.PRODUCT_PRICE;
import static com.santos.hci502.Util.Constants.PRODUCT_STOCK;
import static com.santos.hci502.Util.Constants.PURCHASE_LOG;
import static com.santos.hci502.Util.Constants.PURCHASE_TOTAL;
import static com.santos.hci502.Util.Constants.TIMESTAMP;
import static com.santos.hci502.Util.Constants.USERS;


public class CheckOutDialogFragment extends AppCompatDialogFragment {
    EditText etAddress;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    String addrs = "";
    DatabaseReference fromRef;
    DatabaseReference toRef;
    long dateSentInMillis;
    ProgressDialog progressDialog;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogCustom);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View mview = inflater.inflate(R.layout.fragment_checkout_dialog, null);
        progressDialog = new ProgressDialog(getContext());
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        etAddress = mview.findViewById(R.id.etDeliveryAddress);
        dateSentInMillis = Calendar.getInstance().getTimeInMillis();
        DatabaseReference dataRef = firebaseDatabase.getReference(USERS).child(firebaseAuth.getCurrentUser().getUid()).child(ADDRESS);
        fromRef = firebaseDatabase.getReference(USERS)
                .child(firebaseAuth.getCurrentUser().getUid()).child(CART);
        toRef = firebaseDatabase.getReference(PURCHASE_LOG)
                .child(firebaseAuth.getCurrentUser().getUid()).child(String.valueOf(dateSentInMillis)).child(CART);

        dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    addrs = dataSnapshot.getValue().toString();
                    etAddress.setText(addrs);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        builder.setView(mview)
                .setTitle("Delivery Address")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showProgressDialog("...........");
                        Query query = firebaseDatabase.getReference(USERS)
                                .child(firebaseAuth.getCurrentUser().getUid()).child(CART);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                DatabaseReference stockUpdate = firebaseDatabase.getReference(PRODUCTS);
                                DatabaseReference balanceUpdate = firebaseDatabase.getReference(USERS)
                                        .child(firebaseAuth.getCurrentUser().getUid())
                                        .child(BALANCE);
                                int totalPrice = 0;
                                for(DataSnapshot ds: dataSnapshot.getChildren()){
                                    int price =  Integer.parseInt(String.valueOf(ds.child(PRODUCT_PRICE).getValue()));
                                    totalPrice += price;
                                }
                                int finalTotalPrice = totalPrice;
                                balanceUpdate.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        int balanceLeft = Integer.parseInt(String.valueOf(dataSnapshot.getValue()));

                                        if(balanceLeft >= finalTotalPrice){
                                            balanceLeft -= finalTotalPrice;
                                            balanceUpdate.setValue(String.valueOf(balanceLeft));
                                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                                                        String key = ds.getKey();
                                                        int cartQuantity = Integer.parseInt(String.valueOf(ds.child(ITEM_QUANTITY).getValue()));
                                                        stockUpdate.child(key).child(PRODUCT_STOCK).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                int stock = Integer.parseInt(String.valueOf(dataSnapshot.getValue()));
                                                                stock -= cartQuantity;
                                                                int finalStock = stock;
                                                                stockUpdate.child(key).child(PRODUCT_STOCK).setValue(String.valueOf(finalStock));

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
                                            firebaseDatabase.getReference(PURCHASE_LOG)
                                                    .child(firebaseAuth.getCurrentUser().getUid())
                                                    .child(String.valueOf(dateSentInMillis)).child(BALANCE_LEFT)
                                                    .setValue(String.valueOf(balanceLeft));
                                            processingCheckout();
                                            AlertDialog.Builder builder = new AlertDialog.Builder(mview.getContext(), R.style.AlertDialogCustom);
                                            builder.setTitle("Check Out Success!");
                                            builder.setMessage("Thank you for your purchase!");
                                            builder.setCancelable(false);
                                            builder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            });
                                            builder.show();
                                        }else{
                                            Toast.makeText(mview.getContext(), "Not Enough Balance", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        hideProgressDialog();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });


        return builder.create();
    }

    public void moveFirebaseRecord(DatabaseReference fromPath, final DatabaseReference toPath) {
        fromPath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                toPath.setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            //Toast.makeText(getContext(), "Sending failed", Toast.LENGTH_SHORT).show();
                        } else {
                            fromPath.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    hideProgressDialog();
                                }
                            });
                        }
                    }

                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }

    public void processingCheckout(){
        DatabaseReference myref = firebaseDatabase.getReference(PURCHASE_LOG)
                .child(firebaseAuth.getCurrentUser().getUid()).child(String.valueOf(dateSentInMillis)).child(TIMESTAMP);

        DatabaseReference addressRef = firebaseDatabase.getReference(PURCHASE_LOG)
                .child(firebaseAuth.getCurrentUser().getUid()).child(String.valueOf(dateSentInMillis)).child(ADDRESS);

        DatabaseReference totalRef = firebaseDatabase.getReference(PURCHASE_LOG)
                .child(firebaseAuth.getCurrentUser().getUid()).child(String.valueOf(dateSentInMillis)).child(PURCHASE_TOTAL);

        DatabaseReference profitRef = firebaseDatabase.getReference(ADMIN)
                .child(ADMIN_UID).child(BALANCE);


        myref.setValue(dateSentInMillis * -1);
        addressRef.setValue(etAddress.getText().toString());
        Bundle bundle = getArguments();
        totalRef.setValue(bundle.getString("total"), 0);
        String total = bundle.getString("total");
        profitRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Bundle bundle = getArguments();
                    int currentProfit = Integer.parseInt(String.valueOf(dataSnapshot.getValue()));
                    int newProfit = currentProfit + Integer.parseInt(total);
                    profitRef.setValue(String.valueOf(newProfit));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        moveFirebaseRecord(fromRef, toRef);
    }

    public void showProgressDialog(String message) {
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void hideProgressDialog() {
        progressDialog.dismiss();
    }

}
