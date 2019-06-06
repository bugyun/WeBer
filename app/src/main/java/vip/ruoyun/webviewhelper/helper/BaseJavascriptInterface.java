package vip.ruoyun.webviewhelper.helper;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by ruoyun on 2018/7/4.
 * Author:若云
 * Mail:zyhdvlp@gmail.com
 * Depiction:
 */
public class BaseJavascriptInterface {
    private Activity context;
    private long timeMillis;
    private Handler handler = new Handler(Looper.myLooper());

    public BaseJavascriptInterface(Activity context) {
        this.context = context;
        timeMillis = System.currentTimeMillis();
    }

    @JavascriptInterface
    public void closeWebview() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                context.finish();
            }
        });
    }

    @JavascriptInterface
    public String getDeviceInfo() {
        JSONObject jsonObject = new JSONObject();
        try {
//            jsonObject.put("pid", UserSPUtils.getDeviceMD5());
//            jsonObject.put("version", SystemUtils.getVersionName(context));
//            jsonObject.put("token", UserSPUtils.getUserToken());
            jsonObject.put("type", "android " + android.os.Build.VERSION.RELEASE);
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
        String deviceInfo = jsonObject.toString();
//        LogUtils.i(deviceInfo);
        return deviceInfo;
    }

    @JavascriptInterface
    public void open(String name) {
        // test 试试我们贷多少，apply 立即申请 login
        if (System.currentTimeMillis() - timeMillis > 1000) {
            timeMillis = System.currentTimeMillis();
//            LogUtils.i("callOnJsToUser执行我了。。。。");
            //                UserInfoActivity.actionStart(context, userId);
        }
//        LogUtils.d("callOnJsToUser  被调用");
        if (TextUtils.isEmpty(name)) {
            return;
        }
        switch (name) {
            case "login":
                handler.post(new Runnable() {
                    @Override
                    public void run() {
//                        LoginNewActivity.actionStart(context);
                    }
                });
                break;
        }
    }
}
