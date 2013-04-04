package com.magicpixellabs.front.login;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import com.fasterxml.jackson.core.type.TypeReference;
import com.magicpixellabs.back.APICall;
import com.magicpixellabs.back.APIRequest;
import com.magicpixellabs.back.http.BaseHttpTask;
import com.magicpixellabs.beans.APIResponse;
import com.magicpixellabs.beans.Bean;
import com.magicpixellabs.beans.User;
import com.magicpixellabs.owl.R;

public class ActivityLogin extends Activity
        implements FragmentLogin.Callback, BaseHttpTask.Callback {

    private static final String TAG = "ActivityLogin";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.login_container);
        setProgressBarIndeterminateVisibility(false);
        getActionBar().setTitle(R.string.title_login);

        getFragmentManager().beginTransaction().
                replace(R.id.fragment_container, new FragmentLogin()).
                commit();
    }

    /* Callbacks */

    @Override
    public void onForgotSelected() {

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, new FragmentForgotPassword());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();

    }

    @Override
    public void onLoginSelected(String email, String pass) {

        APIRequest.EmailParameter emailParam = new APIRequest.EmailParameter(email);
        APIRequest.PasswordParameter password = new APIRequest.PasswordParameter(pass);
        try {
            APIRequest request = new APIRequest.Builder()
                    .resource(APIRequest.RESOURCE_USERS)
                    .action(APIRequest.ACTION_LOGIN)
                    .queryString(emailParam)
                    .queryString(password)
                    .build();
            APICall.get(this, request, new TypeReference<APIResponse<User>>() {
            }, null);

            setProgressBarIndeterminateVisibility(true);

        } catch (APIRequest.InvalidRequestException e) {
            Log.e(TAG, e.getMessage());
        }

    }

    @Override
    public void onHttpResult(APIResponse<Bean> response) {

        setProgressBarIndeterminateVisibility(false);

    }
}