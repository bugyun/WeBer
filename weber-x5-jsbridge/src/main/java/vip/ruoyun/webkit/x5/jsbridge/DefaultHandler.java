package vip.ruoyun.webkit.x5.jsbridge;


import com.tencent.smtt.sdk.ValueCallback;

public class DefaultHandler implements BridgeHandler {

    String TAG = "DefaultHandler";

    @Override
    public void handler(String data, ValueCallback<String> function) {
        if (function != null) {
            function.onReceiveValue("DefaultHandler response data");
        }
    }

}
