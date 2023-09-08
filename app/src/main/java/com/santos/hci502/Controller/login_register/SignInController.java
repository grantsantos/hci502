package com.santos.hci502.Controller.login_register;

import com.google.android.material.textfield.TextInputLayout;

public interface SignInController {
    interface SignInControllerView{
        void emailEmptyErrorMessage();
        void passwordEmptyErrorMessage();
        void toastMessage(String message);
        void showProgressDialog(String message);
        void hideProgressDialog();
    }

    void onStart();
    void signInAccount(String email, String password);
    void getCurrentSession();
    void checkInternetConnection(String email, String password);
    void requestLogin(String email, String password);
}
