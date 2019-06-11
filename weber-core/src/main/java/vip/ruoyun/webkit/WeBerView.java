package vip.ruoyun.webkit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.webkit.WebView;

import java.util.HashMap;

public class WeBerView extends WebView {


    public HashMap<String, Object> objectMap = new HashMap<>();

    public WeBerView(Context context) {
        super(context);
    }

    public WeBerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WeBerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public WeBerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public WeBerView(Context context, AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
    }

    @SuppressLint("JavascriptInterface")
    @Override
    public void addJavascriptInterface(Object object, String name) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            super.addJavascriptInterface(object, name);
        } else {
            objectMap.put(name, object);
        }
    }


}
