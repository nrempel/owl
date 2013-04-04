package com.magicpixellabs.front.login;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.magicpixellabs.owl.R;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

public class FragmentLogin extends Fragment implements View.OnClickListener, TextWatcher {

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
        ((EditText) v.findViewById(R.id.edit_email)).addTextChangedListener(this);
        ((EditText) v.findViewById(R.id.edit_password)).addTextChangedListener(this);
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
                String pass = ((EditText) getView().findViewById(R.id.edit_password)).getText().toString();
                String hashed = new String(Hex.encodeHex(DigestUtils.sha(pass)));
                mCallback.onLoginSelected(email, hashed);
                break;
        }
    }

    /* Ensure the user has typed something and that the email address is probably an email address  */

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        Button button = (Button) getView().findViewById(R.id.button_login);
        EditText email = (EditText) getView().findViewById(R.id.edit_email);
        EditText password = (EditText) getView().findViewById(R.id.edit_password);
        if (email.getText().length() > 0 && password.getText().length() > 0 &&
                email.getText().toString().contains(".") &&
                email.getText().toString().contains("@")) {
            button.setEnabled(true);
        } else {
            button.setEnabled(false);
        }

    }


}
