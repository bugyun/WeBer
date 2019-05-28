package vip.ruoyun.webviewhelper;

import android.app.Application;

import vip.ruoyun.webkit.WeBer;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        X5WebHelper.init(this);
        WebViewHelper.init();

        WeBer.init();
    }
}
