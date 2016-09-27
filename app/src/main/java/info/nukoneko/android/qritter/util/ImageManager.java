package info.nukoneko.android.qritter.util;

import android.annotation.SuppressLint;
import android.content.Context;

import com.squareup.picasso.Picasso;

import info.nukoneko.android.qritter.util.tools.PicassoImage;

/**
 * Created by atsumi on 2016/09/27.
 */

public class ImageManager {
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    @SuppressLint("StaticFieldLeak")
    private static Picasso picasso;

    static public synchronized void Initialize(Context applicationContext) {
        context = applicationContext;
        picasso = PicassoImage.Builder(applicationContext);
    }

    static public synchronized Context getContext() {
        return context;
    }

    static public synchronized Picasso getPicasso() {
        return picasso;
    }

    private ImageManager() {
    }
}
