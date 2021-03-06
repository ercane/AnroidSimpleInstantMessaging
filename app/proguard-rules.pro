# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/mree/Android/Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:
-keep class org.spongycastle.** { *; }
-keep class org.eclipse.paho.** { *; }
-keep class com.google.gson.** { *; }
-keep class com.fasterxml.** { *; }
-keep class org.w3c.** { *; }

-keep class messaging.mqtt.android.common.model.ConversationMessageInfo { *; }
-keep class messaging.mqtt.android.common.ref.ContentType { *; }
-keep class messaging.mqtt.android.common.ref.ConversationMessageStatus { *; }
-keep class messaging.mqtt.android.common.ref.ConversationMessageType { *; }
-dontwarn org.spongycastle.**
-dontwarn org.w3c.dom.bootstrap.DOMImplementationRegistry

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
