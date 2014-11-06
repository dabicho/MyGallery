package mx.org.dabicho.mygallery.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

/**
 * Created by dabicho on 2/11/14.
 */
public class ImageUtils {
    private static final String TAG = "ImageUtils";

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth,
                                            int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;

        int stretch_width = (int)Math.ceil((float)width / (float)reqWidth);
        int stretch_height = (int)Math.ceil((float)height / (float)reqHeight);

        if (stretch_width <= stretch_height)
            return stretch_width;
        else
            return stretch_height;

    }


}
