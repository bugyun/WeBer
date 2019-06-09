package vip.ruoyun.webkit.x5;

import android.content.Context;
import android.webkit.ValueCallback;

public class WeBer {

    private WeBer() {

    }

    public static void init(Context context) {
        WeBerHelper.init(context);
    }

    public static void openFile(Context context, ValueCallback<Boolean> valueCallback) {
        WeBerHelper.openFile(context, valueCallback);
    }


}
