package com.santos.hci502.Controller.customeraccount;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.santos.hci502.R;
import com.santos.hci502.Util.CircleTransform;
import com.santos.hci502.View.drawer.CustomerNavigationDrawer;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.NonNull;

import static com.santos.hci502.Util.Constants.*;

public class CustomerAccountControllerImpl implements CustomerAccountController {
    Context context;
    EditText etFullName, etAddress, etContact;
    ImageView ivAccountProfilePic;
    TextView tvEmail, tvBalance;

    ArrayList<byte[]> bytesArray;
    boolean isProfilePicEmpty = true;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference dataRef;

    CustomerAccountControllerView customerAccountControllerView;

    public CustomerAccountControllerImpl(CustomerAccountControllerView customerAccountControllerView, Context context, EditText etFullName, EditText etAddress, EditText etContact,
                                         TextView tvEmail, TextView tvBalance, ImageView ivAccountProfilePic, ArrayList<byte[]> bytesArray) {
        this.customerAccountControllerView = customerAccountControllerView;
        this.context = context;
        this.etFullName = etFullName;
        this.etAddress = etAddress;
        this.etContact = etContact;
        this.tvEmail = tvEmail;
        this.tvBalance = tvBalance;
        this.ivAccountProfilePic = ivAccountProfilePic;
        this.bytesArray = bytesArray;
    }

    @Override
    public void onStart() {
        displayAccountInfo(etFullName, etAddress, etContact, tvEmail, tvBalance, ivAccountProfilePic);
        //topUpStatus("Notice");
    }

