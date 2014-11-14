package mx.org.dabicho.mygallery.util;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import mx.org.dabicho.mygallery.model.GalleryType;
import mx.org.dabicho.mygallery.model.Image;

import static android.util.Log.i;

/**
 * Created by dabicho on 11/13/14.
 */
public class GalleryLoader extends DataLoader<List<Image>> {
    private static final String TAG = "GalleryLoader";
    private long mGalleryId;
    private GalleryType mGalleryType;

    private String[] galleryQueryProjection =
            {MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME
                    , MediaStore.Images.ImageColumns.DATA


            };
    private String[] galleryQuerySelectionArgs;

    public GalleryLoader(Context context, long galleryId, GalleryType galleryType) {
        super(context);
        mGalleryId = galleryId;
        mGalleryType = galleryType;
        galleryQuerySelectionArgs = new String[]{String.valueOf(mGalleryId)};
    }

    @Override
    public List<Image> loadInBackground() {
        i(TAG, "loadInBackground: loading gallery "+mGalleryType);
        Context context = getContext();
        ArrayList<Image> images = new ArrayList<Image>();
        Cursor lCursor=null;
        switch (mGalleryType) {
            case CONTENT_PROVIDER:
                // TODO orden
                lCursor = context.getContentResolver().query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        galleryQueryProjection, MediaStore.Images.ImageColumns.BUCKET_ID +
                                " = ? ", galleryQuerySelectionArgs,
                        MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME + " asc");
                i(TAG, "loadInBackground: "+lCursor.getCount());

                break;
            case ALBUM:
                break;
            case QUERY:
                break;
            default:
                // Una lista vacía
                return images;
        }
        lCursor.moveToFirst();
        while(!lCursor.isAfterLast()){
            Image image=new Image();
            image.setImageId(lCursor.getLong(0));
            image.setImageDataStream(lCursor.getString(1));
            //image.setThumbnailDataStream(lCursor.getString(2));

            i(TAG, "loadInBackground: imagen: " + image);
            lCursor.moveToNext();
            images.add(image);
        }
        return images;
    }
}
