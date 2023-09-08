package com.santos.hci502.View.admin.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.santos.hci502.Adapter.ATUL_ADAPTER;
import com.santos.hci502.Model.ATUL_MODEL;
import com.santos.hci502.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.santos.hci502.Util.Constants.*;

public class AdminTopUpListFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference db;

    RecyclerView recyclerView;
    ATUL_ADAPTER atul_adapter;
    boolean isLoading = false, isMaxData = false;
    String last_node = "", last_key = "";
    LinearLayoutManager layoutManager;
    long pageSize = 0;
    List<ATUL_MODEL> inboxModels = new ArrayList<>();
    TextView tvNoTopUp;

    public AdminTopUpListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_top_up_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        View v = getView();
        firebaseAuth = FirebaseAuth.getInstance();
        db = firebaseDatabase.getReference().child(ADMIN).child(firebaseAuth.getCurrentUser().getUid())
                .child(TOP_UP_REQUESTS);
        db.keepSynced(true);
        recyclerView = v.findViewById(R.id.rv_topUpList);
        tvNoTopUp = v.findViewById(R.id.tV_noTopUpList);
        //textViewNoInbox = v.findViewById(R.id.textView_no_dashboard);
        //getLastKeyFromDatabase();

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        /*DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);*/

        atul_adapter = new ATUL_ADAPTER((getActivity()));
        recyclerView.setAdapter(atul_adapter);

    }

    private void getTopUpList() {

        if (!isMaxData) {
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    pageSize = dataSnapshot.getChildrenCount();
                    Query query;
                    if (TextUtils.isEmpty(last_node))
                        query = FirebaseDatabase.getInstance().getReference()
                                .child(ADMIN).child(firebaseAuth.getCurrentUser().getUid())
                                .child(TOP_UP_REQUESTS)
                                .orderByChild(TIMESTAMP);
                        // .limitToFirst((int) pageSize);

                    else
                        query = FirebaseDatabase.getInstance().getReference()
                                .child(ADMIN).child(firebaseAuth.getCurrentUser().getUid())
                                .child(TOP_UP_REQUESTS)
                                .orderByChild(TIMESTAMP)
                                //.startAt(last_node)
                                .limitToFirst((int) pageSize);

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChildren()) {
                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                    inboxModels.add(dataSnapshot1.getValue(ATUL_MODEL.class));
                                }
                                last_node = "end";
                                atul_adapter.addAll(inboxModels);
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
                .child(ADMIN).child(firebaseAuth.getCurrentUser().getUid())
                .child(TOP_UP_REQUESTS)
                .orderByChild(TIMESTAMP)
                .limitToLast(1);

        getLastKey.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot lastkey : dataSnapshot.getChildren()) {
                    last_key = lastkey.getKey();
                }
                if (last_key == "") {
                    recyclerView.setVisibility(View.GONE);
                    tvNoTopUp.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    tvNoTopUp.setVisibility(View.GONE);
                    getTopUpList();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Can't get last key " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        inboxModels.clear();
        atul_adapter.removeAllItems();
        last_node = "";
        getLastKeyFromDatabase();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(atul_adapter);
    }
}
