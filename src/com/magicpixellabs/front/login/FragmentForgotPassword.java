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

public class FragmentForgotPassword extends Fragment implements View.OnClickListener, TextWatcher {

    /* Callback magic */
    Callback mCallback;

    interface Callback {
        void onRecoverSelected();
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

    private String mEmail;

    public FragmentForgotPassword() {
    }

    public FragmentForgotPassword(String email) {
        mEmail = email;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.login_recover, container, false);
        EditText email = (EditText) v.findViewById(R.id.edit_email);
        email.addTextChangedListener(this);
        v.findViewById(R.id.button_recover).setOnClickListener(this);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EditText email = (EditText) view.findViewById(R.id.edit_email);
        email.setText(mEmail);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_recover:
                mCallback.onRecoverSelected();
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

        Button button = (Button) getView().findViewById(R.id.button_recover);
        EditText email = (EditText) getView().findViewById(R.id.edit_email);
        if (email.getText().length() > 0 &&
                email.getText().toString().contains(".") &&
                email.getText().toString().contains("@")) {
            button.setEnabled(true);
        } else {
            button.setEnabled(false);
        }
    }
}
