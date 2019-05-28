package vip.ruoyun.webviewhelper;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.Button;

import com.tencent.smtt.sdk.WebView;

public class X5WebActivity extends AppCompatActivity {

    private Button mButton;
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);//为了避免视频闪屏和透明问题，需要如下设置（这个对宿主没什么影响，建议声明）
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//避免输入法界面弹出后遮挡输入光标的问题
        setContentView(R.layout.activity_x5_web);

        mButton = findViewById(R.id.mButton);
        mWebView = findViewById(R.id.mWebView);

        mWebView.loadUrl("https://debugtbs.qq.com/");
    }
}
