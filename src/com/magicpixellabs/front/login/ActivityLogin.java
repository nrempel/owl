package com.magicpixellabs.front.login;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import com.fasterxml.jackson.core.type.TypeReference;
import com.magicpixellabs.Dialogs;
import com.magicpixellabs.back.APICall;
import com.magicpixellabs.back.APIRequest;
import com.magicpixellabs.back.http.BaseHttpTask;
import com.magicpixellabs.front.main.ActivityMain;
import com.magicpixellabs.models.beans.APIResponse;
import com.magicpixellabs.models.beans.Bean;
import com.magicpixellabs.models.beans.User;
import com.magicpixellabs.owl.R;

public class ActivityLogin extends Activity
        implements FragmentLogin.Callback, FragmentForgotPassword.Callback,
        BaseHttpTask.Callback {

    private static final String TAG = "ActivityLogin";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.login_container);
        setProgressBarIndeterminateVisibility(false);
        getActionBar().setTitle(R.string.title_login);

        Account[] accounts = AccountManager.get(this).getAccountsByType("com.google");
        String email = "";
        if (accounts.length > 0) {
            email = accounts[0].name;
        }

        getFragmentManager().beginTransaction().
                replace(R.id.fragment_container, new FragmentLogin(email)).
                commit();
    }

    /* Callbacks */

    @Override
    public void onForgotSelected(String email) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, new FragmentForgotPassword(email));
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();
    }


    @Override
    public void onRecoverSelected() {

    }

    @Override
    public void onLoginSelected(String email, String hashed) {
        APIRequest.EmailParameter emailParam = new APIRequest.EmailParameter(email);
        APIRequest.PasswordParameter password = new APIRequest.PasswordParameter(hashed);
        try {
            APIRequest request = new APIRequest.Builder()
                    .resource(APIRequest.RESOURCE_USERS)
                    .action(APIRequest.ACTION_LOGIN)
                    .queryString(emailParam)
                    .queryString(password)
                    .build();
            APICall.get(this, request, new TypeReference<APIResponse<User>>() {});
            setProgressBarIndeterminateVisibility(true);
        } catch (APIRequest.InvalidRequestException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onHttpResult(APIResponse<Bean> response) {
        setProgressBarIndeterminateVisibility(false);
        if (response.isSuccess()) {
            startActivity(new Intent(this, ActivityMain.class));
        } else {
            Dialogs.showErrorDialog(this, response.getError()).show();
        }
    }
}