package vip.ruoyun.webviewhelper;

import android.app.Application;

import vip.ruoyun.webkit.x5.WeBerHelper;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        WeBerHelper.multiProcessOptimize();
        WeBerHelper.init(this);
    }
}
