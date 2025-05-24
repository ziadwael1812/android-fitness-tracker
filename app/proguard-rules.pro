# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in ${sdk.dir}/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep rules here:

# If you use an HTTP client library, you may need to adapt this rule to your library:
#-keepattributes Signature
#-keepattributes *Annotation*
#-keep class com.google.gson.examples.android.model.** { *; }

# Retain generic types for Room.
-keepattributes Signature

# Hilt uses generated code and reflection.
-keepclassmembers,allowshrinking,allowobfuscation class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
-keepclassmembers,allowshrinking,allowobfuscation class * extends androidx.hilt.lifecycle.ViewModelInject {
    <init>(...);
}

# For Kotlin Coroutines
-dontwarn kotlinx.coroutines.**

# If you are using Retrofit with R8, add the following rules:
#-keepclassmembers interface * {
#    @retrofit2.http.*
#    <methods>;
#}
#-keep class retrofit2.Response
#-keep class kotlin.coroutines.Continuation
