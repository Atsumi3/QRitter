package info.nukoneko.android.qritter.util.async;

import android.app.Activity;

/**
 * Created by atsumi on 2016/03/17.
 */

/***
 * 非同期通信のリスナ  onResult->MainThread
 * @param <T>
 */
abstract public class AsyncListener<T> {
    public abstract T doTask() throws Exception;
    public abstract void onResult(Activity context, T result);
    public void onError(Exception e){
        e.printStackTrace();
    }
}
