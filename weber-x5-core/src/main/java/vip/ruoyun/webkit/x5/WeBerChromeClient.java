package vip.ruoyun.webkit.x5;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;

import vip.ruoyun.helper.avoid.AvoidOnResultHelper;

/**
 * Created by ruoyun on 2019-07-02.
 * Author:若云
 * Mail:zyhdvlp@gmail.com
 * Depiction:
 */
public class WeBerChromeClient extends WebChromeClient implements AvoidOnResultHelper.ActivityCallback {

    private ValueCallback<Uri> uploadFile;
    private ValueCallback<Uri[]> uploadFiles;
    private FileChooserIntercept fileChooserIntercept;
    private FragmentActivity fragmentActivity;

    public WeBerChromeClient(FragmentActivity fragmentActivity) {
        this.fragmentActivity = fragmentActivity;
    }

    public interface FileChooserIntercept {
        void onFileChooserIntercept(Intent intent);
    }

    public void setFileChooserIntercept(FileChooserIntercept fileChooserIntercept) {
        this.fileChooserIntercept = fileChooserIntercept;
    }

    // For Android < 3.0
    public void openFileChooser(ValueCallback<Uri> valueCallback) {
        uploadFile = valueCallback;
        openFileChooseProcess(new String[]{"*/*"});
    }

    // For Android  >= 3.0
    public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType) {
        uploadFile = valueCallback;
        if (TextUtils.isEmpty(acceptType)) {
            openFileChooseProcess(new String[]{"*/*"});
        } else {
            openFileChooseProcess(new String[]{acceptType});
        }
    }

    //For Android  >= 4.1
    @Override
    public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
        uploadFile = valueCallback;
        if (TextUtils.isEmpty(acceptType)) {
            openFileChooseProcess(new String[]{"*/*"});
        } else {
            openFileChooseProcess(new String[]{acceptType});
        }
    }

    // For Android >= 5.0
    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        uploadFiles = filePathCallback;
        if (fileChooserParams.getAcceptTypes() != null && fileChooserParams.getAcceptTypes().length > 0) {
            if (fileChooserParams.getAcceptTypes().length == 1 && TextUtils.isEmpty(fileChooserParams.getAcceptTypes()[0])) {
                openFileChooseProcess(new String[]{"*/*"});
            } else {
                openFileChooseProcess(fileChooserParams.getAcceptTypes());
            }
        } else {
            openFileChooseProcess(new String[]{"*/*"});
        }
        return true;
    }

    /**
     * type 类型 :多个 "video/;image/" 单个 "image/*"
     * intent.setType(“video/;image/”);//同时选择视频和图片
     * i.setType("image/*");//图片
     *
     * @param acceptType 类型
     */
    private void openFileChooseProcess(String[] acceptType) {
        StringBuilder typeBuilder = new StringBuilder();
        for (int i = 0; i < acceptType.length; i++) {
            typeBuilder.append(acceptType[i]);
            if (i < acceptType.length - 1) {
                typeBuilder.append(";");
            }
        }
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType(typeBuilder.toString());
        Intent intent = Intent.createChooser(i, "File Chooser");
        if (fileChooserIntercept != null) {
            fileChooserIntercept.onFileChooserIntercept(intent);
        }
        AvoidOnResultHelper.startActivityForResult(fragmentActivity, intent, this);
    }

    @Override
    public void onActivityResult(int resultCode, Intent data) {
        if (null == uploadFile && null == uploadFiles)
            return;
        Uri result = data == null || resultCode != Activity.RESULT_OK ? null : data.getData();
        if (uploadFiles != null) {
            onActivityResultAboveL(resultCode, data);
        } else if (uploadFile != null) {
            uploadFile.onReceiveValue(result);
            uploadFile = null;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int resultCode, Intent intent) {
        if (uploadFiles == null)
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
