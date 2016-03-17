package info.nukoneko.android.qritter.util.tools;

import android.content.Context;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by atsumi on 2016/03/17.
 */
public class PicassoImage {

    private static boolean isDebug = false;
    public static synchronized void setDebug(boolean debug){
        isDebug = debug;
    }

    public static Picasso Builder(Context context){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        if (isDebug){
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(interceptor);
        }

        return new Picasso.Builder(context)
                .downloader(new OkHttp3Downloader(builder.build()))
                .build();
    }
}

