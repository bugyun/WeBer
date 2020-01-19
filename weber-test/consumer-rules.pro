#重点坑：针对Android4.4，系统把openFileChooser方法去掉了，不混淆openFileChooser()
-keepclassmembers class * extends android.webkit.WebChromeClient{
   public void openFileChooser(...);
}

