package com.magicpixellabs.front.login;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.magicpixellabs.simplesms.R;

public class FragmentLogin extends Fragment implements View.OnClickListener {

    /* Callback magic */
    Callback mCallback;

    interface Callback {
        void onForgotSelected();

        void onLoginSelected(String email, String pass);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (Callback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement Callback");
        }
    }
    /* END Callback magic */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.login_main, container, false);
        v.findViewById(R.id.text_forgot).setOnClickListener(this);
        v.findViewById(R.id.button_login).setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_forgot:
                mCallback.onForgotSelected();
                break;
            case R.id.button_login:
                String email = ((EditText) getView().findViewById(R.id.edit_email)).getText().toString();
                String pass = ((EditText) getView().findViewById(R.id.edit_password)).getText().toString(); //this bad boy needs hash
                mCallback.onLoginSelected(email, pass);
                break;
        }
    }
}