    @Override
    public void displayAccountInfo(EditText etFullName, EditText etAddress, EditText etContact, TextView tvEmail, TextView tvBalance, ImageView ivAccountProfilePic) {
        String userUid = firebaseAuth.getCurrentUser().getUid();
        dataRef = firebaseDatabase.getReference("users/" + userUid);

        dataRef.child("profilePicUrl").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String url = dataSnapshot.getValue().toString();
                    Target target = new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            ivAccountProfilePic.setImageBitmap(bitmap);
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                        }
                    };
                    ivAccountProfilePic.setTag(target);
                    Picasso.get().load(url).transform(new CircleTransform()).into(target);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        dataRef.child("email").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    tvEmail.setText(dataSnapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        dataRef.child("balance").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    tvBalance.setText(dataSnapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        dataRef.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    etFullName.setText(dataSnapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        dataRef.child("address").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    etAddress.setText(dataSnapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        dataRef.child("contact").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    etContact.setText(dataSnapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void imageSaving(Intent data, Context context, ArrayList<byte[]> bytesArray, ImageView ivAccountProfilePic) {
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
            Picasso.get().load(mImageUri).transform(new CircleTransform()).into(ivAccountProfilePic);
            isProfilePicEmpty = false;
        } else {
            isProfilePicEmpty = true;
        }
    }

    @Override
    public void saveChanges(ArrayList<byte[]> bytesArray, EditText etFullName, EditText etAddress, EditText etContact) {
        String userUid = firebaseAuth.getCurrentUser().getUid();
        customerAccountControllerView.showProgressDialog("Saving changes...");

        if (!isProfilePicEmpty) {
            StorageReference storageRefPic = FirebaseStorage.getInstance().getReference("CustomerPofilePic/" + userUid);
            StorageReference fileReference = storageRefPic.child("profilepic.jpg");
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
                            dataRef = firebaseDatabase.getReference("users/" + userUid);
                            dataRef.child("profilePicUrl").setValue(imageUrl);
                            dataRef.child("name").setValue(etFullName.getText().toString());
                            dataRef.child("address").setValue(etAddress.getText().toString());
                            dataRef.child("contact").setValue(etContact.getText().toString());
                            customerAccountControllerView.hidProgressDialog();
                            context.startActivity(new Intent(context, CustomerNavigationDrawer.class));
                            ((Activity) context).finish();
                            ((Activity) context).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            dataRef = firebaseDatabase.getReference("users/" + userUid);
            dataRef.child("name").setValue(etFullName.getText().toString());
            dataRef.child("address").setValue(etAddress.getText().toString());
            dataRef.child("contact").setValue(etContact.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            customerAccountControllerView.hidProgressDialog();
                            context.startActivity(new Intent(context, CustomerNavigationDrawer.class));
                            ((Activity) context).finish();
                            ((Activity) context).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        }
                    });

        }

    }

    @Override
    public void topUpBalance(EditText etEnterBalance) {
        if (etEnterBalance.getText().toString().contains(".")) {
            Toast.makeText(context, "Values with decimal point is not allowed", Toast.LENGTH_SHORT).show();
        } else if (!etEnterBalance.getText().toString().isEmpty()) {
            customerAccountControllerView.showProgressDialog("Topping up...");
            String userUid = firebaseAuth.getCurrentUser().getUid();
            dataRef = firebaseDatabase.getReference(ADMIN).child(ADMIN_UID)
                    .child(TOP_UP_REQUESTS).child(userUid);
            DatabaseReference statusRef = firebaseDatabase.getReference(USERS).child(firebaseAuth.getCurrentUser().getUid())
                    .child(TOP_UP_STATUS);
            firebaseDatabase.getReference(USERS)
                    .child(userUid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String profileImage = dataSnapshot.child(PROFILE_PIC_URL).getValue().toString();
                            String name = dataSnapshot.child(NAME).getValue().toString();
                            long dateSentInMillis = Calendar.getInstance().getTimeInMillis();
                            dataRef.child(TOP_UP_VALUE).setValue(etEnterBalance.getText().toString());
                            dataRef.child(PROFILE_PIC_URL).setValue(profileImage);
                            dataRef.child(NAME).setValue(name);
                            dataRef.child(TIMESTAMP).setValue(dateSentInMillis);
                            dataRef.child(USER_UID).setValue(userUid);
                            //statusRef.setValue(PENDING);
                            customerAccountControllerView.hidProgressDialog();
                            AlertDialog.Builder builder = new AlertDialog.Builder(context,  R.style.AlertDialogCustom);
                            builder.setTitle("Success");
                            builder.setMessage("Wait for your top up to be approved");
                            builder.setCancelable(false);
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    firebaseDatabase.getReference(USERS)
                                            .child(firebaseAuth.getCurrentUser().getUid())
                                            .child(TOP_UP_STATUS).setValue(NULL);
                                }
                            });
                            builder.show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            firebaseDatabase.getReference(ADMIN).child(ADMIN_UID)
                    .child(TOP_UP_NOTIF).setValue(1);
        } else {
            Toast.makeText(context, "You did not enter a value", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void topUpStatus(String title) {
        firebaseDatabase.getReference(USERS)
                .child(firebaseAuth.getCurrentUser().getUid())
                .child(TOP_UP_STATUS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String message = "";
                            String status = dataSnapshot.getValue().toString();
                            if (status.equals(REJECTED)) {
                                message = "Your top up request has been rejected";
                                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogCustom);
                                builder.setTitle(title);
                                builder.setMessage(message);
                                builder.setCancelable(false);
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        firebaseDatabase.getReference(USERS)
                                                .child(firebaseAuth.getCurrentUser().getUid())
                                                .child(TOP_UP_STATUS).setValue(NULL);
                                    }
                                });
                                builder.show();
                            } else if (status.equals(ACCEPTED)) {
                                message = "Your top up request has been approved";
                                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogCustom);
                                builder.setTitle(title);
                                builder.setMessage(message);
                                builder.setCancelable(false);
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        firebaseDatabase.getReference(USERS)
                                                .child(firebaseAuth.getCurrentUser().getUid())
                                                .child(TOP_UP_STATUS).setValue(NULL);
                                    }
                                });
                                builder.show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }
}