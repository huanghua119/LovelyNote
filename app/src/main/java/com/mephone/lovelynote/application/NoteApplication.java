package com.mephone.lovelynote.application;

import android.app.Application;

import com.mephone.lovelynote.data.StorageAndroid;

/**
 * @author huanghua
 */
public class NoteApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        StorageAndroid.initialize(getApplicationContext());
    }
}
