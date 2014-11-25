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
    private String mLocalDate;
    private String mUtcDate;
    private double mLat;
    private double mLong;
    private double mAlt;


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
            mThumbnailDataStream=null;
        }
        cursor.close();

        return mThumbnailDataStream;

    }

    public Bitmap getThumbnail(ContentResolver contentResolver){
        try {
            BitmapFactory.Options lOptions = new BitmapFactory.Options();
            Bitmap thumbnail = MediaStore.Images.Thumbnails.getThumbnail(contentResolver, mImageId,
                    MediaStore.Images.Thumbnails.MINI_KIND, lOptions);
            ExifInterface exif = new ExifInterface(mImageDataStream);
            return ImageUtils.rotateBitmap(exif, thumbnail);
        } catch(IOException e){
            e(TAG, "getThumbnail: Could Not Open Image ", e);
            return null;
        }
    }

    public Bitmap getThumbnail(){

        try {
            if (mThumbnailDataStream != null) {
                ExifInterface exif = new ExifInterface(mImageDataStream);
                return ImageUtils.rotateBitmap(exif,BitmapFactory.decodeFile(mThumbnailDataStream));
            }
        }catch (IOException e){
            e(TAG, "getThumbnail: Could not open file",e );
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



    public String getLocalDate() {
        return mLocalDate;
    }

    public void setLocalDate(String localDate) {
        mLocalDate = localDate;
    }

    public String getUtcDate() {
        return mUtcDate;
    }

    public void setUtcDate(String utcDate) {
        mUtcDate = utcDate;
    }

    public double getLat() {
        return mLat;
    }

    public void setLat(double lat) {
        mLat = lat;
    }

    public double getLong() {
        return mLong;
    }

    public void setLong(double aLong) {
        mLong = aLong;
    }

    public double getAlt() {
        return mAlt;
    }

    public void setAlt(double alt) {
        mAlt = alt;
    }

    public void loadExif(){
        try {
            ExifInterface exif=new ExifInterface(mImageDataStream);
            mUtcDate=exif.getAttribute(ExifInterface.TAG_GPS_TIMESTAMP);
            mLocalDate=exif.getAttribute(ExifInterface.TAG_GPS_DATESTAMP);

        }catch (IOException e){
            e(TAG, "loadInBackground: Could not read exif data",e );
        }
    }

}
