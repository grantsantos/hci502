package com.santos.hci502.Controller.login_register;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.santos.hci502.View.drawer.CustomerNavigationDrawer;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import androidx.annotation.NonNull;

import static com.santos.hci502.Util.Constants.ADDRESS;
import static com.santos.hci502.Util.Constants.BALANCE;
import static com.santos.hci502.Util.Constants.CONTACT;
import static com.santos.hci502.Util.Constants.EMAIL;
import static com.santos.hci502.Util.Constants.NAME;
import static com.santos.hci502.Util.Constants.PASSWORD;
import static com.santos.hci502.Util.Constants.PROFILE_PIC_URL;
import static com.santos.hci502.Util.Constants.USERS;
import static com.santos.hci502.Util.Constants.USER_UID;

public class RegisterControllerImpl implements RegisterController {
    RegisterController.RegsiterControllerView regsiterControllerView;
    Context context;
    TextView textViewPhoto;
    ImageView imageViewRegister;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference dataRef = firebaseDatabase.getReference(USERS);
    boolean isProfilePicEmpty = false;

    public RegisterControllerImpl(RegsiterControllerView regsiterControllerView, Context context, TextView textViewPhoto, ImageView imageViewRegister) {
        this.regsiterControllerView = regsiterControllerView;
        this.context = context;
        this.textViewPhoto = textViewPhoto;
        this.imageViewRegister = imageViewRegister;
    }

    @Override
    public boolean requestRegister(TextInputLayout name, TextInputLayout address, TextInputLayout contact, TextInputLayout email, TextInputLayout password) {
        boolean formComplete = true;
        if (name.getEditText().getText().toString().isEmpty()) {
            name.setError("Required");
            formComplete = false;
        }

        if (address.getEditText().getText().toString().isEmpty()) {
            address.setError("Required");
            formComplete = false;
        }

        if (contact.getEditText().getText().toString().isEmpty()) {
            contact.setError("Required");
            formComplete = false;
        }

        if (email.getEditText().getText().toString().isEmpty()) {
            email.setError("Required");
            formComplete = false;
        }

        if(password.getEditText().getText().toString().isEmpty()){
            password.setError("Required");
            formComplete = false;
        }

        if (!isProfilePicEmpty) {
            regsiterControllerView.toastMessage("Select your picture");
            formComplete = false;
        }

        return formComplete;
    }

    @Override
    public void registerCredentials(String name, String address, String contact, String email, String password,
                                    ArrayList<byte[]> bytesArray) {
        firebaseAuth = FirebaseAuth.getInstance();
        regsiterControllerView.showProgressDialog("Registration on Progress. Please Wait..");
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            final String getUid = task.getResult().getUser().getUid();
                            StorageReference storageRefPic = FirebaseStorage.getInstance().getReference("CustomerPofilePic/" + task.getResult().getUser().getUid());
                            StorageReference fileReference = storageRefPic.child("profilepic.jpg");
                            UploadTask uploadTask = null;
                            uploadTask = fileReference.putBytes(bytesArray.get(0));
                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String imageUrl = uri.toString();
                                            dataRef.child(getUid).child(USER_UID).setValue(getUid);
                                            dataRef.child(getUid).child(PROFILE_PIC_URL).setValue(imageUrl);
                                            dataRef.child(getUid).child(BALANCE).setValue(0);
                                            dataRef.child(getUid).child(NAME).setValue(name);
                                            dataRef.child(getUid).child(ADDRESS).setValue(address);
                                            dataRef.child(getUid).child(CONTACT).setValue(contact);
                                            dataRef.child(getUid).child(EMAIL).setValue(email);
                                            dataRef.child(getUid).child(PASSWORD).setValue(password)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            regsiterControllerView.hideProgressDialog();
                                                            context.startActivity(new Intent(context, CustomerNavigationDrawer.class));
                                                            ((Activity) context).finish();
                                                        }
                                                    });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            regsiterControllerView.hideProgressDialog();
                                            regsiterControllerView.toastMessage(e.getMessage());
                                        }
                                    });

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    regsiterControllerView.hideProgressDialog();
                                    regsiterControllerView.toastMessage("Image Problem: " + e.getMessage());
                                }
                            });
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                regsiterControllerView.hideProgressDialog();
                regsiterControllerView.toastMessage(e.getMessage());
            }
        });


    }

    @Override
    public void imageSavingCompression(Intent data, Context context, ArrayList<byte[]> bytesArray, TextView textViewPhoto, ImageView imageViewRegister) {
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
            textViewPhoto.setVisibility(View.GONE);
            Picasso.get().load(mImageUri).fit().into(imageViewRegister);
            isProfilePicEmpty = true;

        } else {
            isProfilePicEmpty = false;
        }
    }
}
