# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Сохраняем конкретный класс TrackParcel
-keep class com.practicum.playlistmaker.common.presentation.model.TrackParcel { *; }

# Сохранить базовые компоненты Android
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
#-keep public class * extends android.app.Service
#-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.view.View

-keep class * extends androidx.viewbinding.ViewBinding {
    public static * inflate(android.view.LayoutInflater);
}

# Сохранить аннотации и параметры generics
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes *Annotation*, Exceptions

# SplashScreen
#-keep class androidx.core.splashscreen.** { *; }

# Preference
#-keep class androidx.preference.** { *; }

# ViewPager2
-keep class androidx.viewpager2.** { *; }

# Navigation Component
-keep class androidx.navigation.** { *; }
-keepclassmembers class * implements androidx.navigation.NavArgs {
    public <init>(...);
}

# Lifecycle + ViewModel
-keep class androidx.lifecycle.** { *; }
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# Room Database
-keep class * extends androidx.room.RoomDatabase
-keep class * implements androidx.room.Entity { *; }
-keep class * extends androidx.room.TypeConverter { *; }
-keepclassmembers class * {
    @androidx.room.* <methods>;
}

# Retrofit
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# GSON
-keep class com.google.gson.** { *; }
-keep class com.google.gson.stream.** { *; }
-keep class com.practicum.playlistmaker.search.domain.model.** { *; }
-keep class com.practicum.playlistmaker.search.data.model.** { *; }

# Media3 (ExoPlayer)
#-keep class androidx.media3.** { *; }
#-keep class com.google.android.exoplayer2.** { *; }
#-dontwarn com.google.android.exoplayer2.**

# Koin (DI)
-keep class org.koin.** { *; }
-keepclassmembers class * {
    @org.koin.core.annotation.* <methods>;
}

# Glide
#-keep public class * implements com.bumptech.glide.module.GlideModule
#-keep class com.bumptech.glide.** { *; }
#-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
#    **[] $VALUES;
#    public *;
#}

# Timber
#-keep class timber.log.** { *; }
#-keepclassmembers class * extends timber.log.Timber {
#    <init>(...);
#}

# Coroutines
-keep class kotlinx.coroutines.** { *; }
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.coroutines.internal.MainDispatcherFactory { *; }
-keepclassmembernames class kotlin.** { *; }

# Parcelize
-keep class * implements android.os.Parcelable {
    public static final ** CREATOR;
}
