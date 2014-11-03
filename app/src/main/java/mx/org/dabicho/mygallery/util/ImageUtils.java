package mx.org.dabicho.mygallery.util;

import android.graphics.BitmapFactory;

/**
 * Created by dabicho on 2/11/14.
 */
public class ImageUtils {

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth,
                                            int reqHeight){
        final int width=options.outWidth;
        final int height=options.outHeight;

        int inSampleSize=1;
        if(height>reqHeight||width>reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }

        }
        return inSampleSize;

    }
}
