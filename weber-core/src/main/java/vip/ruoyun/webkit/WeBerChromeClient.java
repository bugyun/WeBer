package vip.ruoyun.webkit;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import java.io.File;
import vip.ruoyun.helper.avoid.AvoidOnResultHelper;

/**
 * Created by ruoyun on 2019-07-02.
 * Author:若云
 * Mail:zyhdvlp@gmail.com
 * Depiction:
 */
public class WeBerChromeClient extends WebChromeClient implements AvoidOnResultHelper.ActivityCallback {

    private boolean isCapture;

    private File mVFile;

    private ValueCallback<Uri> uploadFile;

    private ValueCallback<Uri[]> uploadFiles;

    private FileChooserIntercept fileChooserIntercept;

    private FragmentActivity fragmentActivity;

    private Uri fileUri;

    public WeBerChromeClient(FragmentActivity fragmentActivity) {
        this.fragmentActivity = fragmentActivity;
    }

    public interface FileChooserIntercept {

        /**
         * @param isCapture  是否是照相功能
         * @param acceptType input标签 acceptType的属性
         * @param intent     意图
         * @return 是否要拦截
         */
        boolean onFileChooserIntercept(boolean isCapture, String[] acceptType, Intent intent);
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
    public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
        uploadFile = valueCallback;
        isCapture = !TextUtils.isEmpty(capture);
        if (TextUtils.isEmpty(acceptType)) {
            openFileChooseProcess(new String[]{"*/*"});
        } else {
            openFileChooseProcess(new String[]{acceptType});
        }
    }

    // For Android >= 5.0
    @RequiresApi(api = VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
            FileChooserParams fileChooserParams) {
        uploadFiles = filePathCallback;
        isCapture = fileChooserParams.isCaptureEnabled();
        if (fileChooserParams.getAcceptTypes() != null && fileChooserParams.getAcceptTypes().length > 0) {
            if (fileChooserParams.getAcceptTypes().length == 1 && TextUtils
                    .isEmpty(fileChooserParams.getAcceptTypes()[0])) {
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
        String acceptTypeString = typeBuilder.toString();
        try {
            Intent intent = new Intent();
            if (isCapture) {
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);// 启动系统相机
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                // data/data/[appName]/cache,必须确保文件夹路径存在，否则拍照后无法完成回调
                mVFile = new File(
                        fragmentActivity.getCacheDir() + File.separator + "weber",
                        System.currentTimeMillis() + ".jpg");
                if (!mVFile.getParentFile().exists()) {
                    mVFile.getParentFile().mkdirs();
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    fileUri = WeBerFileProvider
                            .getUriForFile(fragmentActivity,
                                    fragmentActivity.getPackageName() + "." + WeBer.authority,
                                    mVFile);
                } else {
                    fileUri = Uri.fromFile(mVFile);
                }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            } else {
                if (acceptTypeString.contains("video/")) {//默认打开后置摄像头
                    intent.setAction(MediaStore.ACTION_VIDEO_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } else {//图片或者文件
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType(acceptTypeString);
                    intent = Intent.createChooser(intent, "File Chooser");
                }
            }
            if (fileChooserIntercept != null) {
                if (fileChooserIntercept.onFileChooserIntercept(isCapture, acceptType, intent)) {
                    return;
                }
            }
            AvoidOnResultHelper.startActivityForResult(fragmentActivity, intent, this);
        } catch (Exception e) {//当系统没有相机应用的时候该应用会闪退,所以 try catch
            e.printStackTrace();
            //h5 的动作要有一致性, uploadFiles 赋值之后必须要有 onReceiveValue(),不然会影响其他功能
            if (uploadFiles != null) {
                uploadFiles.onReceiveValue(null);
            } else if (uploadFile != null) {
                uploadFile.onReceiveValue(null);
            }
            reset();
        }
    }

    @Override
    public void onActivityResult(int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            if (uploadFiles != null) {
                uploadFiles.onReceiveValue(null);
            } else if (uploadFile != null) {
                uploadFile.onReceiveValue(null);
            }
            reset();
            return;
        }
        if (null == uploadFile && null == uploadFiles) {
            reset();
            return;
        }
        Uri result = data == null ? null : data.getData();
        Uri[] uris = result == null ? null : new Uri[]{result};
        if (fileUri != null) {
//            afterOpenCamera();
            result = fileUri;
            uris = new Uri[]{fileUri};
        }
        if (uploadFiles != null) {
            uploadFiles.onReceiveValue(uris);
        } else if (uploadFile != null) {
            uploadFile.onReceiveValue(result);
        }
        reset();
    }

    /**
     * 解决拍照后在相册中找不到的问题
     */
    private void afterOpenCamera() {
        ContentValues values = new ContentValues(9);
        values.put(MediaStore.Images.Media.TITLE, "Camera");
        values.put(MediaStore.Images.Media.DISPLAY_NAME, mVFile.getName());
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.ORIENTATION, 0);
        values.put(MediaStore.Images.Media.DATA, mVFile.getAbsolutePath());
        values.put(MediaStore.Images.Media.SIZE, mVFile.length());
        Uri uri = fragmentActivity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        // 通知相册更新
        fragmentActivity.sendBroadcast(new Intent("com.android.camera.NEW_PICTURE", uri));
    }

    /**
     * 重置操作
     */
    private void reset() {
        uploadFiles = null;
        uploadFile = null;
        fileUri = null;
        mVFile = null;
        isCapture = false;
    }
}
