package com.santos.hci502.Controller.login_register;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.santos.hci502.Controller.login_register.SignInController;
import com.santos.hci502.R;
import com.santos.hci502.Util.Constants;
import com.santos.hci502.View.drawer.AdminNavigationDrawer;
import com.santos.hci502.View.drawer.CustomerNavigationDrawer;

import androidx.annotation.NonNull;

public class SignInControllerImpl implements SignInController {
    Context context;
    SignInControllerView signInControllerView;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference custRef = firebaseDatabase.getReference("users");
    DatabaseReference adMinRef = firebaseDatabase.getReference("admin");

    public SignInControllerImpl(SignInControllerView signInControllerView, Context context) {
        this.signInControllerView = signInControllerView;
        this.context = context;
    }

    @Override
    public void onStart() {
        getCurrentSession();
    }

    @Override
    public void signInAccount(String email, String password) {
        signInControllerView.showProgressDialog("Signing in...");
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        String emailLoggedIn = email;

                        custRef.orderByChild("email").equalTo(email)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()){
                                            signInControllerView.hideProgressDialog();
                                            context.startActivity(new Intent(context, CustomerNavigationDrawer.class));
                                            ((Activity) context).finish();
                                        }else{
                                            adMinRef.orderByChild("email").equalTo(emailLoggedIn)
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            if(dataSnapshot.exists()){
                                                                signInControllerView.hideProgressDialog();
                                                                context.startActivity(new Intent(context, AdminNavigationDrawer.class));
                                                                ((Activity) context).finish();
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


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                signInControllerView.hideProgressDialog();
                signInControllerView.toastMessage(e.getMessage());
            }
        });
    }

    @Override
    public void getCurrentSession() {
        if (firebaseAuth.getCurrentUser() != null) {
            String emailLoggedIn = firebaseAuth.getCurrentUser().getEmail();
            String uidLoggedIn = firebaseAuth.getCurrentUser().getUid();
            custRef.child(uidLoggedIn).child("email")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                if(emailLoggedIn.equals(dataSnapshot.getValue().toString())) {
                                    context.startActivity(new Intent(context, CustomerNavigationDrawer.class));
                                    ((Activity) context).finish();
                                }
                            }else{
                                adMinRef.child(uidLoggedIn).child("email")
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.exists()){
                                                    if(emailLoggedIn.equals(dataSnapshot.getValue().toString())) {
                                                        context.startActivity(new Intent(context, AdminNavigationDrawer.class));
                                                        ((Activity) context).finish();
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
        }
    }

    @Override
    public void checkInternetConnection(String email, String password) {
        if (Constants.isNetworkAvailable(context)) {
            signInAccount(email, password);
        } else {
            signInControllerView.toastMessage(context.getString(R.string.NO_INTERNET));
        }
    }

    @Override
    public void requestLogin(String email, String password) {
        if (email.isEmpty()) {
            signInControllerView.emailEmptyErrorMessage();
        } else if (password.isEmpty()) {
            signInControllerView.passwordEmptyErrorMessage();
        } else {
            checkInternetConnection(email, password);
        }
    }
}
