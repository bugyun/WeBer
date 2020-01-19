package vip.ruoyun.weber.test;

import android.content.Context;
import android.webkit.WebView;

/**
 * Created by ruoyun on 2019-05-29.
 * Author:若云
 * Mail:zyhdvlp@gmail.com
 * Depiction:
 */
public class Test {


    public void test(Context context) {
        WebView webView = new WebView(context);
        WeBer.init().build(webView);



    }
}
