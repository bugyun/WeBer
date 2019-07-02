package vip.ruoyun.webkit;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.ViewGroup;
import android.webkit.*;

import java.util.ArrayList;

public class WeBerDemo {

    private ArrayList<WebChromeClient> webChromeClients = new ArrayList<>();
    private ArrayList<WebViewClient> webViewClients = new ArrayList<>();

    private WebView webView;

    public static void init() {

    }


    @SuppressLint("ObsoleteSdkInt")
    public void addWebChromeClient(WebChromeClient webChromeClient) {
        WebSettings settings = webView.getSettings();

        settings.setSavePassword(false);//关闭密码保存提醒功能
        //通过以下设置，防止越权访问，跨域等安全问题：
        settings.setAllowFileAccess(false);//使其不能加载本地的 html 文件，但是 x5 需要打开
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            settings.setAllowFileAccessFromFileURLs(false);
            settings.setAllowUniversalAccessFromFileURLs(false);
        }


        //JSCore
        //


//        对于Android调用JS代码的方法有2种：

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {//小于 4.3 要删除多余的 js 对象
            webView.removeJavascriptInterface("searchBoxJavaBridge_");
            webView.removeJavascriptInterface("accessibility");
            webView.removeJavascriptInterface("accessibilityTraversal");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {


            String name = "Nihao ";
            String method = "javascript:callJS(\"" + name + "\")";
            method = "javascript:callJS(\"你好\")";
            //19 4.4 以上的版本才能运行 占比99.16% https://mta.qq.com/mta/data/device/os
            webView.evaluateJavascript(method, new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {

                }
            });
            // loadUrl  evaluateJavascript

            webView.evaluateJavascript(method, null);
        } else {
            //必须要在页面加载完成之后才能调用
            webView.loadUrl("javascript:callJS()");
        }

        //往 js 中注入类 .17之前不安全
        webView.addJavascriptInterface(new AndroidtoJs(), "test");

        webView.loadUrl("file:///android_asset/javascript.html");
    }


    // 继承自Object类
    @WeBerBridge("test")
    public class AndroidtoJs {

        // 定义JS需要调用的方法
        // 被JS调用的方法必须加入@JavascriptInterface注解
        @JavascriptInterface
        public void hello(String msg) {
            System.out.println("JS调用了Android的hello方法");
        }

        public String test(String msg) {

            return "你好";
        }
    }

    @WeBerBridge("test")
    public class Android2JS {
        public String test(String msg) {
            return "你好";
        }

    }

    //https://android.googlesource.com/platform/cts/+/764c7c7fd72240330c15a3bcb1c7bd99cde83a1c/tests/tests/webkit/src/android/webkit/cts/PostMessageTest.java
    //https://html.spec.whatwg.org/multipage/web-messaging.html#messagechannel
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setWebChromeClient() {
        //第三种方式 postWebMessage WebMessagePort
        WebMessagePort[] webMessageChannel = webView.createWebMessageChannel();
//        webView.postWebMessage(webMessageChannel);


        final WebMessagePort[] channel = webView.createWebMessageChannel();

        WebMessagePort port = channel[0];
        port.setWebMessageCallback(new WebMessagePort.WebMessageCallback() {
            @Override
            public void onMessage(WebMessagePort port, WebMessage message) {

                message.getData();

            }
        });

        webView.postWebMessage(new WebMessage("", new WebMessagePort[]{channel[1]}),
                Uri.parse(""));


    }


    public void test() {
        //方式1. 加载一个网页：
        webView.loadUrl("http://www.google.com/");

        //方式2：加载apk包中的html页面
        webView.loadUrl("file:///android_asset/test.html");

        //方式3：加载手机本地的html页面
        webView.loadUrl("content://com.android.htmlfileprovider/sdcard/test.html");

        // 方式4： 加载 HTML 页面的一小段内容
        /**
         * 参数说明：
         * 参数1：需要截取展示的内容
         * 内容里不能出现 ’#’, ‘%’, ‘\’ , ‘?’ 这四个字符，若出现了需用 %23, %25, %27, %3f 对应来替代，否则会出现异常
         * 参数2：展示内容的类型
         * 参数3：字节码
         * webView.loadData(String data, String mimeType, String encoding)
         */
        //webView.loadData(String data, String mimeType, String encoding)

    }

    public void destory() {
        if (webView != null) {
            ((ViewGroup) webView.getParent()).removeView(webView);//rootView.removeView(mWebView);
            webView.destroy();
            webView = null;
        }
    }

}
