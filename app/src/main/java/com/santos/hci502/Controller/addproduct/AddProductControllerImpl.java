package com.santos.hci502.Controller.addproduct;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import androidx.annotation.NonNull;

import static com.santos.hci502.Util.Constants.*;



public class AddProductControllerImpl implements AddProductController {
    Context context;
    AddProductController.AddProductControllerView presenter;
    ImageView ivProductImage;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference dataRef = firebaseDatabase.getReference(PRODUCTS);
    boolean isProfilePicEmpty = false;

    public AddProductControllerImpl(Context context, AddProductControllerView presenter, ImageView ivProductImage) {
        this.context = context;
        this.presenter = presenter;
        this.ivProductImage = ivProductImage;
    }

    @Override
    public boolean requestProduct(EditText productName, EditText productDesc, EditText productPrice, EditText productStock) {
        boolean formComplete = true;
        if (productName.getText().toString().isEmpty()) {
            productName.setError("Required");
            formComplete = false;
        }

        if (productDesc.getText().toString().isEmpty()) {
            productDesc.setError("Required");
            formComplete = false;
        }

        if (productPrice.getText().toString().isEmpty()) {
            productPrice.setError("Required");
            formComplete = false;
        }

        if (productStock.getText().toString().equals("0") || productStock.getText().toString().isEmpty()) {
            productStock.setError("Required");
            formComplete = false;
        }

        if (!isProfilePicEmpty) {
            presenter.toastMessage("Select the image of the product");
            formComplete = false;
        }

        return formComplete;
    }

    @Override
    public void addProduct(String productName, String productDesc, String productPrice, String productStock, ArrayList<byte[]> bytesArray) {
        presenter.showProgressDialog("Adding Product...");
        String key = firebaseDatabase.getReference("products").push().getKey();

        StorageReference storageRefPic = FirebaseStorage.getInstance().getReference("Product/");
        StorageReference fileReference = storageRefPic.child(key);
        UploadTask uploadTask = null;
        uploadTask = fileReference.putBytes(bytesArray.get(0));
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();
                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageUrl = uri.toString();
                        dataRef.child(key).child(PRODUCT_URL).setValue(imageUrl);
                        dataRef.child(key).child(PRODUCT_NAME).setValue(productName);
                        dataRef.child(key).child(PRODUCT_DESC).setValue(productDesc);
                        dataRef.child(key).child(PRODUCT_PRICE).setValue(productPrice);
                        dataRef.child(key).child(PRODUCT_STOCK).setValue(productStock);
                        presenter.hideProgressDialog();
                        presenter.showAlertProductAdded("Success", productName  +  " has been added");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        presenter.hideProgressDialog();
                        presenter.toastMessage(e.getMessage());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                presenter.hideProgressDialog();
                presenter.toastMessage(e.getMessage());
            }
        });

    }

    @Override
    public void imageSavingCompression(Intent data, Context context, ArrayList<byte[]> bytesArray, ImageView ivProductImage) {
        if (data.getData() != null) {
            Uri mImageUri = data.getData();

            InputStream imageStream = null;
            try {
                imageStream = context.getContentResolver().openInputStream(
                        mImageUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            Bitmap bmp = BitmapFactory.decodeStream(imageStream);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            if (bytesArray.size() != 0) {
                for (int i = 0; i < bytesArray.size(); i++) {
                    bytesArray.remove(i);
                }
            }
            bytesArray.add(stream.toByteArray());
            try {
                stream.close();
                stream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }

            Picasso.get().load(mImageUri).fit().into(ivProductImage);
            isProfilePicEmpty = true;

        } else {
            isProfilePicEmpty = false;
        }
    }
}
