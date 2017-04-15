package info.nukoneko.android.qritter;

import android.app.Application;

import io.realm.Realm;

public final class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}
