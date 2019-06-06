package vip.ruoyun.webviewhelper.helper;

import android.os.Build;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


/**
 * Created by ruoyun on 2018/7/4.
 * Author:若云
 * Mail:zyhdvlp@gmail.com
 * Depiction:
 */
public class WebViewHelper {
    private WebView mWebView;

    private WebViewHelper(WebView mWebView) {
        this.mWebView = mWebView;
    }

    public WebView getWebView() {
        return mWebView;
    }

    public void setWebViewClient(WebViewClient mWebViewClient) {
        mWebView.setWebViewClient(mWebViewClient);
    }

    public void addJavascriptInterface(BaseJavascriptInterface javascriptInterface, String name) {
        mWebView.addJavascriptInterface(javascriptInterface, name);
    }

    public void removeJavascriptInterface(String name) {
        mWebView.removeJavascriptInterface(name);
    }

    public static class Builder {
        private boolean acceptCookie = true;
        private boolean javaScriptEnabled = true;
        private boolean savePassWord = false;
        private boolean saveFormData = false;
        private boolean domStorageEnabled = true;
        private boolean allowFileAccess = true;
        private boolean builtInZoomControls = false;
        private boolean javaScriptCanOpenWindowsAutomatically = true;
        private int cacheMode = WebSettings.LOAD_NO_CACHE;
        private boolean blockNetworkImage = false;
        private int scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY;
        private int backgroundColor = 0;
        private int mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW;

        public Builder buildAcceptCookie(boolean acceptCookie) {
            this.acceptCookie = acceptCookie;
            return this;
        }

        public Builder buildJavaScriptEnabled(boolean javaScriptEnabled) {
            this.javaScriptEnabled = javaScriptEnabled;
            return this;
        }

        public Builder buildSavePassWord(boolean savePassWord) {
            this.savePassWord = savePassWord;
            return this;
        }

        public Builder buildSaveFormData(boolean saveFormData) {
            this.saveFormData = saveFormData;
            return this;
        }

        public Builder buildDomStorageEnabled(boolean domStorageEnabled) {
            this.domStorageEnabled = domStorageEnabled;
            return this;
        }

        public Builder buildAllowFileAccess(boolean allowFileAccess) {
            this.allowFileAccess = allowFileAccess;
            return this;
        }

        public Builder buildBuiltInZoomControls(boolean builtInZoomControls) {
            this.builtInZoomControls = builtInZoomControls;
            return this;
        }

        public Builder buildJavaScriptCanOpenWindowsAutomatically(boolean javaScriptCanOpenWindowsAutomatically) {
            this.javaScriptCanOpenWindowsAutomatically = javaScriptCanOpenWindowsAutomatically;
            return this;
        }

        public Builder buildCacheMode(int cacheMode) {
            this.cacheMode = cacheMode;
            return this;
        }

        public Builder buildBlockNetworkImage(boolean blockNetworkImage) {
            this.blockNetworkImage = blockNetworkImage;
            return this;
        }

        public Builder buildsScrollBarStyle(int scrollBarStyle) {
            this.scrollBarStyle = scrollBarStyle;
            return this;
        }

        public Builder buildBackgroundColor(int backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        public Builder buildMixedContentMode(int mixedContentMode) {
            this.mixedContentMode = mixedContentMode;
            return this;
        }

        public WebViewHelper build(WebView webView) {
            WebViewHelper webViewHelper = new WebViewHelper(webView);
            CookieManager.getInstance().setAcceptCookie(acceptCookie);
            webView.getSettings().setJavaScriptEnabled(javaScriptEnabled);
            webView.getSettings().setSavePassword(savePassWord);
            webView.getSettings().setSaveFormData(saveFormData);
            webView.getSettings().setDomStorageEnabled(domStorageEnabled);
            webView.getSettings().setAllowFileAccess(allowFileAccess);
            webView.getSettings().setBuiltInZoomControls(builtInZoomControls);
            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(javaScriptCanOpenWindowsAutomatically);
            webView.getSettings().setCacheMode(cacheMode);//设置 缓存模式
            webView.getSettings().setBlockNetworkImage(blockNetworkImage);
            webView.setScrollBarStyle(scrollBarStyle);
            webView.setBackgroundColor(backgroundColor);
            //在5.0以上，不允许一个HTML页面既有http也有https的加载，所以设置了下面的混合模式
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                webView.getSettings().setMixedContentMode(mixedContentMode);
            }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//        }
            return webViewHelper;
        }
    }
}
