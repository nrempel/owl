package com.magicpixellabs.front.register;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import com.magicpixellabs.back.http.BaseHttpTask;
import com.magicpixellabs.beans.APIResponse;
import com.magicpixellabs.beans.Bean;
import com.magicpixellabs.owl.R;

public class ActivityRegister extends Activity implements FragmentRegister.Callback,
        BaseHttpTask.Callback {

    private static final String TAG = "ActivityRegister";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_container);
        getActionBar().setTitle(R.string.title_register);

        getFragmentManager().beginTransaction().
                replace(R.id.fragment_container, new FragmentRegister()).
                commit();
    }

    @Override
    public void onNextSelected(String first, String last, String email, String hashed) {
//        APIRequest.EmailParameter emailParam = new APIRequest.EmailParameter(email);
//        APIRequest.PasswordParameter password = new APIRequest.PasswordParameter(hashed);
//        try {
//            APIRequest request = new APIRequest.Builder()
//                    .resource(APIRequest.RESOURCE_USERS)
//                    .action(APIRequest.ACTION_LOGIN)
//                    .queryString(emailParam)
//                    .queryString(password)
//                    .build();
//            APICall.get(this, request, new TypeReference<APIResponse<User>>() {
//            }, null);
//
//            setProgressBarIndeterminateVisibility(true);
//
//        } catch (APIRequest.InvalidRequestException e) {
//            Log.e(TAG, e.getMessage());
//        }
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
            //handle error
        }
    }
}