package com.santos.hci502.Controller.login_register;

import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public interface RegisterController {
    interface RegsiterControllerView{
        void imageChooser();
        void showProgressDialog(String message);
        void hideProgressDialog();
        void toastMessage(String message);

    }
    boolean requestRegister(TextInputLayout name, TextInputLayout address, TextInputLayout contact, TextInputLayout email, TextInputLayout password);
    void registerCredentials(String name, String address, String contact, String email, String password,
                             ArrayList<byte[]> bytesArray);
    void imageSavingCompression(Intent data, Context context, ArrayList<byte[]> bytesArray, TextView textViewPhoto,
                                ImageView imageViewRegister);
}
