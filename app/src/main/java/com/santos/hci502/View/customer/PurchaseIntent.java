package com.santos.hci502.View.customer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.santos.hci502.Adapter.PurchaseIntentAdapter;
import com.santos.hci502.Model.CartModel;
import com.santos.hci502.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.santos.hci502.Util.Constants.ADDRESS;
import static com.santos.hci502.Util.Constants.CART;
import static com.santos.hci502.Util.Constants.PRODUCT_NAME;
import static com.santos.hci502.Util.Constants.PURCHASE_LOG;

public class PurchaseIntent extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference db;

    PurchaseIntentAdapter purchaseIntentAdapter;
    boolean isLoading = false, isMaxData = false;
    LinearLayoutManager linearLayoutManager;
    List<CartModel> inboxModels = new ArrayList<>();
    String last_node = "", last_key = "";
    long pageSize = 0;

    RecyclerView recyclerView;
    Intent intent;
    @BindView(R.id.tvDate)
    TextView tvDate;
    @BindView(R.id.tvTotal)
    TextView tvTotal;
    @BindView(R.id.tvAddress)
    TextView tvAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_intent);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        intent = getIntent();
        firebaseAuth = FirebaseAuth.getInstance();
        db = firebaseDatabase.getReference().child(PURCHASE_LOG)
                .child(firebaseAuth.getCurrentUser().getUid());
        db.keepSynced(true);

        recyclerView = findViewById(R.id.rv_purchase_intent);
        linearLayoutManager = new LinearLayoutManager(this);
        purchaseIntentAdapter = new PurchaseIntentAdapter(this);
        getLastKeyFromDatabase();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(purchaseIntentAdapter);
        tvDate.setText(intent.getStringExtra("date"));
        tvTotal.setText(intent.getStringExtra("total"));
        getAddress();

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
                                .child(PURCHASE_LOG)
                                .child(firebaseAuth.getCurrentUser().getUid())
                                .child(intent.getStringExtra("timeStamp"))
                                .child(CART)
                                .orderByChild(PRODUCT_NAME);
                        // Error here when final product deleted.limitToFirst((int) pageSize);

                    else
                        query = FirebaseDatabase.getInstance().getReference()
                                .child(PURCHASE_LOG)
                                .child(firebaseAuth.getCurrentUser().getUid())
                                .child(intent.getStringExtra("timeStamp"))
                                .child(CART)
                                .orderByChild(PRODUCT_NAME)
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
                                purchaseIntentAdapter.addAll(inboxModels);
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
                .child(PURCHASE_LOG)
                .child(firebaseAuth.getCurrentUser().getUid())
                .child(intent.getStringExtra("timeStamp"))
                .child(CART)
                .orderByChild(PRODUCT_NAME)
                .limitToLast(1);

        getLastKey.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot lastkey : dataSnapshot.getChildren()) {
                    last_key = lastkey.getKey();
                }
                if (last_key == "") {

                } else {
                    getProducts();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Can't get last key " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void getAddress(){
        firebaseDatabase.getReference(PURCHASE_LOG)
                .child(firebaseAuth.getCurrentUser().getUid())
                .child(intent.getStringExtra("timeStamp"))
                .child(ADDRESS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            tvAddress.setText(dataSnapshot.getValue().toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
