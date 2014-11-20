package mx.org.dabicho.mygallery.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import static android.util.Log.e;

/**
 * Herramientas utilitarias para imágenes
 */
public class ImageUtils {
    private static final String TAG = "ImageUtils";

    public static int calculateMaxInSampleSize(BitmapFactory.Options options, int reqWidth,
                                               int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;

        int stretch_width = (int) Math.ceil((float) width / (float) reqWidth);
        int stretch_height = (int) Math.ceil((float) height / (float) reqHeight);

        if (stretch_width <= stretch_height)
            return stretch_width;
        else
            return stretch_height;

    }

    public static int calculateMinInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight, boolean rotate90) {
        if (rotate90)
            return calculateMinInSampleSize(options, reqHeight, reqWidth);
        else
            return calculateMinInSampleSize(options, reqWidth, reqHeight);
    }

    public static int calculateMinInSampleSize(BitmapFactory.Options options, int reqWidth,
                                               int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;

        int stretch_width = (int) Math.ceil((float) width / (float) reqWidth);
        int stretch_height = (int) Math.ceil((float) height / (float) reqHeight);

        if (stretch_width >= stretch_height)
            return stretch_width;
        else
            return stretch_height;

    }

    public static int calculateMaxInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight, boolean rotate90) {
        if (rotate90)
            return calculateMaxInSampleSize(options, reqHeight, reqWidth);
        else
            return calculateMaxInSampleSize(options, reqWidth, reqHeight);
    }


    public static Bitmap rotateBitmap(ExifInterface exif, Bitmap bitmap) {
        Matrix lMatrix = new Matrix();

        if(exif==null)
            return bitmap;

        switch (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                lMatrix.setRotate(90);

                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                lMatrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                lMatrix.setRotate(270);
                break;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                lMatrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                lMatrix.setScale(1, -1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                lMatrix.setScale(-1, 1);
                lMatrix.postRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                lMatrix.setRotate(90);
                lMatrix.postScale(-1, 1);
                break;
            default:
                return bitmap;
        }

        Bitmap orientedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), lMatrix, true);
        bitmap.recycle();
        return orientedBitmap;

    }


}
