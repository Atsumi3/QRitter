#-- common config --#
-dontnote android.net.http.*
-dontnote org.apache.commons.codec.**
-dontnote org.apache.http.**

-dontwarn android.databinding.**

#-- RxJava RxAndroid --#
-dontwarn sun.misc.Unsafe
-keep class rx.schedulers.Schedulers {
    public static <methods>;
}
-keep class rx.schedulers.ImmediateScheduler {
    public <methods>;
}
-keep class rx.schedulers.TestScheduler {
    public <methods>;
}
-keep class rx.schedulers.Schedulers {
    public static ** test();
}

-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
   long producerNode;
   long consumerNode;
}

#-- twitter4j --#
-dontwarn twitter4j.**
-keep  class twitter4j.conf.PropertyConfigurationFactory
-keep class twitter4j.** { *; }

#-- zxing --#
-keep class !com.google.zxing.** { *; }

#-- okio --#
-dontwarn okio.**
