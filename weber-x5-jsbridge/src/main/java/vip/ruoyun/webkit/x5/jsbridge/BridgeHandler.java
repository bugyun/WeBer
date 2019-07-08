package vip.ruoyun.webkit.x5.jsbridge;

import com.tencent.smtt.sdk.ValueCallback;

/**
 * Created by ruoyun on 2019-07-08.
 * Author:若云
 * Mail:zyhdvlp@gmail.com
 * Depiction:
 */
public interface BridgeHandler {
    void handler(String data, ValueCallback<String> valueCallback);
}
