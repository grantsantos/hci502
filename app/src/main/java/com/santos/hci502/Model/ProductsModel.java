package com.santos.hci502.Model;

public class ProductsModel {
    String productName, productDesc, productPrice, productStock, productUrl;

    public ProductsModel() {
        //
    }

    public ProductsModel(String productName, String productDesc, String productPrice, String productStock, String productUrl) {
        this.productName = productName;
        this.productDesc = productDesc;
        this.productPrice = productPrice;
        this.productStock = productStock;
        this.productUrl = productUrl;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public String getProductStock() {
        return productStock;
    }

    public String getProductUrl() {
        return productUrl;
    }
}
