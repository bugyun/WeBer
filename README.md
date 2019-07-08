# WeBer
Android x5 内核 WebView 的 Helper
完美兼容 AndroidX 和 android 库，欢迎使用~~~

## 使用方法

x5内核现在只提供了 v7a 的 so 库，所以如果要使用的话，请在 主 build.gradle 中添加如下配置。
```java
android {
    compileSdkVersion 28
    defaultConfig {
        ndk {
            abiFilters "armeabi-v7a"
        }
    }
}
```

在子项目中的 build.gradle 文件中添加

```java
dependencies {
    implementation 'vip.ruoyun.webkit:weber-x5-core:1.0.2'
}
```

如果找不到项目，可以在根 build.gradle 中添加如下配置
```java
buildscript {
    repositories {
        ...
        jcenter()
        maven { url "https://dl.bintray.com/bugyun/maven" } //我的仓库,如果 jcenter 能找到，就不要添加
    }
}
allprojects {
    repositories {
        ...
        jcenter()
        maven { url "https://dl.bintray.com/bugyun/maven" } //我的仓库,如果 jcenter 能找到，就不要添加
    }
}
```

## 使用

Application中进行初始化
```java
WeBerHelper.init(context);
```
播放视频,context参数只能是 activity 类型的 context，不能设置为 Application 的 context。
```java
WeBerHelper.playVideo(context,videoUrl);
WeBerHelper.playVideo(Context context, String videoUrl, Bundle extraData);
```

打开本地文件
```java
WeBerHelper.openFile(Context context, String filePath, HashMap<String, String> params,ValueCallback<Boolean> valueCallback);
```

## WeBerChromeClient

需要继承 WeBerChromeClient,可以添加文件的监听器。

当 h5 有input 标签的时候，响应事件。可以不设置。
```html
<input class="filechooser" id="file_chooser" type="file" placeholder="file path">
//multiple="multiple" : 只能支持单文件，所以设置multiple无效
//accept="image/*" : 如果不传，默认所有文件类型
```

```java

class TestWeBerChromeClient extends WeBerChromeClient {
    ...
}

TestWeBerChromeClient  chromeClient = new TestWeBerChromeClient();

chromeClient.setFileChooserListener(new WeBerChromeClient.FileChooserListener() {
    @Override
    public void onShowFileChooser(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }
});

//需要在 onActivityResult 事件中添加如下方法
@Override
protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    chromeClient.onActivityResult(requestCode, resultCode, data);
}
```

## WeBerViewClient

可以设置网页是否加载成功，可以不设置。
```java
class TestWeBerViewClient extends WeBerViewClient {
    ...
}

private TestWeBerViewClient viewClient = new TestWeBerViewClient();

viewClient.setOnLoadWebViewListener(new WeBerViewClient.OnLoadWebViewListener() {
    @Override
    public void onPageFinished(boolean isSuccess) {
        //如果成功显示成功界面
        //失败显示失败界面
    }
});
```

## 最佳实践

```xml
<vip.ruoyun.webkit.x5.WeBerView
    android:id="@+id/mWeBerView"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

```xml
//manifest
<activity
    android:name=".WeberActivity"
    <!--支持视频横屏显示，如果不使用，可以不设置-->
    android:configChanges="orientation|screenSize|keyboardHidden"
    <!--theme 在非硬绘手机和声明需要controller的网页上，视频切换全屏和全屏切换回页面内会出现视频窗口透明问题-->
    android:theme="@style/WeBerTheme"
    <!--windowSoftInputMode 避免输入法界面弹出后遮挡输入光标的问题-->
    android:windowSoftInputMode="stateHidden|adjustResize">
</activity>

//styles.xml
<style name="WeBerTheme" parent="AppTheme">
    <item name="android:windowIsTranslucent">false</item>
</style>
```

```java
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
    public void onBackPressed() {
        if (mWeBerView.canGoBack()) {
            mWeBerView.goBack();
            return;
        }
        super.onBackPressed();
    }

    private class TestWeBerChromeClient extends WeBerChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                //加载完毕进度条消失
            } else {
                //更新进度
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
```

## JSBridge
如果你的项目使用了 https://github.com/lzyzsd/JsBridge 开源库，那么现在可以使用本库的 JSBridge 来进行兼容。

使用
```
dependencies {
    implementation 'vip.ruoyun.webkit:weber-x5-jsbridge:1.0.0'
}
```

继承 WeBerViewBridgeClient 类
```java
private class TestWeBerViewClient extends WeBerViewBridgeClient {
    ...
}

TestWeBerViewClient viewClient = new TestWeBerViewClient(mWeBerView);
mWeBerView.setWebViewClient(viewClient);

//注册方法
viewClient.registerHandler("submitFromWeb", new BridgeHandler() {
    @Override
    public void handler(String data, ValueCallback<String> valueCallback) {
        valueCallback.onReceiveValue("");
    }
});

//调用方法
viewClient.callHandler("functionInJs", "data from Java", new ValueCallback<String>() {
    @Override
    public void onReceiveValue(String data) {
        // TODO Auto-generated method stub
        Log.i("zyh", "reponse data from js " + data);
    }
});
```

## 混淆

内部已经内置了混淆，所以不需要添加任何混淆


