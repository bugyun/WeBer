# WeBer
Android x5 内核 WebView 的 Helper
完美兼容 AndroidX 和 android 库，欢迎使用~~~

## TBS(腾讯浏览服务)x5优势

    1. 速度快：相比系统WebView的网页加载速度有近30%的提升
    2. 省流量：云端优化技术使流量节省20%
    3. 更安全：24小时安全问题解决机制
    4. 更稳定：经过亿级用户的使用考验，CRASH率0.15%
    5. 集成强大的视频播放器，支持各种视频格式直接打开
    6. 适屏排版、字体设置等浏览增强功能的提供
    7. Html5更完整支持
    8. 无系统碎片化问题、更少的兼容性问题

## 特有功能
支持 h5 中 input 标签，标准 Android WebView 不支持此标签并且只支持单文件操作。
- 打开所有文件 `<input capture="*/*"/>`
- 打开照相机 `<input capture="camera"/>` 优先级大于 accept
- 打开摄像机 `<input accept="video/*"/>`
- 打开图片 `<input accept="image/*"/>`
- 等等相关文件操作

## 关于
如果不想使用 X5 内核,那么现在有支持系统的 WebView input 标签功能的版本

[我是链接](https://github.com/bugyun/WeBer/blob/master/weber-core/README.md)

## 使用方法

基于 x5 版本：43697,更新日期：2019-08-08

x5内核现在只提供了 32位 的 so 库，暂不支持 64 位。

如果想减少包体积的话，可以通过在主 build.gradle 中添加如下配置，减少 so 文件。
```java
android {
    compileSdkVersion 28
    minSdkVersion 15 //x5 要求最低版本
    defaultConfig {
        ndk {
            abiFilters "armeabi-v7a"
        }
    }
}
```

如果报错的话，那么需要在gradle.properties文件中加上,如果不报错，请不要添加。
```java
//在 com.android.tools.build:gradle:3.5.0 中已经不需要添加
android.useDeprecatedNdk=true
```

### 注意

[x5内核在 targetAPI 为Q时在Android Q上必现crash](https://x5.tencent.com/tbs/technical.html#/detail/sdk/1/9ba4fb91-a525-45ec-8dd2-29a91bf02c5d)

> 目前X5内核尚未全部支持targetAPI Android Q，在部分Android Q机型下会出现crash，我们正在解决中，请将您的target APi 调整为Q及以下或者在Android Q中调用QbSdk.forceSysWebView禁用X5。

请在项目中注意这个问题，如果想要使用 x5 ，请设置 targetAPI 28 ,官网如果修复了这个问题，此项目会立刻升级 x5 版本。

## 添加依赖

jcenter()仓库,在子项目中的 build.gradle 文件中添加

```java
dependencies {
    implementation 'vip.ruoyun.webkit:weber-x5-core:1.0.8'
}
```

## 使用

### Application中进行初始化
```java
WeBer.with()
        .multiProcessOptimize(true)//可选,接⼊TBS SDK后，解决⾸次启动卡顿问题
        .interceptor(new WeBer.Interceptor() { //在初始化之前做一些配置
            @Override
            public void beforeInit(final Context context) {
                //QbSdk 设置
                QbSdk.setDownloadWithoutWifi(true);
                QbSdk.setTbsListener(new TbsListener() {
                    @Override
                    public void onDownloadFinish(int i) {
                        //tbs内核下载完成回调
                    }

                    @Override
                    public void onInstallFinish(int i) {
                        //内核安装完成回调，
                    }

                    @Override
                    public void onDownloadProgress(int i) {
                        //下载进度监听
                    }
                });
            }
        })
        .preInitCallBack(new PreInitCallback() {//添加x5加载回调方法
            @Override
            public void onCoreInitFinished() {

            }

            @Override
            public void onViewInitFinished(final boolean b) {

            }
        })
        //配置 android:authorities => "${applicationId}."+"${authority}"
        //下面会生成  => android:authorities="${applicationId}.provider"
        .authority("provider")
        .build(this);

//得到错误信息，当客户端 crash 的时候,可以在bugly上设置crash 并上传
//map.put("x5crashInfo", x5CrashInfo);
String crashMessage = WeBer.getCrashMessage(this);
```

### 播放视频

context参数只能是 activity 类型的 context，不能设置为 Application 的 context。

```java
WeBer.playVideo(context,videoUrl);
WeBer.playVideo(Context context, String videoUrl, Bundle extraData);
```

### 打开本地文件

```java
WeBer.openFile(Context context, String filePath, HashMap<String, String> params,ValueCallback<Boolean> valueCallback);
```

### 打开 Chrome 调试功能
```java
WeBerView.setWebContentsDebuggingEnabled(true);
```

## WeBerChromeClient

需要继承 WeBerChromeClient,可以添加文件的监听器。

当 h5 有input 标签的时候，响应事件。可以不设置。
```html
<input class="filechooser" id="file_chooser" type="file" placeholder="file path">
```
#### input 标签相关配置

- ```multiple="multiple" : 只能支持单文件，所以设置multiple无效```
- ```accept="video/*" : 打开摄像机功能```
- ```capture="camera" : 如果有值的话，就会调用照相机功能，优先级大于 accept```
- ```accept="image/*" : 选择文件,根据设置 image/*图片, */* 所有文件```

#### onActivityResult
不需要在 onActivityResult 事件中添加回调,使用 https://github.com/bugyun/AvoidOnResultHelper 优化回调.


#### WeBerChromeClient.setFileChooserIntercept() 拦截器

如果直接使用 input 标签来打开具体对应的功能,是需要 Android 手机对应的权限的.可以通过下面的方法添加拦截器，true 表示拦截此次打开（相机/文件/摄像机）的请求来判断是否炫需要权限。

可以通过判断 intent 的 getAction() 来进行具体权限的检查,然后请求对应的权限。代码如下

权限库推荐使用 https://github.com/bugyun/MissPermission 来进行权限的检查和请求。

```java
class TestWeBerChromeClient extends WeBerChromeClient {
    ...
}

TestWeBerChromeClient  chromeClient = new TestWeBerChromeClient(this);

//可选操作
chromeClient.setFileChooserIntercept(new WeBerChromeClient.FileChooserIntercept() {
    /**
     * @param isCapture  是否是照相功能
     * @param acceptType input标签 acceptType的属性
     * @param intent     意图
     * @return 是否要拦截, 可根据 intent 来判断是否要进行照相机权限检查,代码如下
     */
    @Override
    public boolean onFileChooserIntercept(boolean isCapture,String[] acceptType, Intent intent) {
        if (MediaStore.ACTION_VIDEO_CAPTURE.equals(intent.getAction())) {//要使用摄像机
            //要使用摄像机,判断权限 android.permission.CAMERA
            //可以使用 https://github.com/bugyun/MissPermission ,来进行权限的请求.
            return true;//拦截
        } else if (MediaStore.ACTION_IMAGE_CAPTURE.equals(intent.getAction())) {//要使用照相机
            //要使用照相机,判断权限 android.permission.CAMERA
            //可以使用 https://github.com/bugyun/MissPermission ,来进行权限的请求.
            return true;//拦截
        }
        return false;//不拦截
    }
});
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
        chromeClient.setFileChooserIntercept(new WeBerChromeClient.FileChooserIntercept() {
            @Override
            public boolean onFileChooserIntercept(boolean isCapture, String[] acceptType, Intent intent) {
                //在打开文件之前,处理 intent ,修改或者添加参数
                return false;
            }
        });
        long time = System.currentTimeMillis();
        mWeBerView.loadUrl(WeBer.debugTBSUrl);
        TbsLog.d("time-cost", "cost time: " + (System.currentTimeMillis() - time));
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().sync();
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

## 混淆

内部已经内置了混淆，所以不需要添加任何混淆

---

## JSBridge
如果你的项目使用了 https://github.com/lzyzsd/JsBridge (1.0.4版本)开源库，,那么现在可以替换成本库的 JSBridge 来进行兼容。

使用
```
dependencies {
    implementation 'vip.ruoyun.webkit:weber-x5-jsbridge:1.0.1'
}
```

继承 WeBerViewBridgeClient 类
```java
private class TestWeBerViewClient extends WeBerViewBridgeClient {
    public TestWeBerViewClient(WeBerView weBerView) {
        super(weBerView);
    }

    ...
}

mWeBerView.setWebViewClient(viewClient);
TestWeBerViewClient viewClient = new TestWeBerViewClient(mWeBerView);

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


