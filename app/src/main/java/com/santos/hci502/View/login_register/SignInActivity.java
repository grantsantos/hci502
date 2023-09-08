package com.santos.hci502.View.login_register;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.santos.hci502.Controller.login_register.SignInController;
import com.santos.hci502.Controller.login_register.SignInControllerImpl;
import com.santos.hci502.R;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignInActivity extends AppCompatActivity implements SignInController.SignInControllerView {

    @BindView(R.id.textInputEmail)
    TextInputLayout textInputEmail;
    @BindView(R.id.textInputPassword)
    TextInputLayout textInputPassword;
    @BindView(R.id.buttonSignIn)
    Button buttonSignIn;
    @BindView(R.id.buttonRegister)
    Button buttonRegister;

    ProgressDialog progressDialog;
    SignInControllerImpl signInController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);
        FirebaseApp.initializeApp(this);
        progressDialog = new ProgressDialog(this, R.style.AlertDialogCustom);
        signInController = new SignInControllerImpl(this, this);

    }

    @OnClick({R.id.buttonSignIn, R.id.buttonRegister})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.buttonSignIn:
                signInController.requestLogin(textInputEmail.getEditText().getText().toString(),
                        textInputPassword.getEditText().getText().toString());
                break;
            case R.id.buttonRegister:
                startActivity(new Intent(SignInActivity.this, RegisterActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                break;
        }
    }


    @Override
    public void emailEmptyErrorMessage() {
        textInputEmail.setError("Required");
    }

    @Override
    public void passwordEmptyErrorMessage() {
        textInputPassword.setError("Required");
    }

    @Override
    public void toastMessage(String message) {
        Toast.makeText(SignInActivity.this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showProgressDialog(String message) {
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    public void hideProgressDialog() {
        progressDialog.dismiss();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //signInController.onStart();
    }
}
