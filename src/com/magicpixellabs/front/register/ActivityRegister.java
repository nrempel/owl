package com.magicpixellabs.front.register;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import com.fasterxml.jackson.core.type.TypeReference;
import com.magicpixellabs.Dialogs;
import com.magicpixellabs.back.APICall;
import com.magicpixellabs.back.APIRequest;
import com.magicpixellabs.back.http.BaseHttpTask;
import com.magicpixellabs.models.beans.APIResponse;
import com.magicpixellabs.models.beans.Bean;
import com.magicpixellabs.models.beans.User;
import com.magicpixellabs.owl.R;

public class ActivityRegister extends Activity implements FragmentRegister.Callback,
        FragmentPickNumber.Callback, BaseHttpTask.Callback {

    private static final String TAG = "ActivityRegister";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_container);
        getActionBar().setTitle(R.string.title_register);

        Account[] accounts = AccountManager.get(this).getAccountsByType("com.google");
        String email = "";
        if (accounts.length > 0) {
            email = accounts[0].name;
        }

        getFragmentManager().beginTransaction().
                replace(R.id.fragment_container, new FragmentRegister(email)).
                commit();
    }

    @Override
    public void onNextSelected(String first, String last, String email, String hashed) {
        User user = new User();

        User.Name name = new User.Name();
        name.setFirst(first);
        name.setLast(last);

        user.setName(name);
        user.setEmail(email);
        user.setPassword(hashed);

        try {
            APIRequest request = new APIRequest.Builder().resource(APIRequest.RESOURCE_USERS).build();
            APICall.post(this, request, new TypeReference<APIResponse<User>>(){}, user);
            setProgressBarIndeterminateVisibility(true);
        } catch (APIRequest.InvalidRequestException e) {
            Log.e(TAG, e.getMessage());
        }
   }

    @Override
    public void onHttpResult(APIResponse<Bean> response) {

        setProgressBarIndeterminateVisibility(false);
        if (response.isSuccess()) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, new FragmentPickNumber());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.commit();
        } else {
            Dialogs.showErrorDialog(this, response.getError()).show();
        }
    }
}