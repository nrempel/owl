package com.magicpixellabs.front;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.magicpixellabs.front.login.ActivityLogin;
import com.magicpixellabs.front.register.ActivityRegister;
import com.magicpixellabs.owl.R;

public class ActivitySplashScreen extends Activity implements View.OnClickListener {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        getActionBar().setTitle(null);

        findViewById(R.id.button_login).setOnClickListener(this);
        findViewById(R.id.button_register).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_login:
                startActivity(new Intent(this, ActivityLogin.class));
                break;
            case R.id.button_register:
                startActivity(new Intent(this, ActivityRegister.class));
                break;
        }
    }
}