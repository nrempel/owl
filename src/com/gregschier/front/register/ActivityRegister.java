package com.gregschier.front.register;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import com.gregschier.simplesms.R;

public class ActivityRegister extends Activity implements FragmentRegister.Callback{

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_container);
        getActionBar().setTitle(R.string.title_register);

        getFragmentManager().beginTransaction().
                replace(R.id.fragment_container, new FragmentRegister()).
                commit();
    }

    @Override
    public void onNextSelected() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, new FragmentPickNumber());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();
    }

}