package com.santos.hci502.Util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Constants {
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", /*Locale.getDefault()*/Locale.ENGLISH);
    public static final SimpleDateFormat dateFormatOnly = new SimpleDateFormat("MMMM d", /*Locale.getDefault()*/Locale.ENGLISH);
    public static final SimpleDateFormat yearOnly = new SimpleDateFormat("yyyy", Locale.ENGLISH);
    public static final SimpleDateFormat timeFormat12hr = new SimpleDateFormat("h:mm aa", Locale.ENGLISH);
    public static final SimpleDateFormat hourMinuteSecondFormat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
    public static final SimpleDateFormat combinedFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.ENGLISH);
    public static final SimpleDateFormat fixedFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.ENGLISH); //'00'.'000'
    public static final SimpleDateFormat fixedTimeFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);


    public static final SimpleDateFormat hourFormat = new SimpleDateFormat("HH", Locale.ENGLISH);
    public static final SimpleDateFormat minuteFormat = new SimpleDateFormat("mm", Locale.ENGLISH);

    public static final SimpleDateFormat yearDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    public static final SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //Database Reference Values
    public static final String ADMIN  = "admin";
    public static final String ADMIN_UID  = "jSl1kZfcZ9M9SN6Mb1BbXiGcmdp1";
    public static final String ADMIN_EMAIL = "admin@gmail.com";
    public static final String USERS  = "users";
    public static final String USER_UID  = "userUid";
    public static final String PRODUCTS  = "products";
    public static final String PRODUCT_URL  = "productUrl";
    public static final String PRODUCT_NAME  = "productName";
    public static final String PRODUCT_DESC  = "productDesc";
    public static final String PRODUCT_PRICE  = "productPrice";
    public static final String PRODUCT_STOCK  = "productStock";
    public static final String NAME  = "name";
    public static final String ADDRESS  = "address";
    public static final String CONTACT  = "contact";
    public static final String EMAIL  = "email";
    public static final String PASSWORD  = "password";
    public static final String PROFILE_PIC_URL  = "profilePicUrl";
    public static final String TOP_UP_STATUS  = "topUpStatus";
    public static final String TOP_UP_REQUESTS  = "topUpRequests";
    public static final String TOP_UP_VALUE  = "topUpValue";
    public static final String TOP_UP_NOTIF  = "topUpNotif";
    public static final String TIMESTAMP  = "timeStamp";
    public static final String BALANCE  = "balance";
    public static final String CART  = "cart";
    public static final String ITEM_QUANTITY  = "itemQuantity";

    public static final String PURCHASE_LOG  = "purchaseLog";
    public static final String PURCHASE_TOTAL = "purchaseTotal";
    public static final String BALANCE_LEFT = "balanceLeft";


    //Normal Constant Strings
    public static final String PENDING = "pending";
    public static final String REJECTED = "rejected";
    public static final String ACCEPTED = "accepted";
    public static final String NULL = "NULL";





}
