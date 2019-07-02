package vip.ruoyun.webviewhelper;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.ViewGroup;

import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.utils.TbsLog;

import vip.ruoyun.webkit.x5.WeBerChromeClient;
import vip.ruoyun.webkit.x5.WeBerHelper;
import vip.ruoyun.webkit.x5.WeBerView;
import vip.ruoyun.webkit.x5.WeBerViewClient;


public class WeberActivity extends AppCompatActivity {

    private TestWeBerChromeClient chromeClient = new TestWeBerChromeClient();
    private TestWeBerViewClient viewClient = new TestWeBerViewClient();
    private WeBerView mWeBerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weber);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);//这个对宿主没什么影响，建议声明

        mWeBerView = findViewById(R.id.mWeBerView);
        mWeBerView.setWebChromeClient(chromeClient);
        mWeBerView.setWebViewClient(viewClient);
        viewClient.setOnLoadWebViewListener(new WeBerViewClient.OnLoadWebViewListener() {
            @Override
            public void onPageFinished(boolean isSuccess) {
                //如果成功显示成功界面
                //失败显示失败界面
            }
        });
        chromeClient.setFileChooserListener(new WeBerChromeClient.FileChooserListener() {
            @Override
            public void onShowFileChooser(Intent intent, int requestCode) {
                startActivityForResult(intent, requestCode);
            }
        });
        long time = System.currentTimeMillis();
        mWeBerView.loadUrl(WeBerHelper.debugTBSUrl);
        TbsLog.d("time-cost", "cost time: " + (System.currentTimeMillis() - time));
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().sync();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        chromeClient.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * 激活WebView为活跃状态，能正常执行网页的响应
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (mWeBerView != null) {
            mWeBerView.onResume();
            mWeBerView.resumeTimers();
        }
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
        if (mWeBerView != null) {
            mWeBerView.onPause();
            mWeBerView.pauseTimers();
        }
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
        if (mWeBerView != null) {
            ((ViewGroup) mWeBerView.getParent()).removeView(mWeBerView);//rootView.removeView(mWebView);
            mWeBerView.destroy();
            mWeBerView = null;
        }
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWeBerView.canGoBack()) {
            mWeBerView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class TestWeBerChromeClient extends WeBerChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                //加载完毕进度条消失
//                progressView.setVisibility(View.GONE);
            } else {
                //更新进度
//                progressView.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }
    }

    private class TestWeBerViewClient extends WeBerViewClient {
        @Override
        public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
            //1.可以通过 url 来判断是否要接受证书
            if (view.getUrl().contains("http://ruoyun.vip") || view.getUrl().contains("https://ruoyun.vip")) {
                handler.proceed();
            } else {
                handler.cancel();//super.onReceivedSslError(view, handler, error);
            }

            //2.弹框,让用户来决定
            //https://codeday.me/bug/20170927/77276.html
//            final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
//            builder.setMessage("是否通过 ssl 验证");
//            builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    handler.proceed();
//                }
//            });
//            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    handler.cancel();
//                }
//            });
//            final AlertDialog dialog = builder.create();
//            dialog.show();
            //3.测试的时候通过
//                if (isDebug) {//因为使用抓包工具抓取https时，是需要安装证书的，验证自然无法通过。
//                    handler.proceed();// 接受所有网站的证书
//                } else {
//                    handler.cancel();//super.onReceivedSslError(view, handler, error);
//                }

        }
    }
}
