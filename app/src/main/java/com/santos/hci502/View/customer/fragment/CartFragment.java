package com.santos.hci502.View.customer.fragment;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.santos.hci502.Adapter.CartAdapter;
import com.santos.hci502.Model.CartModel;
import com.santos.hci502.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

import static com.santos.hci502.Util.Constants.CART;
import static com.santos.hci502.Util.Constants.PRODUCT_PRICE;
import static com.santos.hci502.Util.Constants.USERS;


public class CartFragment extends Fragment {
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference db;

    RecyclerView recyclerView;
    CartAdapter cartAdapter;
    boolean isLoading = false, isMaxData = false;
    LinearLayoutManager linearLayoutManager;
    List<CartModel> inboxModels = new ArrayList<>();
    String last_node = "", last_key = "";
    long pageSize = 0;
    TextView tvTotalPrice;

    int calculateTotal = 0;
    ValueEventListener listener;
    DatabaseReference dataRef;

    CardView cvCheckOut;
    TextView tvNoItems;
    Button btnCheckout;

    public CartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        View v = getView();
        firebaseAuth = FirebaseAuth.getInstance();
        db = firebaseDatabase.getReference().child(USERS);
        db.keepSynced(true);
        dataRef = firebaseDatabase.getReference(USERS).child(firebaseAuth.getCurrentUser().getUid())
                .child(CART);
        recyclerView = v.findViewById(R.id.rv_cart);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        cartAdapter = new CartAdapter(getActivity());
        tvTotalPrice = v.findViewById(R.id.tvTotalPrice);
        cvCheckOut = v.findViewById(R.id.cv_checkOut);
        tvNoItems = v.findViewById(R.id.tvNoItem) ;
        btnCheckout = v.findViewById(R.id.btnCheckOut) ;
        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckOutDialogFragment dialog = new CheckOutDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putString("total", tvTotalPrice.getText().toString());
                dialog.setArguments(bundle);
                dialog.setCancelable(false);
                dialog.show(getFragmentManager(), "Dialog");
            }
        });
    }

    private void getProducts() {

        if (!isMaxData) {
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    pageSize = dataSnapshot.getChildrenCount();
                    Query query;
                    if (TextUtils.isEmpty(last_node))
                        query = FirebaseDatabase.getInstance().getReference()
                                .child(USERS)
                                .child(firebaseAuth.getCurrentUser().getUid())
                                .child(CART)
                                .orderByChild(PRODUCT_PRICE);
                        // Error here when final product deleted.limitToFirst((int) pageSize);

                    else
                        query = FirebaseDatabase.getInstance().getReference()
                                .child(USERS)
                                .child(firebaseAuth.getCurrentUser().getUid())
                                .child(CART)
                                .orderByChild(PRODUCT_PRICE)
                                //.startAt(last_node)
                                .limitToFirst((int) pageSize);

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChildren()) {
                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                    inboxModels.add(dataSnapshot1.getValue(CartModel.class));
                                }
                                last_node = "end";
                                cartAdapter.addAll(inboxModels);
                                isLoading = false;
                            } else {
                                isLoading = false;
                                isMaxData = true;
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            isLoading = false;
                        }
                    });

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void getLastKeyFromDatabase() {
        final Query getLastKey = FirebaseDatabase.getInstance().getReference()
                .child(USERS)
                .child(firebaseAuth.getCurrentUser().getUid())
                .child(CART)
                .orderByChild(PRODUCT_PRICE)
                .limitToLast(1);

        getLastKey.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot lastkey : dataSnapshot.getChildren()) {
                    last_key = lastkey.getKey();
                }
                if (last_key == "") {
                    cvCheckOut.setVisibility(View.GONE);
                    tvNoItems.setVisibility(View.VISIBLE);
                } else {
                    getProducts();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Can't get last key " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void getTotal(){
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    calculateTotal = 0;
                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                        int price = Integer.parseInt(String.valueOf(ds.child(PRODUCT_PRICE).getValue()));
                        calculateTotal += price;
                    }
                    tvTotalPrice.setText(String.valueOf(calculateTotal));
                    cvCheckOut.setVisibility(View.VISIBLE);
                }else{
                    cvCheckOut.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    tvNoItems.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        dataRef.addValueEventListener(listener);
    }

    @Override
    public void onResume() {
        super.onResume();
        inboxModels.clear();
        cartAdapter.removeAllItems();
        last_node = "";
        getLastKeyFromDatabase();
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(cartAdapter);
        calculateTotal = 0;
        getTotal();
    }

    @Override
    public void onStop() {
        super.onStop();
       dataRef.removeEventListener(listener);

    }
}
