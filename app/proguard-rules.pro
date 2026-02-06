# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep line number information for debugging stack traces.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ========================
# Project Classes
# ========================
-keep class com.neki.android.** { *; }
-keepclassmembers class com.neki.android.** { *; }

# ========================
# Kotlin
# ========================
-keep class kotlin.Metadata { *; }
-keepattributes RuntimeVisibleAnnotations
-keepattributes *Annotation*

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# ========================
# Ktor
# ========================
-keep class io.ktor.** { *; }
-keepclassmembers class io.ktor.** { *; }
-dontwarn io.ktor.**

# ========================
# kotlinx.serialization
# ========================
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers @kotlinx.serialization.Serializable class ** {
    *** Companion;
}
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1>$Companion {
    kotlinx.serialization.KSerializer serializer(...);
}
-if @kotlinx.serialization.Serializable class ** {
    static **$* *;
}
-keepclassmembers class <2>$<3> {
    kotlinx.serialization.KSerializer serializer(...);
}
-if @kotlinx.serialization.Serializable class ** {
    public static ** INSTANCE;
}
-keepclassmembers class <1> {
    public static <1> INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}
-keep,includedescriptorclasses class com.neki.android.**$$serializer { *; }
-keepclassmembers class com.neki.android.** {
    *** Companion;
}

# ========================
# Kakao SDK
# ========================
-keep class com.kakao.sdk.** { *; }
-keepclassmembers class com.kakao.sdk.** { *; }
-dontwarn com.kakao.sdk.**

# Kakao SDK enums (TokenNotFound 등)
-keepclassmembers enum com.kakao.sdk.** {
    public static **[] values();
    public static ** valueOf(java.lang.String);
    <fields>;
}

# ========================
# Hilt / Dagger
# ========================
-keep class dagger.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ComponentSupplier { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }
-keepclassmembers class * {
    @dagger.hilt.* <fields>;
    @dagger.hilt.* <methods>;
    @javax.inject.* <fields>;
    @javax.inject.* <methods>;
}
-dontwarn dagger.internal.codegen.**
-dontwarn dagger.hilt.internal.**

# ========================
# Android / Jetpack
# ========================
# Lifecycle
-keep class androidx.lifecycle.** { *; }
-keepclassmembers class * implements androidx.lifecycle.LifecycleObserver {
    <init>(...);
}

# Navigation
-keep class androidx.navigation.** { *; }

# Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# DataStore
-keep class androidx.datastore.** { *; }
-keepclassmembers class * extends com.google.protobuf.GeneratedMessageLite {
    <fields>;
}

# Paging
-keep class androidx.paging.** { *; }

# ========================
# Enums (General)
# ========================
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
    <fields>;
}

# ========================
# Timber
# ========================
-dontwarn org.jetbrains.annotations.**

# ========================
# OSS Licenses
# ========================
-keep class com.google.android.gms.oss.licenses.** { *; }

# ========================
# Coil
# ========================
-keep class coil3.** { *; }
-dontwarn coil3.**

# ========================
# Naver Maps
# ========================
-keep class com.naver.maps.** { *; }
-dontwarn com.naver.maps.**

# ========================
# OkHttp (used by Coil)
# ========================
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep class okio.** { *; }

# ========================
# ML Kit Barcode
# ========================
-keep class com.google.mlkit.** { *; }
-dontwarn com.google.mlkit.**

# ========================
# Play Services
# ========================
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

# ========================
# CameraX
# ========================
-keep class androidx.camera.** { *; }
-dontwarn androidx.camera.**

# ========================
# Missing class warnings suppression
# ========================
-dontwarn java.lang.invoke.StringConcatFactory
-dontwarn org.slf4j.**
-dontwarn org.bouncycastle.**
-dontwarn org.conscrypt.**
-dontwarn org.openjsse.**
