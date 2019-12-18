package vip.ruoyun.webviewhelper;

import android.app.Application;
import android.content.Context;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.QbSdk.PreInitCallback;
import com.tencent.smtt.sdk.TbsListener;
import vip.ruoyun.webkit.x5.WeBer;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

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
                .preInitCallBack(new PreInitCallback() {
                    @Override
                    public void onCoreInitFinished() {

                    }

                    @Override
                    public void onViewInitFinished(final boolean b) {

                    }
                })
                .build(this);
    }
}
