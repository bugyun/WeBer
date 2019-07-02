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
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setSupportMultipleWindows(false);

        webSetting.setUseWideViewPort(true);
        // webSetting.setLoadWithOverviewMode(true);

        webSetting.setAppCacheEnabled(true);
        // webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setPluginsEnabled(true);


        webSetting.setAppCachePath(getContext().getDir("appcache", Context.MODE_PRIVATE).getPath());
        webSetting.setDatabasePath(getContext().getDir("databases", Context.MODE_PRIVATE).getPath());
        webSetting.setGeolocationDatabasePath(getContext().getDir("geolocation", Context.MODE_PRIVATE).getPath());
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        // webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        // webSetting.setPreFectch(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//在Android 5.0之后，WebView默认不允许Https + Http的混合使用方式
            webSetting.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
    }

}
