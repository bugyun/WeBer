package vip.ruoyun.weber.test;

import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.lang.reflect.Method;
import java.util.HashMap;

import vip.ruoyun.weber.test.generate.WeBerBridgeImp;

public class WeBerWebClient extends WebViewClient {


    public WeBerWebClient(WeBerView weBerView) {
        this.weBerView = weBerView;
    }

    //保存反射的对象
    private HashMap<String, Method> methodMap = new HashMap<>();
    private final WeBerView weBerView;

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url.startsWith("js://webview")) { // 如果是返回数据
            //解析 url
            Uri parse = Uri.parse(url);
            String method = parse.getQueryParameter("method");
            String callback = parse.getQueryParameter("callback");
            String value = parse.getQueryParameter("value");
            //下面代码自动生成
            Object object = weBerView.objectMap.get(method);
            if (null != object) {
                WeBerBridgeImp.eve(object, method, callback, value, weBerView);
            }
            return true;
        }
        return super.shouldOverrideUrlLoading(view, url);
    }

}
