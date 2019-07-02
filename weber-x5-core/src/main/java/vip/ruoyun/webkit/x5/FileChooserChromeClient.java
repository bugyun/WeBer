package vip.ruoyun.webkit.x5;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;

/**
 * Created by ruoyun on 2019-07-02.
 * Author:若云
 * Mail:zyhdvlp@gmail.com
 * Depiction:
 */
public class FileChooserChromeClient extends WebChromeClient {

    private ValueCallback<Uri> uploadFile;
    private ValueCallback<Uri[]> uploadFiles;
    private final static int FILE_CHOOSER_RESULT_CODE = 10000;

    // For Android < 3.0
    public void openFileChooser(ValueCallback<Uri> valueCallback) {
        uploadFile = valueCallback;
        openFileChooseProcess();
    }

    // For Android  >= 3.0
    public void openFileChooser(ValueCallback valueCallback, String acceptType) {
        uploadFile = valueCallback;
        openFileChooseProcess();
    }

    //For Android  >= 4.1
    public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
        uploadFile = valueCallback;
        openFileChooseProcess();
    }

    // For Android >= 5.0
    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        uploadFiles = filePathCallback;
        openFileChooseProcess();
        return true;
    }

    private void openFileChooseProcess() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("*/*");//所有文件
        i.setType("image/*");//图片
//        startActivityForResult(Intent.createChooser(i, "File Chooser"), FILE_CHOOSER_RESULT_CODE);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (null == uploadFile && null == uploadFiles)
                return;
            Uri result = data == null || resultCode != Activity.RESULT_OK ? null : data.getData();
            if (uploadFiles != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            } else if (uploadFile != null) {
                uploadFile.onReceiveValue(result);
                uploadFile = null;
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent) {
        if (requestCode != FILE_CHOOSER_RESULT_CODE || uploadFiles == null)
            return;
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (intent != null) {
                String dataString = intent.getDataString();
                ClipData clipData = intent.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }
                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        uploadFiles.onReceiveValue(results);
        uploadFiles = null;
    }


}
