package vip.ruoyun.webviewhelper.weber;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.smtt.sdk.TbsReaderView;
import com.tencent.smtt.sdk.WebView;

import java.io.File;

import vip.ruoyun.webkit.x5.WeBerHelper;

/**
 * Created by ruoyun on 2019-07-02.
 * Author:若云
 * Mail:zyhdvlp@gmail.com
 * Depiction:
 */
public class Test {


    public void test(Context context) {
        WeBerHelper.init(context);
    }


    //判断是否加载了 x5 内核
    public static void test(WebView webView) {
        webView.getX5WebViewExtension();
        //在没有⾃定义UA的情况下，使⽤您的app打开⽹⻚页，显示000000表示加载的是系统内核，显示⼤于零的数字表示加载了x5内核（该数字 是x5内核版本号）
        //http://soft.imtt.qq.com/browser/tes/feedback.html
    }

    /**
     * X5内核的一大特色就是可以在手机不安装office的情况下，可以只需下载插件即可浏览office文档
     * 这个类 感觉不太建议使用,应该使用下面的 QbSdk.canOpenFile 方法
     */
    private void displayFile(Context context, File mFile) {
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

    private void colse(TbsReaderView tbsReaderView) {
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
}


