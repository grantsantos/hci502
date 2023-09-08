package com.santos.hci502.View;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.santos.hci502.R;
import com.santos.hci502.Util.Constants;
import com.santos.hci502.View.drawer.AdminNavigationDrawer;
import com.santos.hci502.View.drawer.CustomerNavigationDrawer;
import com.santos.hci502.View.login_register.SignInActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashScreen extends AppCompatActivity {
    private static int SPLASH_SCREEN_TIME = 1500;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference custRef = firebaseDatabase.getReference("users");
    DatabaseReference adMinRef = firebaseDatabase.getReference("admin");
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ButterKnife.bind(this);
    }

    public void getCurrentSession() {
        if (firebaseAuth.getCurrentUser() != null) {
            if (Constants.isNetworkAvailable(getApplicationContext())) {
                String emailLoggedIn = firebaseAuth.getCurrentUser().getEmail();
                String uidLoggedIn = firebaseAuth.getCurrentUser().getUid();
                custRef.child(uidLoggedIn).child("email")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.exists()) {
                                    if (emailLoggedIn.equals(dataSnapshot.getValue().toString())) {
                                        Intent intent = new Intent(getApplicationContext(), CustomerNavigationDrawer.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                } else {
                                    adMinRef.child(uidLoggedIn).child("email")
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        if (emailLoggedIn.equals(dataSnapshot.getValue().toString())) {
                                                            startActivity(new Intent(getApplicationContext(), AdminNavigationDrawer.class));
                                                            finish();
                                                        }
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
            } else {

                AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreen.this, R.style.AlertDialogCustom);
                builder.setTitle("Connection Failed");
                builder.setMessage("Please connect to the internet");
                builder.setCancelable(false);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        builder.show();
                        progressBar.setVisibility(View.GONE);
                    }
                }, SPLASH_SCREEN_TIME);


            }
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                    finish();
                }
            }, SPLASH_SCREEN_TIME);

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        getCurrentSession();
    }
}
