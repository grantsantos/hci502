package com.santos.hci502.View.admin.activity;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.santos.hci502.R;
import com.santos.hci502.Util.Constants;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.santos.hci502.Util.Constants.PRODUCTS;
import static com.santos.hci502.Util.Constants.PRODUCT_DESC;
import static com.santos.hci502.Util.Constants.PRODUCT_NAME;
import static com.santos.hci502.Util.Constants.PRODUCT_PRICE;
import static com.santos.hci502.Util.Constants.PRODUCT_STOCK;
import static com.santos.hci502.Util.Constants.PRODUCT_URL;

public class EditProductIntent extends AppCompatActivity {

    @BindView(R.id.ivProdImage)
    ImageView ivProdImage;
    @BindView(R.id.etProductName)
    EditText etProductName;
    @BindView(R.id.etDescription)
    EditText etDescription;
    @BindView(R.id.etPrice)
    EditText etPrice;
    @BindView(R.id.etStock)
    EditText etStock;

    boolean isProfilePicEmpty = false;
    ArrayList<byte[]> bytesArray;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference dataRef;
    FirebaseAuth firebaseAuth;

    ProgressDialog progressDialog;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.delete_product_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product_intent);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Product Information");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(this);
        bytesArray = new ArrayList<>();
        initViews();
    }

    public void initViews() {
        String image, name, desc, price, stock;

        image = getIntent().getStringExtra(PRODUCT_URL);
        name = getIntent().getStringExtra(PRODUCT_NAME);
        desc = getIntent().getStringExtra(PRODUCT_DESC);
        price = getIntent().getStringExtra(PRODUCT_PRICE);
        stock = getIntent().getStringExtra(PRODUCT_STOCK);

        Glide.with(getApplicationContext())
                .load(image)
                .into(ivProdImage);

        etProductName.setText(name);
        etDescription.setText(desc);
        etPrice.setText(price);
        etStock.setText(stock);
    }

    public void saveChanges() {
        showProgressDialog("Saving Changes");
        String image = getIntent().getStringExtra(PRODUCT_URL);
        Query query = FirebaseDatabase.getInstance().getReference()
                .child(PRODUCTS)
                .orderByChild(PRODUCT_URL)
                .equalTo(image);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String getKey = "";
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        getKey = ds.getKey();
                        dataRef = firebaseDatabase.getReference(PRODUCTS)
                                .child(getKey);
                    }
                    if(!isProfilePicEmpty) {
                        dataRef.child(PRODUCT_NAME).setValue(etProductName.getText().toString());
                        dataRef.child(PRODUCT_DESC).setValue(etDescription.getText().toString());
                        dataRef.child(PRODUCT_PRICE).setValue(etPrice.getText().toString());
                        dataRef.child(PRODUCT_STOCK).setValue(etStock.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                hideProgressDialog();
                                finish();
                            }
                        });
                    }else{
                        String finalGetKey = getKey;
                        dataRef.child(PRODUCT_NAME).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    String productName = dataSnapshot.getValue().toString();
                                    StorageReference storageRefPic = FirebaseStorage.getInstance().getReference("Product/");
                                    StorageReference fileReference = storageRefPic.child(finalGetKey);
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
                                                    dataRef.child(PRODUCT_URL).setValue(imageUrl);
                                                    dataRef.child(PRODUCT_NAME).setValue(etProductName.getText().toString());
                                                    dataRef.child(PRODUCT_DESC).setValue(etDescription.getText().toString());
                                                    dataRef.child(PRODUCT_PRICE).setValue(etPrice.getText().toString());
                                                    dataRef.child(PRODUCT_STOCK).setValue(etStock.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            hideProgressDialog();
                                                            finish();
                                                        }
                                                    });
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    toastMessage(e.getMessage());
                                                }
                                            });

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            hideProgressDialog();
                                            toastMessage(e.getMessage());
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                hideProgressDialog();
                toastMessage(databaseError.getMessage());
            }
        });

    }

    public void deleteProduct(){
        showProgressDialog("Deleting Product. Please Wait");
        String imageUrl = getIntent().getStringExtra(PRODUCT_URL);
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        Query query = FirebaseDatabase.getInstance().getReference()
                .child(PRODUCTS)
                .orderByChild(PRODUCT_URL)
                .equalTo(imageUrl);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String key = "";
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        key = ds.getKey();
                        dataRef = firebaseDatabase.getReference(PRODUCTS)
                                .child(key);
                    }
                    StorageReference deleteReference = firebaseStorage.getReferenceFromUrl(imageUrl);
                    deleteReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            dataRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    hideProgressDialog();
                                    showAlertSuccess("Success", "Deleted successfully");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    toastMessage(e.getMessage());
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            toastMessage(e.getMessage());
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void imageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    public void showAlertConfirmation(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteProduct();
            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();

    }

    public void showAlertSuccess(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data.getData() != null) {
                Uri mImageUri = data.getData();

                InputStream imageStream = null;
                try {
                    imageStream = getContentResolver().openInputStream(
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

                Glide.with(getApplicationContext())
                        .load(mImageUri)
                        .into(ivProdImage);
                isProfilePicEmpty = true;

            } else {
                isProfilePicEmpty = false;
                toastMessage("Picture error. Please select again");
            }
        }else{
            toastMessage("You did not select a picture");
        }
    }

    public void showProgressDialog(String message) {
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void hideProgressDialog() {
        progressDialog.dismiss();
    }

    public void toastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.deleteProduct:
                showAlertConfirmation("Confirmation", "Are you sure you want to delete this product?");
                break;
        }
        return true;
    }


    @OnClick({R.id.fab, R.id.btnSaveChanges})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fab:
                imageChooser();
                break;
            case R.id.btnSaveChanges:
                if(Constants.isNetworkAvailable(getApplicationContext())) {
                    String name, desc, price, stock;

                    name = getIntent().getStringExtra(PRODUCT_NAME);
                    desc = getIntent().getStringExtra(PRODUCT_DESC);
                    price = getIntent().getStringExtra(PRODUCT_PRICE);
                    stock = getIntent().getStringExtra(PRODUCT_STOCK);
                    if (!etProductName.getText().toString().equals(name) || !etDescription.getText().toString().equals(desc) ||
                            !etPrice.getText().toString().equals(price) || !etStock.getText().toString().equals(stock) || isProfilePicEmpty) {
                        saveChanges();
                    } else {
                        finish();
                    }
                }else{
                    toastMessage(getApplicationContext().getResources().getString(R.string.NO_INTERNET));
                }
                break;
        }
    }
}
