package com.santos.hci502.Controller.addproduct;

import android.content.Context;
import android.content.Intent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public interface AddProductController {
    interface AddProductControllerView{
        void imageChooser();
        void showProgressDialog(String message);
        void hideProgressDialog();
        void toastMessage(String message);
        void showAlertProductAdded(String title, String message);
    }

    boolean requestProduct(EditText productName, EditText productDesc, EditText productPrice, EditText productStock);
    void addProduct(String productName, String productDesc, String productPrice, String productStock,
                             ArrayList<byte[]> bytesArray);
    void imageSavingCompression(Intent data, Context context, ArrayList<byte[]> bytesArray,
                                ImageView ivProductImage);
}
