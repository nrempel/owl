package com.magicpixellabs.front.register;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.magicpixellabs.owl.R;

public class FragmentPickNumber extends Fragment {

    /* Callback magic */
    Callback mCallback;

    interface Callback {

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
        View v = inflater.inflate(R.layout.register_choose_number, null);
        return v;
    }


}
