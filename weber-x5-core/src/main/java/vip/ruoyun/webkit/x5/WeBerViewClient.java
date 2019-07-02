package vip.ruoyun.webkit.x5;

import android.graphics.Bitmap;

import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

public class WeBerViewClient extends WebViewClient {

    private boolean isReceivedError = false;

    private OnLoadWebViewListener onLoadWebViewListener;

    public void setOnLoadWebViewListener(OnLoadWebViewListener onLoadWebViewListener) {
        this.onLoadWebViewListener = onLoadWebViewListener;
    }

    public interface OnLoadWebViewListener {
        void onPageFinished(boolean isSuccess);
    }

    /**
     * 防止加载网页时调起系统浏览器
     */
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }


    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        isReceivedError = false;
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        isReceivedError = true;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if (onLoadWebViewListener != null) {
            onLoadWebViewListener.onPageFinished(!isReceivedError);
        }
    }
}
