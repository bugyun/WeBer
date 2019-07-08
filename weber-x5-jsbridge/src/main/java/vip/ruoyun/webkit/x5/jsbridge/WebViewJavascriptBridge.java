package vip.ruoyun.webkit.x5.jsbridge;


import com.tencent.smtt.sdk.ValueCallback;


public interface WebViewJavascriptBridge {

    public void send(String data);

    public void send(String data, ValueCallback<String> responseCallback);


}
