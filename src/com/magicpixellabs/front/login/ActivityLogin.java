package com.magicpixellabs.front.login;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import com.magicpixellabs.owl.R;

public class ActivityLogin extends Activity implements FragmentLogin.Callback {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_container);
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

        //authenticate user and bla bla

    }

}