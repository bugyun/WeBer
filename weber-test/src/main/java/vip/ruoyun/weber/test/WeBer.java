package vip.ruoyun.weber.test;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * Created by ruoyun on 2019-05-29.
 * Author:若云
 * Mail:zyhdvlp@gmail.com
 * Depiction:
 */
public class WeBer {

    private Builder builder;

    private WeBer(Builder builder) {
        this.builder = builder;
    }


    public static Builder init() {
        return new Builder();
    }


    public static class Builder {
        //https://www.jianshu.com/p/0d7d429bd216

        //WebSettings
        private boolean javaScriptEnabled = true;//如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        private boolean pluginsEnabled = true;//支持插件

        //设置自适应屏幕，两者合用（下面这两个方法合用）
        private boolean useWideViewPort = true;//将图片调整到适合webview的大小

        /**
         * 设置WebView是否使用预览模式加载界面。
         * 是否允许WebView度超出以概览的方式载入页面，默认false
         * 即缩小内容以适应屏幕宽度。该项设置在内容宽度超出WebView控件的宽度时生效，例如当getUseWideViewPort() 返回true时。
         */
        private boolean loadWithOverviewMode = false;

        /**
         * 设置WebView是否支持使用屏幕控件或手势进行缩放，默认是true，支持缩放。
         */
        private boolean supportZoom = true; // 支持缩放，默认为true。是下面那个的前提
        /**
         * 设置WebView是否使用其内置的变焦机制，该机制集合屏幕缩放控件使用，默认是false，不使用内置变焦机制。
         * 设置内置的缩放控件。若为false，则该WebView不可缩放
         */
        private boolean builtInZoomControls = true;
        /**
         * 设置WebView使用内置缩放机制时，是否展现在屏幕缩放控件上，默认true，展现在控件上。
         */
        private boolean displayZoomControls = false;
        /**
         * 设置WebView是否通过手势触发播放媒体，默认是true，需要手势触发。
         */
        private boolean mediaPlaybackRequiresUserGesture = false;

        //其他细节操作
        /**
         * 缓存模式如下：
         * LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
         * LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据。
         * LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
         * LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
         */
        private int cacheMode = WebSettings.LOAD_DEFAULT;
        /**
         * 设置脚本是否允许自动打开弹窗，默认false，不允许
         */
        private boolean javaScriptCanOpenWindowsAutomatically = true;
        /**
         * 设置WebView是否加载图片资源，默认true，自动加载图片
         */
        private boolean loadsImagesAutomatically = true; //支持自动加载图片
        /**
         * 是否禁止从网络（通过http和https URI schemes访问的资源）下载图片资源，默认值为false。
         * 注意，除非getLoadsImagesAutomatically()返回true,否则该方法无效。还请注意，即使此项设置为false，
         * 使用setBlockNetworkLoads(boolean)禁止所有网络加载也会阻止网络图片的加载。
         * 当此项设置的值从true变为false，WebView当前显示的内容所引用的网络图片资源会自动获取
         */
        private boolean blockNetworkImage = false;
        /**
         * 设置WebView加载页面文本内容的编码，默认“UTF-8”。
         */
        private String defaultTextEncodingName = "utf-8";//设置编码格式
        /**
         * 设置是否开启DOM存储API权限，默认false，未开启，
         */
        private boolean domStorageEnabled = true;
        /**
         * 设置是否开启数据库存储API权限，默认false，未开启，
         */
        private boolean databaseEnabled = false;
        /**
         * 设置Application缓存API是否开启，默认false，设置有效的缓存路径参考setAppCachePath(String path)方法
         */
        private boolean appCacheEnabled = true;
        /**
         * 设置当前Application缓存文件路径，Application Cache API能够开启需要指定Application具备写入权限的路径
         */
        private String appcachepath = "";
        /**
         * MIXED_CONTENT_ALWAYS_ALLOW 允许从任何来源加载内容，即使起源是不安全的；
         * MIXED_CONTENT_NEVER_ALLOW 不允许Https加载Http的内容，即不允许从安全的起源去加载一个不安全的资源；
         * MIXED_CONTENT_COMPLTIBILITY_MODE 当涉及到混合式内容时，WebView会尝试去兼容最新Web浏览器的风格；
         */
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        private int mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW;//原因是Android 5.0上Webview默认不允许加载Http与Https混合内容

        /**
         * 开启后，在用户输入密码时，会弹出提示框：询问用户是否保存密码；
         * 如果选择”是”，密码会被明文保到 /data/data/com.package.name/databases/webview.db 中，这样就有被盗取密码的危险
         */
        private boolean savePassWord = false;//保存密码


        /**
         * 设置是否允许 WebView 使用 File 协议,默认设置为true，即允许在 File 域下执行任意 JavaScript 代码
         * 如果使用 https://github.com/Tencent/VasSonic 框架的话，需要打开此选项
         */
        private boolean allowFileAccess = true; //默认 false
        /**
         * 是否允许通过 file url 加载的 Js代码读取其他的本地文件,file:///etc/hosts
         * 当设置成为 false 时，上述JS的攻击代码执行会导致错误，表示浏览器禁止从 file url 中的 JavaScript 读取其它本地文件。
         * 在Android 4.1前默认允许 , 在Android 4.1后默认禁止
         */
        private boolean allowFileAccessFromFileURLs = false; //
        /**
         * 是否允许通过 file url 加载的 Javascript 可以访问其他的源(包括http、https等源)
         * 在Android 4.1前默认允许（setAllowFileAccessFromFileURLs（）不起作用），在Android 4.1后默认禁止
         */
        private boolean allowUniversalAccessFromFileURLs = false; //设置可以访问文件

        /**
         * 是否允许在WebView中访问内容URL（Content Url），默认允许。内容Url访问允许WebView从安装在系统中的内容提供者载入内容。
         */
        private boolean allowContentAccess = true;


        private boolean acceptCookie = true;

        /**
         * 设置WebView是否保存表单数据，默认true，保存数据。
         * 在Android O中，该平台实现了一个功能齐全的自动填充功能来存储表单数据。因此，禁用了Webview表单数据保存功能。
         * 请注意，该功能将继续支持旧版本的Android和以前一样。
         */
        private boolean saveFormData = false;


        /**
         * 设置WebView中加载页面字体变焦百分比，默认100，整型数。
         */
        private int textZoom = 100;

        /**
         * 设置WebView访问第三方Cookies策略，
         */
        private boolean AcceptThirdPartyCookies = false;

        /**
         * 设置WebView是否支持多屏窗口
         */
        private boolean supportMultipleWindows = true;
        /**
         * 设置是否开启定位功能，默认true，开启定位
         */
        private boolean geolocationEnabled = true;
        private long appCacheMaxSize = Long.MAX_VALUE;

        /**
         * 设置是否支持flash插件
         */
        private WebSettings.PluginState setPluginState = WebSettings.PluginState.ON_DEMAND;

        private int scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY;
        private int backgroundColor = 0;


        private WebView webView;

        private Builder() {

        }

        public WeBer build(WebView webView) {
            this.webView = webView;
            WebSettings settings = webView.getSettings();
            settings.setAppCachePath("");//每个 Application 只调用一次
            settings.setAppCacheMaxSize(Long.MAX_VALUE);//
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
//            settings.setAppCacheEnabled(Long.MAX_VALUE);//
//            settings.setSaveFormData(Long.MAX_VALUE);//
//            settings.setSaveFormData(Long.MAX_VALUE);//
//            settings.setAllowContentAccess(Long.MAX_VALUE);//

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                settings.setMixedContentMode(mixedContentMode);
            }


            return new WeBer(this);
        }
    }
}
