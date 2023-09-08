package com.santos.hci502.View.admin.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.santos.hci502.R;
import com.santos.hci502.Util.CircleTransform;
import com.santos.hci502.View.login_register.SignInActivity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.santos.hci502.Util.Constants.ACCEPTED;
import static com.santos.hci502.Util.Constants.ADMIN;
import static com.santos.hci502.Util.Constants.BALANCE;
import static com.santos.hci502.Util.Constants.REJECTED;
import static com.santos.hci502.Util.Constants.TOP_UP_REQUESTS;
import static com.santos.hci502.Util.Constants.TOP_UP_STATUS;
import static com.santos.hci502.Util.Constants.USERS;

public class TopUpIntentActivity extends AppCompatActivity {

    @BindView(R.id.ivIntentProfPic)
    ImageView ivIntentProfPic;
    @BindView(R.id.tvDate)
    TextView tvDate;
    @BindView(R.id.tvTime)
    TextView tvTime;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvAmount)
    TextView tvAmount;
    @BindView(R.id.bConfirm)
    Button bConfirm;
    @BindView(R.id.bReject)
    Button bReject;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference dataRef;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up_intent);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Top Up Information");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(this);
        initializeViews();
    }

    @OnClick({R.id.bConfirm, R.id.bReject})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bConfirm:
                showAlertPositiveConfirmation("Confirmation",
                        "Are you sure you want to CONFIRM?");
                break;
            case R.id.bReject:
                showAlertNegativeConfirmation("Confirmation",
                        "Are you sure you want to REJECT?");
                break;
        }
    }

    public void initializeViews() {
        String date = getIntent().getStringExtra("date");
        String time = getIntent().getStringExtra("time");
        String pic = getIntent().getStringExtra("pic");
        String name = getIntent().getStringExtra("name");
        String amount = getIntent().getStringExtra("amount");


        tvDate.setText(date);
        tvTime.setText(time);
        tvName.setText(name);
        tvAmount.setText(amount);
        tvDate.setText(date);

        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                ivIntentProfPic.setImageBitmap(bitmap);
                //progressBarProfPic.setVisibility(View.GONE);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                //progressBar.setVisibility(View.VISIBLE);
            }
        };
        ivIntentProfPic.setTag(target);
        Picasso.get().load(pic).transform(new CircleTransform()).into(target);

    }

    public void showProgressDialog(String message) {
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void showAlertDone(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(TopUpIntentActivity.this);
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

    public void showAlertPositiveConfirmation(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(TopUpIntentActivity.this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                confirmMethod();
            }
        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    public void showAlertNegativeConfirmation(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(TopUpIntentActivity.this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                rejectMethod();
            }
        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    public void confirmMethod() {
        String uid = getIntent().getStringExtra("uid");
        dataRef = firebaseDatabase.getReference(USERS).child(uid).child(BALANCE);
        DatabaseReference statusRef = firebaseDatabase.getReference(USERS).child(uid).child(TOP_UP_STATUS);
        showProgressDialog("Processing");
        dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int currentBalance = Integer.parseInt(dataSnapshot.getValue().toString());
                    currentBalance += Integer.parseInt(tvAmount.getText().toString());
                    dataRef.setValue(currentBalance);
                    dataRef = firebaseDatabase.getReference(ADMIN).child(firebaseAuth.getCurrentUser().getUid()).child(TOP_UP_REQUESTS)
                            .child(uid);
                    statusRef.setValue(ACCEPTED);
                    dataRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            showAlertDone("Success", tvAmount.getText().toString() +
                                    " has been added to " + tvName.getText().toString() + "'s" +
                                    " account.");
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void rejectMethod() {
        String uid = getIntent().getStringExtra("uid");
        DatabaseReference statusRef = firebaseDatabase.getReference(USERS).child(uid).child(TOP_UP_STATUS);
        dataRef = firebaseDatabase.getReference(ADMIN).child(firebaseAuth.getCurrentUser().getUid()).child(TOP_UP_REQUESTS)
                .child(uid);
        statusRef.setValue(REJECTED);
        dataRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                showAlertDone("Success", "Rejected Successfully");
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
