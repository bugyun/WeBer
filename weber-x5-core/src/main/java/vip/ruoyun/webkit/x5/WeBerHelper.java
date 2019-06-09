package vip.ruoyun.webkit.x5;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsListener;
import com.tencent.smtt.sdk.TbsReaderView;
import com.tencent.smtt.sdk.ValueCallback;

import java.io.File;
import java.util.HashMap;

public class WeBerHelper {

    public static void init(Context context) {
        QbSdk.setDownloadWithoutWifi(true);
        QbSdk.PreInitCallback initCallback = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean isSuccess) {//x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.d("app", " onViewInitFinished is " + isSuccess);
                if (!isSuccess) { //如果onViewInitFinished false,内核会尝试安装，你可以通过下面监听接口获知
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
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(context.getApplicationContext(), initCallback);
    }

    /**
     * 播放视频的调用接口
     * 通过TbsVideo的静态方法，如下：
     * canUseTbsPlayer(context) : 判断当前Tbs播放器是否已经可以使用
     * openVideo(context,videoUrl) : 直接调用播放接口，传入视频流的url
     * openVideo(context,videoUrl,extraData) : extraData对象是根据定制需要传入约定的信息，没有需要可以传如null
     * screenMode  102 来实现默认全屏+控制栏等UI
     */
    public static void testPlay() {

    }


    /**
     * X5内核的一大特色就是可以在手机不安装office的情况下，可以只需下载插件即可浏览office文档
     * 这个类 感觉不太建议使用,应该使用下面的 QbSdk.canOpenFile 方法
     */
    public void displayFile(Context context, File mFile) {
        if (mFile != null && !TextUtils.isEmpty(mFile.toString())) {
            //增加下面一句解决没有TbsReaderTemp文件夹存在导致加载文件失败
            String bsReaderTemp = Environment.getExternalStorageDirectory() + "/" + "TbsReaderTemp";
            File bsReaderTempFile = new File(bsReaderTemp);

            if (!bsReaderTempFile.exists()) {
                Log.d("", "准备创建/storage/emulated/0/TbsReaderTemp！！");
                boolean mkdir = bsReaderTempFile.mkdir();
                if (!mkdir) {
                    Log.e("", "创建/storage/emulated/0/TbsReaderTemp失败！！！！！");
                }
            }
            TbsReaderView tbsReaderView = new TbsReaderView(context, new TbsReaderView.ReaderCallback() {
                @Override
                public void onCallBackAction(Integer integer, Object o, Object o1) {

                }
            });
            Bundle bundle = new Bundle();
            String filePath = "";
            bundle.putString(TbsReaderView.KEY_FILE_PATH, filePath);//传递文件路径
            bundle.putString(TbsReaderView.KEY_TEMP_PATH, bsReaderTemp);//加载插件保存的路径
            boolean result = tbsReaderView.preOpen(getFileType(filePath), false);//判断是否支持打开此类型的文件
            if (result) {
                tbsReaderView.openFile(bundle);
            }
        }
    }

    public void colse(TbsReaderView tbsReaderView) {
        tbsReaderView.onStop();
    }

    /***
     * 获取文件类型
     *
     * @param paramString
     * @return
     */
    private String getFileType(String paramString) {
        String str = "";

        if (TextUtils.isEmpty(paramString)) {
            Log.d("", "paramString---->null");
            return str;
        }
        Log.d("", "paramString:" + paramString);
        int i = paramString.lastIndexOf('.');
        if (i <= -1) {
            Log.d("", "i <= -1");
            return str;
        }
        str = paramString.substring(i + 1);
        Log.d("", "paramString.substring(i + 1)------>" + str);
        return str;
    }

    /**
     * 使用要求: android.permission.WRITE_EXTERNAL_STORAGE 权限
     * context：调起 miniqb 的 Activity 的 context。此参数只能是 activity 类型的 context，不能设置为 Application 的 context。
     * filePath：文件路径。格式为 android 本地存储路径格式，例如：/sdcard/Download/xxx.doc. 不支持 file:/// 格式。暂不支持在线文件。
     * extraParams：miniqb 的扩展功能。为非必填项，可传入 null 使用默认设置。
     * - local: “true”表示是进入文件查看器，如果不设置或设置为“false”，则进入 miniqb 浏览器模式。不是必 须设置项。
     * - style: “0”表示文件查看器使用默认的 UI 样式。“1”表示文件查看器使用微信的 UI 样式。不设置此 key 或设置错误值，则为默认 UI 样式。
     * - topBarBgColor: 定制文件查看器的顶部栏背景色。格式为“#xxxxxx”，例“#2CFC47”;不设置此 key 或设置 错误值，则为默认 UI 样式。
     * - menuData: 该参数用来定制文件右上角弹出菜单，可传入菜单项的 icon 的文本，用户点击菜单项后，sdk 会通过 startActivity+intent 的方式回调。menuData 是 jsonObject 类型，
     * ValueCallback：提供 miniqb 打开/关闭时给调用方回调通知,以便应用层做相应处理。 在单独进程打开文件的场景中，回调参数出现如下字符时，表示可以关闭当前进程，避免内存占用。
     * - 1：用 QQ 浏览器打开
     * - 2：用 MiniQB 打开
     * - 3：调起阅读器弹框
     * - -1：filePath 为空 打开失败
     */
    public static void openFile(Context context, final android.webkit.ValueCallback<Boolean> valueCallback) {
        QbSdk.canOpenFile(context, "", new ValueCallback<Boolean>() {
            @Override
            public void onReceiveValue(Boolean isCanOpen) {
                valueCallback.onReceiveValue(isCanOpen);
                if (isCanOpen) {
                    Log.d("test", "文件可以打开");
                } else {
                    Log.d("test", "不支持此类型文件");
                }
            }
        });
        HashMap<String, String> params = new HashMap<>();
        QbSdk.openFileReader(context, "/sdcard/xxx.doc", params, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String val) {
                Log.d("test", "onReceiveValue,val =" + val);
            }
        });

    }
}
