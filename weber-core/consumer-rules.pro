#重点坑：针对Android4.4，系统把openFileChooser方法去掉了，不混淆openFileChooser()
-keepclassmembers class * extends android.webkit.WeBerChromeClient{
   public void openFileChooser(...);
}

## js 调用方法
#保留annotation， 例如 @JavascriptInterface 等 annotation
-keepattributes *Annotation*

#保留跟 javascript相关的属性
-keepattributes JavascriptInterface

#保留JavascriptInterface中的方法
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}