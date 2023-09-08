package com.santos.hci502.View.customer;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.santos.hci502.Adapter.PurchaseAdapter;
import com.santos.hci502.Model.PurchaseHistoryModel;
import com.santos.hci502.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.santos.hci502.Util.Constants.PURCHASE_LOG;
import static com.santos.hci502.Util.Constants.TIMESTAMP;

public class PurchaseHistory extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference db;

    RecyclerView recyclerView;
    PurchaseAdapter purchaseAdapter;
    boolean isLoading = false, isMaxData = false;
    LinearLayoutManager linearLayoutManager;
    List<PurchaseHistoryModel> inboxModels = new ArrayList<>();
    String last_node = "", last_key = "";
    long pageSize = 0;
    @BindView(R.id.tvNoPurchase)
    TextView tvNoPurchase;
    @BindView(R.id.tv1)
    TextView tv1;
    @BindView(R.id.tv2)
    TextView tv2;
    @BindView(R.id.rv_purchase_history)
    RecyclerView rvPurchaseHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_history);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Purchase History");
        firebaseAuth = FirebaseAuth.getInstance();
        db = firebaseDatabase.getReference(PURCHASE_LOG).child(firebaseAuth.getCurrentUser().getUid());
        db.keepSynced(true);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.rv_purchase_history);
        recyclerView.setLayoutManager(linearLayoutManager);
        purchaseAdapter = new PurchaseAdapter(this);

        getLastKeyFromDatabase();
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(purchaseAdapter);
    }

    private void getHistory() {

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
                                .orderByChild(TIMESTAMP);
                        // Error here when final product deleted.limitToFirst((int) pageSize);

                    else
                        query = FirebaseDatabase.getInstance().getReference()
                                .child(PURCHASE_LOG)
                                .child(firebaseAuth.getCurrentUser().getUid())
                                .orderByChild(TIMESTAMP)
                                .limitToFirst((int) pageSize);

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChildren()) {
                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                    inboxModels.add(dataSnapshot1.getValue(PurchaseHistoryModel.class));
                                }
                                last_node = "end";
                                purchaseAdapter.addAll(inboxModels);
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
                .orderByChild(TIMESTAMP)
                .limitToLast(1);

        getLastKey.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot lastkey : dataSnapshot.getChildren()) {
                    last_key = lastkey.getKey();
                }
                if (last_key == "") {
                    tvNoPurchase.setVisibility(View.VISIBLE);
                } else {
                    tv1.setVisibility(View.VISIBLE);
                    tv2.setVisibility(View.VISIBLE);
                    rvPurchaseHistory.setVisibility(View.VISIBLE);
                    getHistory();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Can't get last key " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

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
