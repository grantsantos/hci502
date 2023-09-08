package com.santos.hci502.View.admin.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.santos.hci502.Adapter.ProductSearchAdapter;
import com.santos.hci502.Adapter.ProductsAdminAdapter;
import com.santos.hci502.Adapter.ProductsClientAdapter;
import com.santos.hci502.Model.ProductsModel;
import com.santos.hci502.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import static com.santos.hci502.Util.Constants.PRODUCTS;
import static com.santos.hci502.Util.Constants.PRODUCT_DESC;
import static com.santos.hci502.Util.Constants.PRODUCT_NAME;
import static com.santos.hci502.Util.Constants.PRODUCT_PRICE;
import static com.santos.hci502.Util.Constants.PRODUCT_STOCK;
import static com.santos.hci502.Util.Constants.PRODUCT_URL;

public class ProductsAdminFragment extends Fragment {
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference db;

    RecyclerView recyclerView;
    ProductsAdminAdapter productsAdminAdapter;
    ProductsClientAdapter productsClientAdapter;
    ProductSearchAdapter productSearchAdapter;
    boolean isLoading = false, isMaxData = false;
    String last_node = "", last_key = "";
    StaggeredGridLayoutManager gridLayoutManager;
    LinearLayoutManager linearLayoutManager;
    long pageSize = 0;
    List<ProductsModel> inboxModels = new ArrayList<>();
    //SearchView searchView;

    ArrayList<String> productNameList;
    ArrayList<String> priceList;
    ArrayList<String> productUrlList;
    ArrayList<String> productDescList;
    ArrayList<String> productStockList;

    public ProductsAdminFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_products_admin, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.android_search);
        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) menuItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setQueryHint("Search Name/Price");
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchedString) {
                if(!searchedString.isEmpty()){
                    setAdapter(searchedString);
                }else{
                    onResume();
                }
                return false;
            }

        });


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        View v = getView();
        firebaseAuth = FirebaseAuth.getInstance();
        db = firebaseDatabase.getReference().child(PRODUCTS);
        db.keepSynced(true);
        recyclerView = v.findViewById(R.id.rv_Products_admin);
        //searchView = v.findViewById(R.id.searchProduct);
        productNameList = new ArrayList<>();
        priceList = new ArrayList<>();
        productUrlList = new ArrayList<>();
        productDescList = new ArrayList<>();
        productStockList = new ArrayList<>();


        //textViewNoInbox = v.findViewById(R.id.textView_no_dashboard);
        linearLayoutManager = new LinearLayoutManager(getActivity());

        productsClientAdapter = new ProductsClientAdapter(getActivity());
        productsAdminAdapter = new ProductsAdminAdapter((getActivity()));


/*
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchedString) {
                if (!searchedString.isEmpty()) {
                    setAdapter(searchedString);
                } else {
                    productNameList.clear();
                    priceList.clear();
                    productUrlList.clear();
                    recyclerView.removeAllViews();

                    inboxModels.clear();
                    productsAdminAdapter.removeAllItems();
                    productsClientAdapter.removeAllItems();
                    last_node = "";
                    getLastKeyFromDatabase();

                    if(firebaseAuth.getCurrentUser().getEmail().equals("admin@gmail.com")) {
                        recyclerView.setLayoutManager(linearLayoutManager);
                        recyclerView.setAdapter(productsAdminAdapter);
                    }else{
                        recyclerView.setLayoutManager(gridLayoutManager);
                        recyclerView.setAdapter(productsClientAdapter);
                    }
                }
                return false;
            }
        });
*/

    }

    void setAdapter(String searchedString) {
        DatabaseReference dataRef = firebaseDatabase.getReference(PRODUCTS);
        dataRef.orderByChild(PRODUCT_NAME).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    productNameList.clear();
                    priceList.clear();
                    productUrlList.clear();
                    productDescList.clear();
                    productStockList.clear();
                    recyclerView.removeAllViews();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String name = ds.child(PRODUCT_NAME).getValue(String.class);
                        String price = ds.child(PRODUCT_PRICE).getValue(String.class);
                        String url = ds.child(PRODUCT_URL).getValue(String.class);
                        String desc = ds.child(PRODUCT_DESC).getValue(String.class);
                        String stock = ds.child(PRODUCT_STOCK).getValue(String.class);

                        if (name.toLowerCase().contains(searchedString.toLowerCase())) {
                            productNameList.add(name);
                            priceList.add(price);
                            productUrlList.add(url);
                            productDescList.add(desc);
                            productStockList.add(stock);
                        } else if (price.toLowerCase().contains(searchedString.toLowerCase())) {
                            productNameList.add(name);
                            priceList.add(price);
                            productUrlList.add(url);
                            productDescList.add(desc);
                            productStockList.add(stock);
                        }

                    }

                    productSearchAdapter = new ProductSearchAdapter(getActivity(), productNameList, priceList, productUrlList,
                            productDescList, productStockList);

                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.setAdapter(productSearchAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                                .child(PRODUCTS)
                                .orderByChild(PRODUCT_NAME);
                        // Error here when final product deleted.limitToFirst((int) pageSize);

                    else
                        query = FirebaseDatabase.getInstance().getReference()
                                .child(PRODUCTS)
                                .orderByChild(PRODUCT_NAME)
                                //.startAt(last_node)
                                .limitToFirst((int) pageSize);

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChildren()) {
                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                    inboxModels.add(dataSnapshot1.getValue(ProductsModel.class));
                                }
                                last_node = "end";
                                productsAdminAdapter.addAll(inboxModels);
                                productsClientAdapter.addAll(inboxModels);
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
                .child(PRODUCTS)
                .orderByChild(PRODUCT_PRICE)
                .limitToLast(1);

        getLastKey.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot lastkey : dataSnapshot.getChildren()) {
                    last_key = lastkey.getKey();
                }
                if (last_key == "") {
                    Toast.makeText(getActivity(), "NO PRODUCTS", Toast.LENGTH_SHORT).show();
                    // textViewNoInbox.setVisibility(View.VISIBLE);
                } else {
                    getProducts();
                    //textViewNoInbox.setVisibility(View.GONE);
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
        productsAdminAdapter.removeAllItems();
        productsClientAdapter.removeAllItems();
        last_node = "";
        getLastKeyFromDatabase();
        gridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        linearLayoutManager = new LinearLayoutManager(getActivity());

        if (firebaseAuth.getCurrentUser().getEmail().equals("admin@gmail.com")) {
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(productsAdminAdapter);
        } else {
            recyclerView.setLayoutManager(gridLayoutManager);
            recyclerView.setAdapter(productsClientAdapter);
        }

    }
}
