package com.magicpixellabs.front.register;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.magicpixellabs.owl.R;

public class FragmentRegister extends Fragment implements View.OnClickListener {

    /* Callback magic */
    Callback mCallback;

    interface Callback {
        void onNextSelected();
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
        v.findViewById(R.id.button_next).setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        mCallback.onNextSelected();
    }
}
