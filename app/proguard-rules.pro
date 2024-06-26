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

-dontobfuscate
#-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*,!code/allocation/variable
#-keepattributes *Annotation*

# Crashlytics
-keep class com.google.firebase.crashlytics.** { *; }
-keep class com.crashlytics.android.** { *; }

# Crashlytics NDK
-keep class com.google.firebase.crashlytics.ndk.** { *; }
-keep class com.crashlytics.sdk.ndk.** { *; }

# Firebase Performance Monitoring
-keep class com.google.firebase.perf.** { *; }
-keep class com.google.firebase.perf.metrics.** { *; }

# Google Analytics
-keep class com.google.analytics.** { *; }
-keep class com.google.android.gms.analytics.** { *; }

# Google Analytics para Firebase
-keep class com.google.firebase.analytics.** { *; }

-keep class retrofit2.** { *; }
-keep interface retrofit2.** { *; }

-keep class com.alefmoreira.citytraveltracker.remote.responses.MatrixAPI.** { *; }