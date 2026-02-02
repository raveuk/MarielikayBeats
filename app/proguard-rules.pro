# Add project specific ProGuard rules here.

# ============================================
# OBFUSCATION - Make decompilation harder
# ============================================

# Obfuscate all class names aggressively
-repackageclasses 'z'
-allowaccessmodification
-optimizationpasses 5

# Remove debug info
-renamesourcefileattribute ''
-keepattributes !SourceFile,!LineNumberTable

# Remove debug logs (keeps error/warn for crash debugging)
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# ============================================
# KEEP RULES
# ============================================

# Kotlin Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.example.kidztubeplayer.**$$serializer { *; }
-keepclassmembers class com.example.kidztubeplayer.** {
    *** Companion;
}
-keepclasseswithmembers class com.example.kidztubeplayer.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Koin
-keep class org.koin.** { *; }
-keep class org.koin.core.** { *; }
-keep class org.koin.dsl.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Media3 / ExoPlayer
-keep class androidx.media3.** { *; }
-keep interface androidx.media3.** { *; }
-dontwarn androidx.media3.**

# Ktor
-keep class io.ktor.** { *; }
-keep class kotlinx.coroutines.** { *; }
-dontwarn io.ktor.**
-dontwarn kotlinx.atomicfu.**
-dontwarn io.netty.**
-dontwarn com.typesafe.**
-dontwarn org.slf4j.**

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# Coil
-keep class coil3.** { *; }
-dontwarn coil3.**

# Keep data classes
-keep class com.example.kidztubeplayer.core.domain.model.** { *; }
-keep class com.example.kidztubeplayer.core.data.local.entity.** { *; }
-keep class com.example.kidztubeplayer.core.data.remote.** { *; }

# Keep ViewModels
-keep class * extends androidx.lifecycle.ViewModel { *; }

# Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# ============================================
# NEWPIPE EXTRACTOR
# ============================================

# NewPipe Extractor and its dependencies
-keep class org.schabi.newpipe.extractor.** { *; }
-dontwarn org.schabi.newpipe.extractor.**

# Rhino JavaScript engine (used by NewPipe for YouTube cipher)
-keep class org.mozilla.javascript.** { *; }
-dontwarn org.mozilla.javascript.**
-dontwarn java.beans.**
-dontwarn javax.script.**

# jsoup HTML parser
-keep class org.jsoup.** { *; }
-dontwarn org.jsoup.**
-dontwarn com.google.re2j.**

# Gson (used by NewPipe)
-keep class com.google.gson.** { *; }
-dontwarn com.google.gson.**
