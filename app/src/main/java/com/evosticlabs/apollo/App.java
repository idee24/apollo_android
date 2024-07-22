package com.evosticlabs.apollo;

import android.app.Application;

import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

/**
 * @Created by Yerimah on 22/07/2024.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
    }
}
