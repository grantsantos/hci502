package com.santos.hci502.View.admin.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.santos.hci502.Controller.addproduct.AddProductController;
import com.santos.hci502.Controller.addproduct.AddProductControllerImpl;
import com.santos.hci502.R;
import com.santos.hci502.Util.Constants;
import com.santos.hci502.View.drawer.AdminNavigationDrawer;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddProductActivity extends AppCompatActivity implements AddProductController.AddProductControllerView {

    @BindView(R.id.ivSelectProductImage)
    ImageView ivSelectProductImage;
    @BindView(R.id.etProductName)
    EditText etProductName;
    @BindView(R.id.etPrice)
    EditText etPrice;
    @BindView(R.id.etStock)
    EditText etStock;
    @BindView(R.id.etDescription)
    EditText etDescription;

    ProgressDialog progressDialog;
    ArrayList<byte[]> bytesArray;

    AddProductController presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add New Product");
        progressDialog = new ProgressDialog(this);
        bytesArray = new ArrayList<>();
        presenter = new AddProductControllerImpl(this, this, ivSelectProductImage);

    }

    @OnClick({R.id.btnPlus, R.id.btnMinus, R.id.btnAddProduct, R.id.ivSelectProductImage})
    public void onViewClicked(View view) {
        int stock = 0;
        if (etStock.getText().toString().isEmpty()) {
             stock = 0;
        } else {
             stock = Integer.parseInt(etStock.getText().toString());
        }
        switch (view.getId()) {
            case R.id.ivSelectProductImage:
                imageChooser();
                break;
            case R.id.btnPlus:
                    stock += 1;
                    etStock.setText(String.valueOf(stock));
                break;
            case R.id.btnMinus:
                if (stock != 0) {
                    stock -= 1;
                    etStock.setText(String.valueOf(stock));
                }else{
                    etStock.setText(String.valueOf(stock));
                }


                break;
            case R.id.btnAddProduct:
                if (Constants.isNetworkAvailable(this)) {
                    if (!presenter.requestProduct(etProductName, etDescription, etPrice, etStock)) {
                        return;
                    } else {
                        String productName = etProductName.getText().toString();
                        String productDesc = etDescription.getText().toString();
                        String productPrice = etPrice.getText().toString();
                        String productStock = etStock.getText().toString();
                        presenter.addProduct(productName, productDesc, productPrice, productStock, bytesArray);
                    }
                } else {
                    toastMessage("Connect to the internet");
                }
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (data != null) {
            presenter.imageSavingCompression(data, this, bytesArray, ivSelectProductImage);
        } else {
            toastMessage("No Image Selected");
        }
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

    @Override
    public void showAlertProductAdded(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                startActivity(new Intent(AddProductActivity.this, AddProductActivity.class));
            }
        });
        builder.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                startActivity(new Intent(AddProductActivity.this, AdminNavigationDrawer.class));
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(AddProductActivity.this, AdminNavigationDrawer.class));

    }
}
