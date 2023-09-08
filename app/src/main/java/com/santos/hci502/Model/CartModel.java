package com.santos.hci502.Model;

public class CartModel {
    String productName, productDesc, productPrice, itemQuantity, productUrl;

    public CartModel() {
        //empty constructor
    }

    public CartModel(String productName, String productDesc, String productPrice, String itemQuantity, String productUrl) {
        this.productName = productName;
        this.productDesc = productDesc;
        this.productPrice = productPrice;
        this.itemQuantity = itemQuantity;
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

    public String getItemQuantity() {
        return itemQuantity;
    }

    public String getProductUrl() {
        return productUrl;
    }
}
