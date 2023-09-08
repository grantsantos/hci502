package com.santos.hci502.View.drawer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.Handler;
import android.view.View;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.santos.hci502.R;
import com.santos.hci502.Util.CircleTransform;
import com.santos.hci502.Util.Constants;
import com.santos.hci502.View.admin.fragment.ProductsAdminFragment;
import com.santos.hci502.View.customer.AboutActivity;
import com.santos.hci502.View.customer.AccountInfoActivity;
import com.santos.hci502.View.customer.PurchaseHistory;
import com.santos.hci502.View.customer.fragment.CartFragment;
import com.santos.hci502.View.login_register.RegisterActivity;
import com.santos.hci502.View.login_register.SignInActivity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import static com.santos.hci502.Util.Constants.ACCEPTED;
import static com.santos.hci502.Util.Constants.NULL;
import static com.santos.hci502.Util.Constants.REJECTED;
import static com.santos.hci502.Util.Constants.TOP_UP_STATUS;
import static com.santos.hci502.Util.Constants.USERS;

public class CustomerNavigationDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference databaseReferenceProfilePic;
    DatabaseReference databaseReferenceName;
    DatabaseReference databaseReferenceBalance;
    TextView textViewName, textViewBalance;
    ImageView imageViewProfPic;
    ProgressDialog progressDialog;


    boolean dataIsLoaded = false;
    NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_navigation_drawer);
        FirebaseApp.initializeApp(this);
        progressDialog = new ProgressDialog(CustomerNavigationDrawer.this, R.style.AlertDialogCustom);
        firebaseAuth = FirebaseAuth.getInstance();
        String userUid = firebaseAuth.getCurrentUser().getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users/" + userUid);

        databaseReferenceProfilePic = databaseReference.child("profilePicUrl");
        databaseReferenceName = databaseReference.child("name");
        databaseReferenceBalance = databaseReference.child("balance");

        databaseReferenceProfilePic.keepSynced(true);
        databaseReferenceName.keepSynced(true);
        databaseReferenceBalance.keepSynced(true);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        fab.setVisibility(View.GONE);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        View navHeaderView = navigationView.getHeaderView(0);

        textViewName = navHeaderView.findViewById(R.id.textViewName);
        textViewBalance = navHeaderView.findViewById(R.id.textViewBalance);
        imageViewProfPic = navHeaderView.findViewById(R.id.imageViewProfilePic);


        if(getIntent().getStringExtra("PRODUCT_INTENT") !=null){
            getSupportActionBar().setTitle("My Cart");
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_customer_container,
                    new CartFragment()).commit();
            navigationView.getMenu().getItem(1).setChecked(true);
        }else if(savedInstanceState == null){
            getSupportActionBar().setTitle("Products");
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_customer_container,
                    new ProductsAdminFragment()).commit();
            navigationView.getMenu().getItem(0).setChecked(true);
        }



    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            showAlertCloseApp("", "Do you want to exit this app?");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cart_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.cartMenu) {
            getSupportActionBar().setTitle("My Cart");
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_customer_container,
                    new CartFragment()).commit();
            navigationView.getMenu().getItem(1).setChecked(true);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_products) {
            getSupportActionBar().setTitle("Products");
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_customer_container,
                    new ProductsAdminFragment()).commit();

        } else if (id == R.id.nav_cart) {
            getSupportActionBar().setTitle("My Cart");
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_customer_container,
                    new CartFragment()).commit();
        } else if (id == R.id.nav_acc) {
            startActivity(new Intent(CustomerNavigationDrawer.this, AccountInfoActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        } else if (id == R.id.nav_log_out) {
            firebaseAuth.signOut();
            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
            finish();
        } else if (id == R.id.nav_purchase_history) {
            startActivity(new Intent(getApplicationContext(), PurchaseHistory.class));
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(getApplicationContext(), AboutActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void displayUserInfo() {
        databaseReferenceProfilePic.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String imageUrl = dataSnapshot.getValue().toString();

                    Target target = new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            imageViewProfPic.setVisibility(View.VISIBLE);
                            imageViewProfPic.setImageBitmap(bitmap);
                            hideProgressDialog();
                            topUpStatus("Notice");
                            dataIsLoaded = true;
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                            showProgressDialog("Loading Data...");
                        }
                    };
                    imageViewProfPic.setTag(target);
                    Picasso.get().load(imageUrl).transform(new CircleTransform()).into(target);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        databaseReferenceName.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.getValue().toString();
                    textViewName.setText(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        databaseReferenceBalance.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String balance = dataSnapshot.getValue().toString();
                    textViewBalance.setText("Balance: " + balance);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void topUpStatus(String title) {
        firebaseDatabase.getReference(USERS)
                .child(firebaseAuth.getCurrentUser().getUid())
                .child(TOP_UP_STATUS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String message = "";
                            String status = dataSnapshot.getValue().toString();
                            if (status.equals(REJECTED)) {
                                message = "Your top up request has been rejected";
                                AlertDialog.Builder builder = new AlertDialog.Builder(CustomerNavigationDrawer.this);
                                builder.setTitle(title);
                                builder.setMessage(message);
                                builder.setCancelable(false);
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        firebaseDatabase.getReference(USERS)
                                                .child(firebaseAuth.getCurrentUser().getUid())
                                                .child(TOP_UP_STATUS).setValue(NULL);
                                    }
                                });
                                builder.show();
                            } else if (status.equals(ACCEPTED)) {
                                message = "Your top up request has been accepted";
                                AlertDialog.Builder builder = new AlertDialog.Builder(CustomerNavigationDrawer.this);
                                builder.setTitle(title);
                                builder.setMessage(message);
                                builder.setCancelable(false);
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        firebaseDatabase.getReference(USERS)
                                                .child(firebaseAuth.getCurrentUser().getUid())
                                                .child(TOP_UP_STATUS).setValue(NULL);
                                    }
                                });
                                builder.show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    public void showAlertCloseApp(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CustomerNavigationDrawer.this, R.style.AlertDialogCustom);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    public void showAlertNoInternet(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CustomerNavigationDrawer.this, R.style.AlertDialogCustom);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    finishAndRemoveTask();
                } else {
                    finish();
                }
            }
        });
        builder.show();
    }

    public void showProgressDialog(String message) {
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void hideProgressDialog() {
        progressDialog.dismiss();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!Constants.isNetworkAvailable(this)) {
            if (!dataIsLoaded)
                showAlertNoInternet("No Internet Connection", "Please connect to the internet to use this app");
        } else {
            showProgressDialog("Loading Data...");
            if (!dataIsLoaded) {
                displayUserInfo();
            }else{
                hideProgressDialog();
            }
        }
    }
}
