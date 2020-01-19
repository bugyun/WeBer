package vip.ruoyun.webviewhelper;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ClientCertRequest;
import android.webkit.ConsoleMessage;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import vip.ruoyun.webkit.WeBer;
import vip.ruoyun.webkit.WeBerChromeClient;
import vip.ruoyun.webkit.WeBerChromeClient.FileChooserIntercept;

public class WebActivity extends AppCompatActivity {

    private WebView mWebView;

    private Button mButton;

    private ViewGroup rootView;

    private boolean isDebug;

    private Handler handler = new Handler(Looper.myLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        rootView = findViewById(R.id.rootView);
        mButton = findViewById(R.id.mButton);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mWebView = findViewById(R.id.mWebView);
//        mWebView.onResume();
//        mWebView.loadUrl("https://www.baidu.com/");
        //往 js 中注入类 .17之前不安全
        mWebView.addJavascriptInterface(new AndroidtoJs(), "test");

        WebSettings wetSettings = mWebView.getSettings();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//在Android 5.0之后，WebView默认不允许Https + Http的混合使用方式
            wetSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        wetSettings.setDomStorageEnabled(true);//DOM storage 是HTML5提供的一种标准接口，主要将键值对存储在本地
        wetSettings.setJavaScriptEnabled(true);

        WeBerChromeClient weBerChromeClient = new WeBerChromeClient(this);
        weBerChromeClient.setFileChooserIntercept(new FileChooserIntercept() {
            @Override
            public boolean onFileChooserIntercept(final boolean isCapture, final String[] acceptType,
                    final Intent intent) {
                return false;
            }
        });
        mWebView.setWebChromeClient(weBerChromeClient);

        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                    long contentLength) {

            }
        });
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
                //客户端证书
                super.onReceivedClientCertRequest(view, request);
            }

            @Override
            public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {

                //1.可以通过 url 来判断是否要接受证书
                if (error.getUrl().contains("http://ruoyun.vip") || error.getUrl().contains("https://ruoyun.vip")) {
                    handler.proceed();
                } else {
                    handler.cancel();//super.onReceivedSslError(view, handler, error);
                }

                //2.弹框,让用户来决定
                //https://codeday.me/bug/20170927/77276.html
                final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage("是否通过 ssl 验证");
                builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.proceed();
                    }
                });
                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.cancel();
                    }
                });
                final AlertDialog dialog = builder.create();
                dialog.show();

                //3.测试的时候通过
//                if (isDebug) {//因为使用抓包工具抓取https时，是需要安装证书的，验证自然无法通过。
//                    handler.proceed();// 接受所有网站的证书
//                } else {
//                    handler.cancel();//super.onReceivedSslError(view, handler, error);
//                }
            }
        });
        //file:///android_asset/webpage/fileChooser.html
        mWebView.loadUrl("file:///android_asset/webpage/fileChooser.html");

    }

    // 继承自Object类
    public class AndroidtoJs {

        // 定义JS需要调用的方法
        // 被JS调用的方法必须加入@JavascriptInterface注解
        @SuppressLint("NewApi")
        @JavascriptInterface
        public String hello(String msg) {
            long newTime = System.currentTimeMillis();
            Log.e("zyh", newTime + "");
            System.out.println("JS调用了Android的hello方法");

            handler.post(new Runnable() {
                @Override
                public void run() {
                    final long time = System.currentTimeMillis();
                    mWebView.evaluateJavascript("javascript:callJS()", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            //回调消耗的时间比较久,是使用其他方式的 7 倍时间多.
                            Log.d("zyh", "返回值为:" + value);
                            Log.d("zyh", "cost time: " + (System.currentTimeMillis() - time));
                        }
                    });
//                    mWebView.loadUrl("javascript:callJS()");
                    Log.d("zyh", "cost time: " + (System.currentTimeMillis() - time));
                }
            });
            return "你好,1111!";
        }

        @JavascriptInterface
        public String test(String msg) {

            return "你好";
        }

        @JavascriptInterface
        public void callback(String name, String path) {

            Log.d("zyh", "callback " + path);

        }
    }

    /**
     * 激活WebView为活跃状态，能正常执行网页的响应
     */
    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    /**
     * 当页面被失去焦点被切换到后台不可见状态，需要执行onPause
     * 通过onPause动作通知内核暂停所有的动作，比如DOM的解析、plugin的执行、JavaScript执行。
     * 通知内核尝试停止所有处理，如动画和地理位置，但是不能停止Js，如果想全局停止Js，
     * 可以调用pauseTimers()全局停止Js，调用onResume()恢复。
     */
    @Override
    public void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    /**
     * 销毁Webview
     * 在关闭了Activity时，如果Webview的音乐或视频，还在播放。就必须销毁Webview
     * 但是注意：webview调用destory时, webview仍绑定在Activity上
     * 这是由于自定义webview构建时传入了该Activity的context对象
     * 因此需要先从父容器中移除webview, 然后再销毁webview:
     */
    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            ((ViewGroup) mWebView.getParent()).removeView(mWebView);//rootView.removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }

        super.onDestroy();
    }
}
