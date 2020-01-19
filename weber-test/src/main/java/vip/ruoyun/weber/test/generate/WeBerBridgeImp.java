package vip.ruoyun.weber.test.generate;

import android.text.TextUtils;
import vip.ruoyun.weber.test.WeBerDemo.Android2JS;
import vip.ruoyun.weber.test.WeBerDemo.AndroidtoJs;
import vip.ruoyun.weber.test.WeBerView;

import java.lang.reflect.Method;
import java.util.HashMap;


public class WeBerBridgeImp {

    public static void eve(Object object, String method, String callback, String value, WeBerView weBerView) {
        switch (object.getClass().getSimpleName()) {
            case "AndroidtoJs":
                AndroidtoJs androidtoJs = (AndroidtoJs) object;//获取对象
                switch (method) {
                    case "hello":
                        androidtoJs.hello(value);
                        break;
                    case "test":
                        if (TextUtils.isEmpty(callback)) {
                            String s = androidtoJs.test(value);
                            weBerView.loadUrl("javascript:callJS(\"" + s + "\")");
                        }
                        break;
                }
                break;
            case "Android2JS":
                Android2JS android2JS = (Android2JS) object;//获取对象
                switch (method) {
                    case "test":
                        if (TextUtils.isEmpty(callback)) {
                            String s = android2JS.test(value);
                            weBerView.loadUrl("javascript:callJS(\"" + s + "\")");
                        }
                        break;
                }
                break;
            default:
                break;
        }
    }



    private HashMap<String, Method> methodMap = new HashMap<>();






}
