package com.santos.hci502.View.drawer;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.View;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.santos.hci502.R;
import com.santos.hci502.View.admin.activity.AddProductActivity;
import com.santos.hci502.View.admin.fragment.AdminTopUpListFragment;
import com.santos.hci502.View.admin.fragment.ProductsAdminFragment;
import com.santos.hci502.View.customer.AboutActivity;
import com.santos.hci502.View.login_register.SignInActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import static com.santos.hci502.Util.Constants.ACCEPTED;
import static com.santos.hci502.Util.Constants.ADMIN;
import static com.santos.hci502.Util.Constants.ADMIN_UID;
import static com.santos.hci502.Util.Constants.BALANCE;
import static com.santos.hci502.Util.Constants.NULL;
import static com.santos.hci502.Util.Constants.REJECTED;
import static com.santos.hci502.Util.Constants.TOP_UP_NOTIF;
import static com.santos.hci502.Util.Constants.TOP_UP_STATUS;
import static com.santos.hci502.Util.Constants.USERS;

public class AdminNavigationDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FloatingActionButton fab;
    TextView tvProfit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_navigation_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        fab = (FloatingActionButton) findViewById(R.id.fab);

        //getProfit();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                finish();
                startActivity(new Intent(AdminNavigationDrawer.this, AddProductActivity.class));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View navHeaderView = navigationView.getHeaderView(0);
        tvProfit = navHeaderView.findViewById(R.id.tvProfit);
        getProfit();

        if (savedInstanceState == null) {
            fab.setVisibility(View.VISIBLE);
            getSupportActionBar().setTitle("Products");
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_bar_admin_container,
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
        // getMenuInflater().inflate(R.menu.admin_navigation_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_bar_admin_container,
                    new ProductsAdminFragment()).commit();
            fab.setVisibility(View.VISIBLE);

        } else if (id == R.id.nav_top_up) {
            getSupportActionBar().setTitle("Top Up Requests");
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_bar_admin_container,
                    new AdminTopUpListFragment()).commit();
            fab.setVisibility(View.GONE);
        } else if (id == R.id.nav_log_out) {
            firebaseAuth.signOut();
            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
            finish();
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(getApplicationContext(), AboutActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void getProfit() {
        firebaseDatabase.getReference(ADMIN).child(ADMIN_UID)
                .child(BALANCE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            tvProfit.setText("Asdf");
                            tvProfit.setText("Profit: " + String.valueOf(dataSnapshot.getValue()));
                            topUpStatus("Notice");
                        }else{
                            topUpStatus("Notice");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public void showAlertCloseApp(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminNavigationDrawer.this, R.style.AlertDialogCustom);
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

    public void topUpStatus(String title) {
        firebaseDatabase.getReference(ADMIN)
                .child(ADMIN_UID)
                .child(TOP_UP_NOTIF)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String message = "";
                            long status = (long) dataSnapshot.getValue();
                            if (status == 1) {
                                message = "There is/are new top up request.";
                                AlertDialog.Builder builder = new AlertDialog.Builder(AdminNavigationDrawer.this);
                                builder.setTitle(title);
                                builder.setMessage(message);
                                builder.setCancelable(false);
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        firebaseDatabase.getReference(ADMIN)
                                                .child(ADMIN_UID)
                                                .child(TOP_UP_NOTIF).setValue(0);
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

}
