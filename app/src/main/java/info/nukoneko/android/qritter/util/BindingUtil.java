package info.nukoneko.android.qritter.util;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import java.util.Locale;

import twitter4j.Status;

public class BindingUtil {
    private static final String STATUS_FORMAT = "https://twitter.com/%s/status/%d";

    @BindingAdapter({"statusUrl"})
    public static void loadQR(ImageView view, Status status) {
        final String formattedText =
                String.format(Locale.getDefault(),
                        STATUS_FORMAT,
                        status.getUser().getScreenName(), status.getId());
        view.setImageBitmap(QRGenerator.create(formattedText));
    }
}
