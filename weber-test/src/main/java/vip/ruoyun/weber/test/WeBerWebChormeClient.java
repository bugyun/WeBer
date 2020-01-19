package vip.ruoyun.weber.test;

import android.net.Uri;
import android.webkit.JsPromptResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import vip.ruoyun.weber.test.generate.WeBerBridgeImp;

public class WeBerWebChormeClient extends WebChromeClient {

    public WeBerWebChormeClient(WeBerView weBerView) {
        this.weBerView = weBerView;
    }

    //保存反射的对象
    private final WeBerView weBerView;

    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
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

        return super.onJsPrompt(view, url, message, defaultValue, result);
    }
}
