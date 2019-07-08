package vip.ruoyun.webkit.x5.jsbridge;


import com.tencent.smtt.sdk.ValueCallback;


public interface WebViewJavascriptBridge {
    void send(String data);

    void send(String data, ValueCallback<String> responseCallback);
}
