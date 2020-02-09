# WeBer
Android 内核 WebView 的 Helper
完美兼容 AndroidX 和 android 库，欢迎使用~~~

## 特有功能
支持 h5 中 input 标签，标准 Android WebView 不支持此标签并且只支持单文件操作。
- 打开所有文件 `<input capture="*/*"/>`
- 打开照相机 `<input capture="camera"/>` 优先级大于 accept
- 打开摄像机 `<input accept="video/*"/>`
- 打开图片 `<input accept="image/*"/>`
- 等等相关文件操作

# 使用方法

## 添加依赖

jcenter()仓库,在子项目中的 build.gradle 文件中添加

```java
dependencies {
    implementation 'vip.ruoyun.webkit:weber-core:1.0.0'
}
```

## 使用

### Application中进行初始化
```java
WeBer.with()
        .interceptor(new WeBer.Interceptor() { //在初始化之前做一些配置
            @Override
            public void beforeInit(final Context context) {

            }
        })
        //配置 android:authorities => "${applicationId}."+"${authority}"
        //下面会生成  => android:authorities="${applicationId}.provider"
        .authority("provider")
        .build(this);
```

## WeBerChromeClient 支持 input 标签

需要继承 WeBerChromeClient,可以添加文件的监听器。

当 h5 有input 标签的时候，响应事件。可以不设置。
```html
<input class="filechooser" id="file_chooser" type="file" placeholder="file path">
```
### input 标签相关配置

- ```multiple="multiple" : 只能支持单文件，所以设置multiple无效```
- ```accept="video/*" : 打开摄像机功能```
- ```capture="camera" : 如果有值的话，就会调用照相机功能，优先级大于 accept```
- ```accept="image/*" : 选择文件,根据设置 image/*图片, */* 所有文件```

### onActivityResult
不需要在 onActivityResult 事件中添加回调,使用 https://github.com/bugyun/AvoidOnResultHelper 优化回调.


### WeBerChromeClient.setFileChooserIntercept() 拦截器

如果直接使用 input 标签来打开具体对应的功能,是需要 Android 手机对应的权限的.可以通过下面的方法添加拦截器，true 表示拦截此次打开（相机/文件/摄像机）的请求来判断是否炫需要权限。

权限库推荐使用 https://github.com/bugyun/MissPermission 来进行权限的检查和请求。

可以通过判断 intent 的 getAction() 来进行具体权限的检查,然后请求对应的权限。代码如下


```java
class TestWeBerChromeClient extends WeBerChromeClient {
    ...
}

TestWeBerChromeClient  chromeClient = new TestWeBerChromeClient(this);

//可选操作
chromeClient.setFileChooserIntercept(new WeBerChromeClient.FileChooserIntercept() {
    /**
     * @param isCapture  是否是照相功能
     * @param acceptType input标签 acceptType的属性
     * @param intent     意图
     * @return 是否要拦截, 可根据 intent 来判断是否要进行照相机权限检查,代码如下
     */
    @Override
    public boolean onFileChooserIntercept(boolean isCapture,String[] acceptType, Intent intent) {
        if (MediaStore.ACTION_VIDEO_CAPTURE.equals(intent.getAction())) {//要使用摄像机
            //要使用摄像机,判断权限 android.permission.CAMERA
            //可以使用 https://github.com/bugyun/MissPermission ,来进行权限的请求.
            return true;//拦截
        } else if (MediaStore.ACTION_IMAGE_CAPTURE.equals(intent.getAction())) {//要使用照相机
            //要使用照相机,判断权限 android.permission.CAMERA
            //可以使用 https://github.com/bugyun/MissPermission ,来进行权限的请求.
            return true;//拦截
        }
        return false;//不拦截
    }
});
```

## 混淆

内部已经内置了混淆，所以不需要添加任何混淆


