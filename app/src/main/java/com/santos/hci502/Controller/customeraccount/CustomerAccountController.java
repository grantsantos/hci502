package com.santos.hci502.Controller.customeraccount;

import android.content.Context;
import android.content.Intent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public interface CustomerAccountController {
    interface CustomerAccountControllerView {
        void showProgressDialog(String message);
        void hidProgressDialog();
    }

    void onStart();

    void displayAccountInfo(EditText etFullName, EditText etAddress, EditText etContact,
                            TextView tvEmail, TextView tvBalance, ImageView ivAccountProfilePic);

    void imageSaving(Intent data, Context context, ArrayList<byte[]> bytesArray, ImageView ivAccountProfilePic);

    void saveChanges(ArrayList<byte[]> bytesArray, EditText etFullName, EditText etAddress, EditText etContact);

    void topUpBalance(EditText etEnterBalance);

    void topUpStatus(String title);
}
