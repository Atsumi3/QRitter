package info.nukoneko.android.qritter.util;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import info.nukoneko.android.qritter.util.tools.QRGenerator;
import twitter4j.Status;

/**
 * Created by atsumi on 2016/03/17.
 */
public class BindingUtil {
    @BindingAdapter({"bind:statusUrl"})
    public static void loadQR(ImageView view, Status status){
        view.setImageBitmap(QRGenerator.create(String.format("https://twitter.com/%s/status/%d", status.getUser().getScreenName(), status.getId()), 200));
    }
}
