package com.santos.hci502.Model;

public class PurchaseHistoryModel {
    String purchaseTotal;
    long timeStamp;

    public PurchaseHistoryModel() {
        //empty constructor
    }

    public PurchaseHistoryModel(String purchaseTotal, long timeStamp) {
        this.purchaseTotal = purchaseTotal;
        this.timeStamp = timeStamp;
    }

    public String getPurchaseTotal() {
        return purchaseTotal;
    }

    public long getTimeStamp() {
        return timeStamp;
    }
}
