package vip.ruoyun.webviewhelper;

import android.app.Application;

import vip.ruoyun.webkit.WeBerDemo;
import vip.ruoyun.webkit.x5.WeBer;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        WeBer.init(this);
        WebViewHelper.init();

        WeBerDemo.init();
    }
}
