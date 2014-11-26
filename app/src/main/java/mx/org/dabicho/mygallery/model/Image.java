package mx.org.dabicho.mygallery.model;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;

import mx.org.dabicho.mygallery.util.ImageUtils;

import static android.util.Log.e;
import static android.util.Log.i;

/**
 * Created by dabicho on 11/13/14.
 */
public class Image {
    private static final String TAG = "Image";

    private long mImageId;
    private String mImageDataStream;
    private String mThumbnailDataStream;

    private String mDateTimeOriginal;
    private String gpsDateTime;
    private float[] mLatLong = new float[2];

    private double mAlt;

    private boolean exifLoaded = false;

    public long getImageId() {
        return mImageId;
    }

    public void setImageId(long imageId) {
        this.mImageId = imageId;
    }

    public String getImageDataStream() {
        return mImageDataStream;
    }

    public void setImageDataStream(String imageDataStream) {
        this.mImageDataStream = imageDataStream;
    }

    public String getThumbnailDataStream() {
        return mThumbnailDataStream;
    }

    public String queryThumbnailDataStream(ContentResolver contentResolver) {
        Cursor cursor = MediaStore.Images.Thumbnails.queryMiniThumbnail(contentResolver, mImageId,
                MediaStore.Images.Thumbnails.MINI_KIND, new String[]{MediaStore.Images.Thumbnails.DATA});
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            mThumbnailDataStream = cursor.getString(0);


        } else {
            mThumbnailDataStream = null;
        }
        cursor.close();

        return mThumbnailDataStream;

    }

    public Bitmap getThumbnail(ContentResolver contentResolver) {
        try {
            BitmapFactory.Options lOptions = new BitmapFactory.Options();
            Bitmap thumbnail = MediaStore.Images.Thumbnails.getThumbnail(contentResolver, mImageId,
                    MediaStore.Images.Thumbnails.MINI_KIND, lOptions);
            ExifInterface exif = new ExifInterface(mImageDataStream);
            return ImageUtils.rotateBitmap(exif, thumbnail);
        } catch (IOException e) {
            e(TAG, "getThumbnail: Could Not Open Image ", e);
            return null;
        }
    }

    public Bitmap getThumbnail() {

        try {
            if (mThumbnailDataStream != null) {
                ExifInterface exif = new ExifInterface(mImageDataStream);
                return ImageUtils.rotateBitmap(exif, BitmapFactory.decodeFile(mThumbnailDataStream));
            }
        } catch (IOException e) {
            e(TAG, "getThumbnail: Could not open file", e);
        }
        return null;
    }


    public void setThumbnailDataStream(String thumbnailDataStream) {
        this.mThumbnailDataStream = thumbnailDataStream;
    }

    @Override
    public String toString() {
        return "Image{" +
                "imageId=" + mImageId +
                ", imageDataStream='" + mImageDataStream + '\'' +
                ", thumbnailDataStream='" + mThumbnailDataStream + '\'' +
                '}';
    }


    public float getLat() {
        return mLatLong[0];
    }

    public void setLat(float lat) {
        mLatLong[0] = lat;
    }

    public float getLong() {
        return mLatLong[1];
    }

    public void setLong(float aLong) {
        mLatLong[1] = aLong;
    }

    public double getAlt() {
        return mAlt;
    }

    public void setAlt(double alt) {
        mAlt = alt;
    }

    public String getDateTimeOriginal() {
        i(TAG, "getDateTimeOriginal: "+mDateTimeOriginal);
        return mDateTimeOriginal;
    }

    public void setDateTimeOriginal(String dateTimeOriginal) {
        mDateTimeOriginal = dateTimeOriginal;
    }

    public ExifInterface loadExif() {
        try {
            ExifInterface exif = new ExifInterface(mImageDataStream);
            // This Tag posibly refers to exif DateTimeOriginal
            if (!exifLoaded) {
                mDateTimeOriginal = exif.getAttribute(ExifInterface.TAG_DATETIME);
                i(TAG, "loadExif: "+mDateTimeOriginal);

                exif.getLatLong(mLatLong);
                mAlt = exif.getAltitude(2000);
                exifLoaded = true;
            }
            return exif;
        } catch (IOException e) {
            e(TAG, "loadInBackground: Could not read exif data", e);
            return null;
        }
    }

    public void updateExif() {
        try {
            ExifInterface exif = new ExifInterface(mImageDataStream);
            // This Tag posibly refers to exif DateTimeOriginal

            mDateTimeOriginal = exif.getAttribute(ExifInterface.TAG_DATETIME);

            exif.getLatLong(mLatLong);
            mAlt = exif.getAltitude(2000);
            exifLoaded = true;


        } catch (IOException e) {
            e(TAG, "loadInBackground: Could not read exif data", e);

        }
    }

}
