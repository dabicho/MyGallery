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

    public ExifInterface loadExif(){
        ExifInterface exifData=null;
        try {
            exifData = new ExifInterface(mImageDataStream);
            
        }catch (IOException e){
            e(TAG, "getExifData: Error looking for exif information for "+mImageDataStream,e );
        }
        return exifData;
    }
    

}
