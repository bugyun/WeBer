package vip.ruoyun.webkit.x5;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsVideo;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebView;
import java.util.HashMap;

public class WeBer {

    public static final String debugTBSUrl = "http://debugtbs.qq.com/";

    static String authority = "fileProvider";

    public static Builder with() {
        return new Builder();
    }

    public interface Interceptor {

        void beforeInit(Context context);
    }

    public static class Builder {

        private boolean isMultiProcessOptimize = false;

        private QbSdk.PreInitCallback preInitCallback;

        private Interceptor interceptor;

        private Builder() {
        }

        /**
         * 接⼊TBS SDK后，⾸次启动卡顿怎么办？
         * <p>
         * 由于在Android 5.0 +系统使⽤Art虚拟机在APP⾸次加载Dex时会进⾏Dex2oat，会有⼀定耗时，APP
         * <p>
         * 接⼊时可以尽早的调⽤QbSdk.initX5Environment⽅法，在异步线程初始化。如果想达到进⼀步的优 化效果，可以使⽤SpeedyClassLoader⽅案进⾏集成
         * 多进程方式
         * 在调用TBS init 初始化之前、创建WebView之前进行如下配置，以开启优化方案
         */
        public Builder multiProcessOptimize(boolean isMultiProcessOptimize) {
            this.isMultiProcessOptimize = isMultiProcessOptimize;
            return this;
        }

        public Builder preInitCallBack(@NonNull QbSdk.PreInitCallback preInitCallback) {
            this.preInitCallback = preInitCallback;
            return this;
        }

        /**
         * 在初始化之前做一些配置
         */
        public Builder interceptor(Interceptor interceptor) {
            this.interceptor = interceptor;
            return this;
        }

        public Builder authority(@NonNull String authority) {
            WeBer.authority = authority;
            return this;
        }

        public void build(Context context) {
            if (isMultiProcessOptimize) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
                map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
                QbSdk.initTbsSettings(map);
            }
            if (interceptor != null) {
                interceptor.beforeInit(context);
            }
            QbSdk.initX5Environment(context.getApplicationContext(), preInitCallback); //x5内核初始化接口
        }
    }

    //--------------静态方法----------------------------

    /**
     * 得到错误信息，当客户端 crash 的时候
     * map.put("x5crashInfo", x5CrashInfo);
     */
    public static String getCrashMessage(Context context) {
        return WebView.getCrashExtraMessage(context);
    }


    /**
     * 播放视频的调用接口
     * 通过TbsVideo的静态方法，如下：
     * canUseTbsPlayer(context) : 判断当前Tbs播放器是否已经可以使用
     * openVideo(context,videoUrl) : 直接调用播放接口，传入视频流的url
     * openVideo(context,videoUrl,extraData) : extraData对象是根据定制需要传入约定的信息，没有需要可以传如null
     * screenMode  102 来实现默认全屏+控制栏等UI
     */
    public static boolean playVideo(Context context, String videoUrl) {
        return playVideo(context, videoUrl, null);
    }

    public static boolean playVideo(Context context, String videoUrl, Bundle extraData) {
        if (TbsVideo.canUseTbsPlayer(context)) {//是否可以播放
            TbsVideo.openVideo(context, videoUrl, extraData);
            return true;
        }
        return false;
    }

    /**
     * 目前TSB还不支持在线预览功能，只支持本地文件打开，打开方法请参考官网文件版本sdk内的使用说明https://x5.tencent.com/tbs/sdk.html
     * 使用要求: android.permission.WRITE_EXTERNAL_STORAGE 权限
     *
     * @param context       调起 miniqb 的 Activity 的 context。此参数只能是 activity 类型的 context，不能设置为 Application 的 context。
     * @param filePath      文件路径。格式为 android 本地存储路径格式，例如：/sdcard/Download/xxx.doc. 不支持 file:/// 格式。暂不支持在线文件。
     * @param params        miniqb 的扩展功能。为非必填项，可传入 null 使用默认设置。
     *                      local: “true”表示是进入文件查看器，如果不设置或设置为“false”，则进入 miniqb 浏览器模式。不是必 须设置项。
     *                      style: “0”表示文件查看器使用默认的 UI 样式。“1”表示文件查看器使用微信的 UI 样式。不设置此 key 或设置错误值，则为默认 UI 样式。
     *                      topBarBgColor: 定制文件查看器的顶部栏背景色。格式为“#xxxxxx”，例“#2CFC47”;不设置此 key 或设置 错误值，则为默认 UI 样式。
     *                      menuData: 该参数用来定制文件右上角弹出菜单，可传入菜单项的 icon 的文本，用户点击菜单项后，sdk 会通过 startActivity+intent 的方式回调。
     * @param valueCallback 提供 miniqb 打开/关闭时给调用方回调通知,以便应用层做相应处理。 在单独进程打开文件的场景中，回调参数出现如下字符时，表示可以关闭当前进程，避免内存占用。
     * @return 1：用 QQ 浏览器打开 2：用 MiniQB 打开 3：调起阅读器弹框 -1：filePath 为空 打开失败
     */
    public static int openFile(Context context, String filePath, HashMap<String, String> params,
            final ValueCallback<Boolean> valueCallback) {
        QbSdk.canOpenFile(context, filePath, new ValueCallback<Boolean>() {
            @Override
            public void onReceiveValue(Boolean isCanOpen) {
                valueCallback.onReceiveValue(isCanOpen);
            }
        });
        return QbSdk.openFileReader(context, filePath, params, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String val) {
                Log.d("test", "onReceiveValue,val =" + val);
            }
        });
    }
}
