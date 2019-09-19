package vip.ruoyun.webkit.x5;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;

import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

import java.util.Map;

public class WeBerView extends WebView {

    public WeBerView(Context context) {
        super(context);
        init();
    }

    public WeBerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public WeBerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    public WeBerView(Context context, AttributeSet attributeSet, int i, boolean b) {
        super(context, attributeSet, i, b);
        init();
    }

    public WeBerView(Context context, AttributeSet attributeSet, int i, Map<String, Object> map, boolean b) {
        super(context, attributeSet, i, map, b);
        init();
    }

    private void init() {
        WeBerViewClient client = new WeBerViewClient();
        setWebViewClient(client);
        initWebViewSettings();
        this.getView().setClickable(true);
    }

    private void initWebViewSettings() {
        WebSettings webSetting = this.getSettings();
        webSetting.setJavaScriptEnabled(true); //支持js

        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);//支持通过JS打开新窗口
        webSetting.setAllowFileAccess(true);////设置可以访问文件
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);//可能的话，使所有列的宽度不超过屏幕宽度。默认值

        webSetting.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSetting.setBuiltInZoomControls(true);//设置内置的缩放控件
        webSetting.setSupportMultipleWindows(false);//是否支持多窗口

        webSetting.setUseWideViewPort(true);//将图片调整到适合webview的大小
        webSetting.setLoadWithOverviewMode(true);// 缩放至屏幕的大小

        webSetting.setAppCacheEnabled(true);
        webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setTextZoom(100);//设置文本的缩放倍数，默认为 100

        webSetting.setPluginsEnabled(true);//支持插件

        webSetting.setAppCachePath(getContext().getDir("appcache", Context.MODE_PRIVATE).getPath());
        webSetting.setDatabasePath(getContext().getDir("databases", Context.MODE_PRIVATE).getPath());
        webSetting.setGeolocationDatabasePath(getContext().getDir("geolocation", Context.MODE_PRIVATE).getPath());
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);//提高渲染的优先级
        // webSetting.setPreFectch(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSetting.setAllowUniversalAccessFromFileURLs(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//在Android 5.0之后，WebView默认不允许Https + Http的混合使用方式
            webSetting.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            this.removeJavascriptInterface("searchBoxJavaBridge_");
        }
    }

}
