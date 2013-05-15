package com.magicpixellabs;

import android.app.Application;
import com.roscopeco.ormdroid.ORMDroidApplication;

public class ApplicationOwl extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ORMDroidApplication.initialize(this);
    }
}
