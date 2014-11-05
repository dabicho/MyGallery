package mx.org.dabicho.mygallery.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

    /**
     * Este método abre la imágen source y realiza un recorte del centro que se asemeje al center
     * crop de ImageView
     *
     * @param source
     * @param width
     * @param height
     * @return
     */
    public synchronized static Bitmap cropImageToCenter(String source, int width, int height) {
        if(source == null || width <= 0 || height <= 0)
            return null;
        int x = 0;
        int y = 0;
        int dWidth;
        int dHeight;
        Bitmap lBitmap;
        Log.i(TAG, "cropImageToCenter: " + Thread.currentThread().getId() + " " + width + " x " + height);
        BitmapFactory.Options lOptions = new BitmapFactory.Options();
        lOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(source, lOptions);
        Log.i(TAG, "cropImageToCenter: " + Thread.currentThread().getId() + " " + lOptions.outWidth + "" +
                " x" + " " + lOptions.outHeight);
        lOptions.inSampleSize = calculateInSampleSize(lOptions, width, height);
        lOptions.inJustDecodeBounds = false;
        lBitmap = BitmapFactory.decodeFile(source, lOptions);
        Log.i(TAG, "cropImageToCenter: " + Thread.currentThread().getId() + " " + lBitmap.getWidth() +
                " x " +
                lBitmap.getHeight()+" "+lOptions.inSampleSize);
        dHeight = height * lBitmap.getWidth() / width;
        if(dHeight <= lBitmap.getHeight()) {
            dWidth = lBitmap.getWidth();
        } else {
            dHeight = lBitmap.getHeight();
            dWidth = height * lBitmap.getWidth() / width;
        }

        x = (lBitmap.getWidth() - dWidth) / 2;
        if(x < 0)
            x = 0;
        y = (lBitmap.getHeight() - dHeight) / 2;
        if(y < 0)
            y = 0;
        Bitmap croppedBitmap = Bitmap.createBitmap(lBitmap, x, y, dWidth, dHeight);
        lBitmap.recycle();
        lBitmap = null;
        System.gc();
        Log.i(TAG, "cropImageToCenter: " + Thread.currentThread().getId() + " " + croppedBitmap.getWidth()
                + " x " +
                "" + croppedBitmap
                .getHeight());
        return croppedBitmap;
    }
}
