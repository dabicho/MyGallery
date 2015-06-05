package mx.org.dabicho.mygallery.util;


import android.content.Context;
import android.database.Cursor;

import android.provider.MediaStore;


import java.util.ArrayList;
import java.util.List;

import mx.org.dabicho.mygallery.model.GalleryType;
import mx.org.dabicho.mygallery.model.Image;


import static android.util.Log.d;

/**
 * DataLoader for a gallery images
 */
public class GalleryLoader extends DataLoader<List<Image>> {
    private static final String TAG = "GalleryLoader";
    private long mGalleryId;
    private GalleryType mGalleryType;
    private int mUpdateInterval;
    private GalleryLoaderUpdateCallbacks mUpdateCallbacks;

    private String[] galleryQueryProjection =
            {MediaStore.Images.ImageColumns._ID
                    , MediaStore.Images.ImageColumns.DATA


            };
    private String[] galleryQuerySelectionArgs;

    public GalleryLoader(Context context, long galleryId, GalleryType galleryType, int updateInterval
            , GalleryLoaderUpdateCallbacks updateCallbacks) {
        super(context);

        mGalleryId = galleryId;
        mGalleryType = galleryType;
        mUpdateInterval = updateInterval;
        mUpdateCallbacks = updateCallbacks;
        galleryQuerySelectionArgs = new String[]{String.valueOf(mGalleryId)};
    }


    @Override
    public List<Image> loadInBackground() {
        boolean stopLoading = false;
        d(TAG, "loadInBackground: loading gallery " + mGalleryType);
        Context context = getContext();
        ArrayList<Image> images = new ArrayList<Image>();
        Cursor lCursor = null;

        switch (mGalleryType) {
            case CONTENT_PROVIDER:
                // TODO orden
                lCursor = context.getContentResolver().query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        galleryQueryProjection, MediaStore.Images.ImageColumns.BUCKET_ID +
                                " = ? ", galleryQuerySelectionArgs,
                        MediaStore.Images.ImageColumns.DATA + " asc");
                d(TAG, "loadInBackground: " + lCursor.getCount());

                break;
            case ALBUM:

                break;
            case QUERY:

                break;
            default:
                // Una lista vacía

        }
        if (lCursor != null) {
            lCursor.moveToFirst();
            while (!lCursor.isAfterLast()) {
                Image image = new Image();
                image.setImageId(lCursor.getLong(0));
                image.setImageDataStream(lCursor.getString(1));


                //image.queryThumbnailDataStream(getContext().getContentResolver());
                //image.setThumbnailDataStream(lCursor.getString(2));


                lCursor.moveToNext();
                images.add(image);
                if (mUpdateInterval > 0 && mUpdateCallbacks != null &&
                        images.size() % mUpdateInterval == 0) {
                    stopLoading = mUpdateCallbacks.updateGallery(images);
                }

                if (stopLoading)
                    break;
            }
            lCursor.close();
        } else {
            d(TAG, "loadInBackground: there is no cursor");
        }
        return images;
    }
}

