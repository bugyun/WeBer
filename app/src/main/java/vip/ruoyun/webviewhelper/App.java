package vip.ruoyun.webviewhelper;

import android.app.Application;

import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsListener;

import vip.ruoyun.webkit.x5.WeBerHelper;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        WeBerHelper.multiProcessOptimize();
//        WeBerHelper.init(this);

        WeBerHelper.init(this, new WeBerHelper.Interceptor() {
            @Override
            public void beforeInit() {//在初始化之前做一些配置
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
        });
//        WeBerHelper.init(this,new QbSdk.PreInitCallback(){
//
//            @Override
//            public void onCoreInitFinished() {
//
//            }
//
//            @Override
//            public void onViewInitFinished(boolean b) {
//
//            }
//        });
    }
}
