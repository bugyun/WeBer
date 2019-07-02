package vip.ruoyun.webviewhelper.helper;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;


/**
 * Created by ruoyun on 2018/7/4.
 * Author:若云
 * Mail:zyhdvlp@gmail.com
 * Depiction:
 */
//Web视图
public class BaseWebViewClient extends WebViewClient {

    private static final String TAG = "WebViewHelper";

    private boolean isReceivedError = false;

    private OnLoadWebViewListener onLoadWebViewListener;

    public interface OnLoadWebViewListener {
        void onPageFinished(boolean isSuccess);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        isReceivedError = false;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
//        if (url.startsWith("tel:")) {
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//            view.getContext().startActivity(intent);
//            return true;
//        }
//            if (url.startsWith("http://callback/h5")) {
//                MessageHelper.sendMessage(new H5PhoneAuthMessage());
//                activity.finish();
//                return true;
//            }
        view.loadUrl(url);
        return true;
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        handler.proceed();// 接受所有网站的证书
//            handler.cancel();
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        isReceivedError = true;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if (!view.getSettings().getLoadsImagesAutomatically()) {
            view.getSettings().setLoadsImagesAutomatically(true);
        }
        if (onLoadWebViewListener != null) {
            onLoadWebViewListener.onPageFinished(!isReceivedError);
        }
    }
}
