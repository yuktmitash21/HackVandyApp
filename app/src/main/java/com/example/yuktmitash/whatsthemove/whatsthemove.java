package com.example.yuktmitash.whatsthemove;

import android.app.Application;
import android.support.multidex.MultiDexApplication;

import com.firebase.client.Firebase;

public class whatsthemove extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
