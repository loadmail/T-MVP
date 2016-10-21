package com;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import com.base.util.SpUtil;

/**
 * Created by baixiaokang on 16/4/23.
 */
public class App extends Application {
    private static App mApp;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        SpUtil.init(this);  // TODO: 2016/10/21 sp的init
    }

    public static Context getAppContext() {
        return mApp;
    }

    public static Resources getAppResources() {
        return mApp.getResources();
    }

}
