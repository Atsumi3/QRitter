package info.nukoneko.android.qritter.util;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.Hashtable;

final class QRGenerator {
    private static final int IMAGE_SIZE = 200;
    private static final Hashtable<EncodeHintType, ErrorCorrectionLevel> HINTS =
            new Hashtable<EncodeHintType, ErrorCorrectionLevel>() {{
                put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            }};
    private static final QRCodeWriter mWriter = new QRCodeWriter();

    @Nullable
    static Bitmap create(String contents) {
        try {
            return getBitmapFromMatrix(mWriter.encode(contents, BarcodeFormat.QR_CODE, IMAGE_SIZE, IMAGE_SIZE, HINTS));
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    private static Bitmap getBitmapFromMatrix(@NonNull final BitMatrix matrix) {
        final int width = matrix.getWidth();
        final int height = matrix.getHeight();
        final Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bmp.setPixel(x, y, matrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bmp;
    }
}
