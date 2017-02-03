# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}


-dontshrink #不压缩输入的类文件
-dontoptimize #优化log不能有这个配置
-optimizationpasses 5
-dontusemixedcaseclassnames #混淆时不会产生形形色色的类名
-dontskipnonpubliclibraryclasses #指定不去忽略非公共的库类
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keepattributes Exceptions, Signature, InnerClasses
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

-dontwarn okhttp3.**
-dontwarn okio.**

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context);
}

-keep public class com.luoxudong.app.asynchttp.builder.** {
    *;
}

-keep public class com.luoxudong.app.asynchttp.callable.** {
    *;
}

-keep public class com.luoxudong.app.asynchttp.interceptor.** {
     *;
 }

-keep public class com.luoxudong.app.asynchttp.model.** {
    *;
}

-keep public class com.luoxudong.app.asynchttp.AsyncHttpUtil {
    *;
}

-keep public class com.luoxudong.app.asynchttp.ContentType {
    *;
}


