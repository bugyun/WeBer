package vip.ruoyun.webviewhelper;

import android.app.Application;

import vip.ruoyun.webkit.WeBerDemo;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        X5WebHelper.init(this);
        WebViewHelper.init();

        WeBerDemo.init();
    }
}
