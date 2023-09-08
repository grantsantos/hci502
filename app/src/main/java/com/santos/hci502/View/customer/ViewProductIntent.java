package com.santos.hci502.View.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.santos.hci502.R;
import com.santos.hci502.View.customer.fragment.QuantityDialogFragment;
import com.santos.hci502.View.drawer.CustomerNavigationDrawer;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.santos.hci502.Util.Constants.PRODUCT_DESC;
import static com.santos.hci502.Util.Constants.PRODUCT_NAME;
import static com.santos.hci502.Util.Constants.PRODUCT_PRICE;
import static com.santos.hci502.Util.Constants.PRODUCT_STOCK;
import static com.santos.hci502.Util.Constants.PRODUCT_URL;

public class ViewProductIntent extends AppCompatActivity {

    @BindView(R.id.ivProdImage)
    ImageView ivProdImage;
    @BindView(R.id.tvProdName)
    TextView tvProdName;
    @BindView(R.id.tvProdDesc)
    TextView tvProdDesc;
    @BindView(R.id.tvProdPrice)
    TextView tvProdPrice;
    @BindView(R.id.tvProdStock)
    TextView tvProdStock;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product_intent);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

        tvProdName.setText(name);
        tvProdDesc.setText(desc);
        tvProdPrice.setText(price);
        tvProdStock.setText(stock);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cart_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.cartMenu:
                Intent intent = new Intent(ViewProductIntent.this, CustomerNavigationDrawer.class);
                intent.putExtra("PRODUCT_INTENT", "TRUE");
                startActivity(intent);
                finish();
                break;
        }
        return true;
    }

    @OnClick(R.id.btnAddToCart)
    public void onViewClicked() {
        if (tvProdStock.getText().toString().equals("0")){
            Toast.makeText(this, "This item is out of stock", Toast.LENGTH_SHORT).show();
        }else{
            String image = getIntent().getStringExtra(PRODUCT_URL);
            String name = getIntent().getStringExtra(PRODUCT_NAME);
            String desc = getIntent().getStringExtra(PRODUCT_DESC);
            String price = getIntent().getStringExtra(PRODUCT_PRICE);
            Bundle bundle = new Bundle();
            bundle.putString("stock", tvProdStock.getText().toString());
            bundle.putString("imageUrl", image);
            bundle.putString("name", name);
            bundle.putString("desc", desc);
            bundle.putString("price", price);
            QuantityDialogFragment dialog = new QuantityDialogFragment();
            dialog.setArguments(bundle);
            dialog.setCancelable(false);
            dialog.show(getSupportFragmentManager(), "Dialog");
        }
    }
}
