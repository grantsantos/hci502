package com.santos.hci502.View.login_register;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.santos.hci502.Controller.login_register.RegisterController;
import com.santos.hci502.Controller.login_register.RegisterControllerImpl;
import com.santos.hci502.R;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity implements RegisterController.RegsiterControllerView {

    @BindView(R.id.imageViewRegister)
    ImageView imageViewRegister;
    @BindView(R.id.textViewPhoto)
    TextView textViewPhoto;
    @BindView(R.id.textInputName)
    TextInputLayout textInputName;
    @BindView(R.id.textInputAddress)
    TextInputLayout textInputAddress;
    @BindView(R.id.textInputContact)
    TextInputLayout textInputContact;
    @BindView(R.id.textInputRegisterEmail)
    TextInputLayout textInputRegisterEmail;
    @BindView(R.id.textInputRegisterPassword)
    TextInputLayout textInputRegisterPassword;

    RegisterController registerController;
    ArrayList<byte[]> bytesArray;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Register");
        progressDialog = new ProgressDialog(this,R.style.AlertDialogCustom);
        bytesArray = new ArrayList<>();
        registerController = new RegisterControllerImpl(this, this, textViewPhoto, imageViewRegister);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            registerController.imageSavingCompression(data, RegisterActivity.this, bytesArray,
                    textViewPhoto, imageViewRegister);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


    @OnClick({R.id.cardViewImageView, R.id.buttonRegister})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cardViewImageView:
                imageChooser();
                break;
            case R.id.buttonRegister:
                if (!registerController.requestRegister(textInputName, textInputAddress, textInputContact, textInputRegisterEmail, textInputRegisterPassword)) {
                    return;
                }
                String name = textInputName.getEditText().getText().toString();
                String address = textInputAddress.getEditText().getText().toString();
                String contact = textInputContact.getEditText().getText().toString();
                String email = textInputRegisterEmail.getEditText().getText().toString();
                String password = textInputRegisterPassword.getEditText().getText().toString();
                registerController.registerCredentials(name, address, contact, email, password, bytesArray);
                break;
        }
    }

    @Override
    public void imageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
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
    public void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }



}
