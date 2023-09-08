package com.santos.hci502.View.customer;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.santos.hci502.Controller.customeraccount.CustomerAccountController;
import com.santos.hci502.Controller.customeraccount.CustomerAccountControllerImpl;
import com.santos.hci502.R;
import com.santos.hci502.View.drawer.CustomerNavigationDrawer;

import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AccountInfoActivity extends AppCompatActivity implements CustomerAccountController.CustomerAccountControllerView {

    @BindView(R.id.ivAccountProfilePic)
    ImageView ivAccountProfilePic;
    @BindView(R.id.tvEmail)
    TextView tvEmail;
    @BindView(R.id.tvBalance)
    TextView tvBalance;
    @BindView(R.id.etFullName)
    EditText etFullName;
    @BindView(R.id.etAddress)
    EditText etAddress;
    @BindView(R.id.etContactNumber)
    EditText etContactNumber;

    ArrayList<byte[]> bytesArray = new ArrayList<>();
    ProgressDialog progressDialog;

    CustomerAccountControllerImpl customerAccountController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Account Settings");
        progressDialog = new ProgressDialog(this);
        customerAccountController = new CustomerAccountControllerImpl(this, this, etFullName, etAddress, etContactNumber,
                tvEmail, tvBalance, ivAccountProfilePic, bytesArray);


        customerAccountController.onStart();
    }

    public void imageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            customerAccountController.imageSaving(data, this, bytesArray, ivAccountProfilePic);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), CustomerNavigationDrawer.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(getApplicationContext(), CustomerNavigationDrawer.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;
            case R.id.saveChanges:
                customerAccountController.saveChanges(bytesArray, etFullName, etAddress, etContactNumber);
                break;
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_changes_menu, menu);
        return true;
    }


    @Override
    public void showProgressDialog(String message) {
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    public void hidProgressDialog() {
        progressDialog.dismiss();
    }

    @OnClick({R.id.fabSelectImage, R.id.constraintBalance})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fabSelectImage:
                imageChooser();
                break;
            case R.id.constraintBalance:
                showAlertTopUp("Top up your balance", "Enter amount");
                break;
        }
    }

    public void showAlertTopUp(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AccountInfoActivity.this, R.style.AlertDialogCustom);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        builder.setView(editText);
        builder.setPositiveButton("ENTER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                customerAccountController.topUpBalance(editText);
            }
        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }



}
