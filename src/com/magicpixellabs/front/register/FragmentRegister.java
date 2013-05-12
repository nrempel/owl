package com.magicpixellabs.front.register;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.magicpixellabs.owl.R;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

public class FragmentRegister extends Fragment implements View.OnClickListener, TextWatcher {

    /* Callback magic */
    Callback mCallback;

    interface Callback {
        void onNextSelected(String first, String last, String email, String hashed);
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
        View v = inflater.inflate(R.layout.register_main, container, false);
        ((TextView) v.findViewById(R.id.text_terms)).setMovementMethod(new LinkMovementMethod());
        ((EditText) v.findViewById(R.id.edit_first_name)).addTextChangedListener(this);
        ((EditText) v.findViewById(R.id.edit_last_name)).addTextChangedListener(this);
        ((EditText) v.findViewById(R.id.edit_email)).addTextChangedListener(this);
        ((EditText) v.findViewById(R.id.edit_password)).addTextChangedListener(this);
        v.findViewById(R.id.button_next).setOnClickListener(this);
        return v;
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

        Button button = (Button) getView().findViewById(R.id.button_next);
        EditText email = (EditText) getView().findViewById(R.id.edit_email);
        EditText password = (EditText) getView().findViewById(R.id.edit_password);
        EditText first = (EditText) getView().findViewById(R.id.edit_first_name);
        EditText last = (EditText) getView().findViewById(R.id.edit_last_name);
        if (email.getText().length() > 0 && password.getText().length() > 0 &&
                first.getText().length() > 0 && last.getText().length() > 0 &&
                email.getText().toString().contains(".") &&
                email.getText().toString().contains("@")) {
            button.setEnabled(true);
        } else {
            button.setEnabled(false);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button_next:
                EditText first = (EditText) getView().findViewById(R.id.edit_first_name);
                EditText last = (EditText) getView().findViewById(R.id.edit_last_name);
                EditText email = (EditText) getView().findViewById(R.id.edit_email);
                EditText password = (EditText) getView().findViewById(R.id.edit_password);
                String hashed = new String(Hex.encodeHex(DigestUtils.sha(password.toString())));
                mCallback.onNextSelected(first.toString(), last.toString(),
                        email.toString(), hashed);
                break;
        }
    }
}
